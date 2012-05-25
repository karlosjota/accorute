package su.msu.cs.lvk.accorute.taskmanager;

import com.truchsess.util.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import su.msu.cs.lvk.accorute.tasks.TaskDeletedForGC;
import su.msu.cs.lvk.accorute.utils.Callback0;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 01.04.2010
 * Time: 23:30:08
 * To change this template use File | Settings | File Templates.
 */
public class TaskManager implements Comparator<Task>, Runnable, RejectedExecutionHandler {
    ArrayListTree<Object> taskTree = new ArrayListTree<Object>();
    ArrayList<Callback0> taskTreeChangeCallbacks = new ArrayList<Callback0>();
    
    public void addTaskTreeChangeListener(Callback0 cb){
        taskTreeChangeCallbacks.add(cb);
    }
    public void removeTaskTreeChangeListener(Callback0 cb){
        taskTreeChangeCallbacks.remove(cb);
    }
    private void invokeTaskTreeChangeListeners(){
        for(Callback0 cb : taskTreeChangeCallbacks){
            cb.CallMeBack();
        }
    }
    public void cleanTree(){
        synchronized (taskTree){
            final TaskManager theTaskManager = this;
            final Map<TreeCursor, List<Object>> nodesToDeleteByParent = new HashMap<TreeCursor, List<Object>>();
            try {
                walkTree(new TreeWalker.Walk<Object>() {
                    private void process(TreeCursor<Object> objectTreeCursor) throws TreeWalker.AbortProcessingException {
                        if(objectTreeCursor.getElement() instanceof Task){
                            MutableTreeCursor<Object> theCursor = (MutableTreeCursor<Object>) objectTreeCursor;
                            if(((Task)theCursor.getElement()).getStatus() == Task.TaskStatus.FINISHED){
                                String s = theCursor.getElement().getClass().getSimpleName() + " " +theCursor.getElement().toString();
                                theCursor.setElement(new TaskDeletedForGC(theTaskManager, s));
                            }
                        }
                    }
                    public void doDown(TreeCursor<Object> objectTreeCursor) throws TreeWalker.AbortProcessingException {
                        process(objectTreeCursor);
                    }

                    public void doUp(TreeCursor<Object> objectTreeCursor) throws TreeWalker.AbortProcessingException {
                        //process(objectTreeCursor);
                    }

                    public void doNext(TreeCursor<Object> objectTreeCursor) throws TreeWalker.AbortProcessingException {
                        process(objectTreeCursor);
                    }
                });
            } catch (TreeWalker.AbortProcessingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        invokeTaskTreeChangeListeners();

    }
    public String toString(){
        return "Task Manager " + getStatus().toString() + ": " + runningTasks.size() + " running, " + pendingTasks.size() + " pending";
    }
    synchronized public int compare(Task t1, Task t2){
        int p1 = taskPriority.get(t1);
        int p2 = taskPriority.get(t2);
        if(p1==p2)
            return taskID.get(t1) - taskID.get(t2);
        else return p1-p2;
    }
    final int maxThreads = 5000;
    final private Queue<Task> pendingTasks = new PriorityQueue<Task>(100,this);
    final private HashSet<Task> runningTasks = new HashSet<Task>();
    final private ThreadPoolExecutor executor = new  ThreadPoolExecutor(20, maxThreads,2,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    final private HashMap<Task, Integer> taskPriority = new HashMap<Task, Integer>();
    private int nextID = 0;
    final private HashMap<Task, Integer> taskID = new HashMap<Task, Integer>();
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        throw new RejectedExecutionException();
    }

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
    public void walkTree(TreeWalker.Walk<Object> walk)  throws TreeWalker.AbortProcessingException{
        TreeWalkerBase<Object> walker = new TreeWalkerBase<Object>();
        walker.doWalk(taskTree, walk);
    }
    TaskManager() {
        logger.trace("Task manager created");
        taskTree.getCursor().addChild(this);
    }
    synchronized public void terminate(){
        isTerminating = true;
        logger.trace("Terminating task manager");
        invokeTaskTreeChangeListeners();
        notifyAll();
    }

    synchronized public void waitForEmptyQueue(){
        while (pendingTasks.size() + runningTasks.size() != 0){
            try{
                wait();
            }catch(InterruptedException ex){}
        }
    }
    synchronized public void waitForFinish(){
        if(!isTerminating){
            logger.error("Cannot waitForFinish whilst not terminating! Call terminate() first.");
            return;
        }
        while (getStatus() != TaskManagerStatus.NOT_STARTED  || isTerminating ){
            try{
                wait();
            }catch(InterruptedException ex){}
        }
    }
    synchronized public boolean addTask(Task pending){
        if(!isTerminating){
            logger.trace("Task added");
            taskPriority.put(pending,2); // normal task
            taskID.put(pending,nextID);
            nextID++;
            pendingTasks.add(pending);
            MutableListTreeCursor<Object> cur = taskTree.getCursor();
            cur.down();
            cur.addChild(pending);
            invokeTaskTreeChangeListeners();
            notifyAll();//wake up the running cycle
            return true;
        }
        logger.warn("tried to add task to terminating taskManager!");
        return false;
    }
    synchronized void taskStarted(){
        invokeTaskTreeChangeListeners();
    }
    synchronized void taskFinished(){
        logger.trace("Task was finished");
        invokeTaskTreeChangeListeners();
        notifyAll();//wake up the running cycle
    }

    synchronized public boolean addWaitedTask(final Task pending,final Task whoWaits){
        logger.trace("Waited task added");
        /*if( pending.isSerial() ){
            logger.error("You can only wait from a non-serial task, and only for a non-serial task!!!");
            return false;
        }*/
        taskID.put(pending,nextID);
        TreeWalkerBase<Object> walker = new TreeWalkerBase<Object>();
        try{
            walker.doWalk(taskTree,new TreeWalker.Walk<Object>() {
                private void checkCursor(TreeCursor<Object> objectTreeCursor) throws TreeWalker.AbortProcessingException {
                    if(objectTreeCursor.getElement() == whoWaits){
                        MutableListTreeCursor cur = (MutableListTreeCursor<Object>) objectTreeCursor;
                        cur.addChild(pending);
                        throw  new TreeWalker.AbortProcessingException();
                    }
                }
                public void doDown(TreeCursor<Object> objectTreeCursor) throws TreeWalker.AbortProcessingException {
                    checkCursor(objectTreeCursor);
                }

                public void doUp(TreeCursor<Object> objectTreeCursor) throws TreeWalker.AbortProcessingException {
                    checkCursor(objectTreeCursor);
                }

                public void doNext(TreeCursor<Object> objectTreeCursor) throws TreeWalker.AbortProcessingException {
                    checkCursor(objectTreeCursor);
                }
            });
        }catch(TreeWalker.AbortProcessingException ex){
        }
        nextID++;
        taskPriority.put(pending,1); // waited task
        pendingTasks.add(pending);
        invokeTaskTreeChangeListeners();
        notifyAll();//wake up the running cycle
        return true;
    }
    public void toggle(){
        if(status == TaskManagerStatus.PAUSED)
            resume();
        else if(status == TaskManagerStatus.RUNNING)
            pause();
        else if(status == TaskManagerStatus.NOT_STARTED)
            new Thread(this).start();
    }
    synchronized public void resume(){
        logger.trace("Resumed");
        setStatus(TaskManagerStatus.RUNNING);
        invokeTaskTreeChangeListeners();
        notifyAll();
    }
    synchronized public void pause(){
        logger.trace("Paused");
        setStatus(TaskManagerStatus.PAUSED);
        invokeTaskTreeChangeListeners();
        notifyAll();
    }
    synchronized public void run(){
        logger.trace("Stared to run");
        //reentrant synchronization rulez!!!
        if(getStatus() != TaskManagerStatus.NOT_STARTED){
            logger.error("Tried to call run twice!");
            return;//onotole negodue!
        }
        setStatus(TaskManagerStatus.RUNNING);
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
                taskPriority.remove(t);
                taskID.remove(t);
                logger.trace("removed task from running");
            }
            boolean serialRunning = false;
            //...then resume tasks that can be unblocked

            for(Task t : runningTasks){
                /* this code is no more needed
                if(t.getStatus() == Task.TaskStatus.BLOCKED){
                    if(finishedTasks.contains(waitingTasks.get(t))){
                        logger.trace("Unblock blocked task");
                        t.resume();                                                 
                    }
                } */
                if(t.isSerial()){
                    //if the serial task is running right now, we shouldn't go any further
                    serialRunning = true;
                }
            }

            if(! serialRunning){
                //OK, here we go...
                Task task = pendingTasks.peek();
                if(task != null){
                    if((task.isSerial()) && runningTasks.isEmpty()){
                        task = pendingTasks.poll();
                        logger.trace("Execute new serial task");
                        runningTasks.add(task);
                        task.setStatus(Task.TaskStatus.SCHEDULED);
                        invokeTaskTreeChangeListeners();
                        executor.execute(task);
                    }else{
                        //execute all asynchronous tasks...
                        while(task != null && ! (task.isSerial())){
                            task = pendingTasks.poll();
                            logger.trace("Execute new task");
                            runningTasks.add(task);
                            task.setStatus(Task.TaskStatus.SCHEDULED);
                            invokeTaskTreeChangeListeners();
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
                        wait(1);
                        finished = executor.isTerminated();
                    }catch (InterruptedException iex){}
                }
                logger.debug("All tasks are finished, switching back to not_started. " +
                        "Active: " + executor.getActiveCount() +
                        ". Completed: " + executor.getCompletedTaskCount()+
                        ". Overall: " + executor.getTaskCount());
                setStatus(TaskManagerStatus.NOT_STARTED);
                invokeTaskTreeChangeListeners();
                notifyAll();
                isTerminating = false;
                return;
            }
            try{
                notifyAll(); // wake waiting
                logger.trace("Sleep until next action...");
                wait(5000);
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
        t.addTask(new JoinTask(t));
        t.addTask(new JoinTask(t));
        t.terminate();
        t.run();
    }
}
