package su.msu.cs.lvk.accorute.taskmanager;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 01.04.2010
 * Time: 23:30:08
 * To change this template use File | Settings | File Templates.
 */
public class TaskManager implements Comparator<Task>, Runnable {
    
    public int compare(Task t1, Task t2){
        int p1 = taskPriority.get(t1);
        int p2 = taskPriority.get(t2);
        if(p1==p2)
            return taskID.get(t1) - taskID.get(t2);
        else return p1-p2;
    }

    final private Queue<Task> pendingTasks = new PriorityQueue<Task>(10,this);
    final private HashSet<Task> runningTasks = new HashSet<Task>();
    final private HashMap<Task, Task> waitingTasks = new HashMap<Task, Task>(); //TODO:poor naming
    final private ThreadPoolExecutor executor = new  ThreadPoolExecutor(10000,10000,2,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    final private HashMap<Task, Integer> taskPriority = new HashMap<Task, Integer>();
    private int nextID = 0;
    final private HashMap<Task, Integer> taskID = new HashMap<Task, Integer>();
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public enum TaskManagerStatus {
        NOT_STARTED, RUNNING, PAUSED
    }
    private boolean isTerminating = false;
    private TaskManagerStatus status = TaskManagerStatus.NOT_STARTED;

    public TaskManagerStatus getStatus(){
        return status;
    }
    synchronized private void setStatus(TaskManagerStatus stat){
        status = stat;
    }

    TaskManager() {
        logger.trace("Task manager created");
    }
    synchronized public void terminate(){
        isTerminating = true;
        logger.trace("Terminating task manager");
        notify();
    }

    
    synchronized public boolean addTask(Task pending){
        if(!isTerminating){
            logger.trace("Task added");
            taskPriority.put(pending,2); // normal task
            taskID.put(pending,nextID);
            nextID++;
            pendingTasks.add(pending);
            notify();//wake up the running cycle
            return true;
        }
        return false;
    }
    synchronized public void taskFinished(){
        logger.trace("Task was finished");
        notify();//wake up the running cycle    
    }

    synchronized public void addWaitedTask(Task waiting, Task pending){
        logger.trace("Waited task added");
        if( (pending instanceof SerialTask) || (waiting instanceof  SerialTask) ){
            throw new IllegalArgumentException("You can only wait from a non-serial task, and only for a non-serial task!!!");
        }
        taskPriority.put(pending,1); // waited task
        pendingTasks.add(pending);
        waitingTasks.put(waiting,pending);
        notify();//wake up the running cycle
    }
    synchronized public void resume(){
        logger.trace("Resumed");
        setStatus(TaskManagerStatus.RUNNING);
        notify();
    }
    synchronized public void pause(){
        logger.trace("Paused");
        setStatus(TaskManagerStatus.PAUSED);
        notify();
    }
    synchronized public void run(){
        logger.trace("Stared to run");
        //reentrant synchronization rulez!!!
        if(getStatus() != TaskManagerStatus.NOT_STARTED){
            logger.error("Tried to call run twice!");
            return;//onotole negodue!
        }
        while(true){
            logger.trace("Next run iteration; " + runningTasks.size() + " running, " + pendingTasks.size() + " pending");
            while (getStatus() == TaskManagerStatus.PAUSED){
                try{
                    wait();
                }catch (InterruptedException e){}
            }
            //get all finished tasks...
            HashSet<Task> finishedTasks = new HashSet<Task>();
            for(Task t : runningTasks){
                if(t.getStatus() == Task.TaskStatus.FINISHED){
                    finishedTasks.add(t);
                }
            }
            logger.trace(finishedTasks.size() + " tasks finished");
            //...and remove them from running 
            for(Task t : finishedTasks){
                runningTasks.remove(t);
                logger.trace("removed task from running");
            }
            boolean serialRunning = false;
            //...then resume tasks that can be unblocked
            for(Task t : runningTasks){
                if(t.getStatus() == Task.TaskStatus.BLOCKED){
                    if(finishedTasks.contains(waitingTasks.get(t))){
                        logger.trace("Unblock blocked task");
                        t.resume();                                                 
                    }
                }
                if(t instanceof SerialTask){
                    //if the serial task is running right now, we shouldn't go any further
                    serialRunning = true;
                }
            }

            if(! serialRunning){
                //OK, here we go...
                Task task = pendingTasks.peek();
                if(task != null){
                    if((task instanceof SerialTask) && runningTasks.isEmpty()){
                        task = pendingTasks.poll();
                        logger.trace("Execute new serial task");
                        runningTasks.add(task);
                        executor.execute(task);
                    }else{
                        //execute all asynchronous tasks...
                        while(! (task instanceof SerialTask) && task != null){
                            task = pendingTasks.poll();
                            logger.trace("Execute new task");
                            runningTasks.add(task);
                            executor.execute(task);
                            task = pendingTasks.peek();
                        }
                    }
                }
            }
            if(isTerminating && (runningTasks.size() + pendingTasks.size() == 0) ){
                executor.shutdown();
                boolean finished = false;
                while(!finished){
                    try{
                        finished = executor.awaitTermination(1,TimeUnit.MILLISECONDS); 
                    }catch (InterruptedException iex){}
                }
                logger.debug("All tasks are finished, switching back to not_started. " +
                        "Active: " + executor.getActiveCount() +
                        ". Completed: " + executor.getCompletedTaskCount()+
                        ". Overall: " + executor.getTaskCount());
                setStatus(TaskManagerStatus.NOT_STARTED);
                isTerminating = false;
                return;
            }
            try{
                logger.trace("Sleep until next action...");
                wait();
            }catch (InterruptedException e){}
        }
    }
    
    public static void main(String[] args){
        ApplicationContext ctx;
        try {
            ctx = new ClassPathXmlApplicationContext(
                    (String[])ArrayUtils.addAll(new String[] {"accorute-config.xml"},
                                    new String[0])
                    );
        } catch (BeanDefinitionStoreException ex) {
            System.err.println("Error loading evaluation contexts: " + ex.getMessage());
            return;
        }
        Logger logger = Logger.getLogger(TaskManager.class.getName());
        logger.debug("Started!");
        TaskManager t = new TaskManager();
        t.addTask(new JoinTask(t,"FINISHED 1 part!"));
        t.addTask(new JoinTask(t,"FINISHED 2 part!"));
        t.terminate();
        t.run();
    }
}
