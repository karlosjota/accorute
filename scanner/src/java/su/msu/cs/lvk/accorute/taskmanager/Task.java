package su.msu.cs.lvk.accorute.taskmanager;

import org.apache.log4j.Logger;
import su.msu.cs.lvk.accorute.utils.Callback0;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 02.04.2010
 * Time: 1:54:52
 * To change this template use File | Settings | File Templates.
 */

/**
 * Abstract class for a task that is run by {@link TaskManager}
 */
public abstract class Task implements Runnable{
    public TaskManager getTaskManager() {
        return taskManager;
    }

    final public TaskManager taskManager;
    private Date started, finished;

    public Date getFinished() {
        return finished;
    }

    public Date getStarted() {
        return started;
    }

    public String getExecutorThreadName() {
        if(executorThread != null)
            return executorThread.getName();
        else
            return null;
    }

    private Thread executorThread;

    public boolean isSerial() {
        return serial;
    }

    final private boolean serial;
    
    public enum TaskStatus {
        NOT_STARTED,SCHEDULED, RUNNING, BLOCKED, FINISHED
    }

    private final List<Callback0> Callbacks = new ArrayList<Callback0>();

    public void registerCallback(Callback0 cb){
        Callbacks.add(cb);
    }

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    private TaskStatus status = TaskStatus.NOT_STARTED;
    private boolean successful = false;

    public boolean isSuccessful() {
        return successful;
    }

    synchronized public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    /**
     * Get current status
     * @return current status
     */
    public TaskStatus getStatus(){
        return status;
    }

    /**
     * Concurrent way to set current status.
     * @param stat
     */
    synchronized protected void setStatus(TaskStatus stat){
        status = stat;
        logger.trace("Task STATUS CHANGED to " + stat.name() +" at thread "
                + Thread.currentThread().getId());
    }

    /**
     * @param t - TaskManager that invoked this task.
     */
    public Task(TaskManager t){
        taskManager = t;
        serial = false;
    }
    /**
     * @param t - TaskManager that invoked this task.
     * @param ser - set to true iff serial 
     */
    public Task(TaskManager t, boolean  ser){
        taskManager = t;
        serial = ser;
    }

    /**
     * This adds task to Manager until the specified task will be executed and successfully finished.
     * Relies on the {@link TaskManager}, that should explicitly resume this task when
     * the target task is finished.
     * @param tsk -
     */
    protected boolean addTask(Task tsk){
        return taskManager.addTask(tsk);
    }
    private int spawnedTasks = 0;
    protected void waitForSpawnedTasks(){
        synchronized (this){
            setStatus(TaskStatus.BLOCKED);
            while(spawnedTasks != 0 && getStatus() == TaskStatus.BLOCKED){
                try{
                    wait();
                }catch (InterruptedException e){}
            }
        }
    }
    protected boolean addWaitedTask(Task tsk){
        tsk.registerCallback(new Callback0() {
            public void CallMeBack() {
                synchronized (this){
                    spawnedTasks --;
                    if(spawnedTasks == 0 && getStatus() == TaskStatus.BLOCKED){
                        resume();
                    }
                }
            }
        });
        if(taskManager.addWaitedTask(tsk, this)){
            spawnedTasks ++;
            return true;
        }
        return false;
    }

    /**
     * This blocks until the specified task will be executed and successfully finished.
     * Relies on the {@link TaskManager}, that should explicitly resume this task when
     * the target task is finished.
     * @param tsk -
     */
    synchronized protected void waitForTask(Task tsk){
        if(tsk.isSerial()){
            logger.error("Will not wait for a serial task!");
        }
        logger.trace("Task  WILL WAIT for other task at thread " + Thread.currentThread().getId());
        setStatus(TaskStatus.BLOCKED);
        class myCallback implements Callback0 {
            public void CallMeBack(){
                resume();
            }
        };
        myCallback cb = new myCallback();
        tsk.registerCallback(cb);
        taskManager.addWaitedTask(tsk, this);
        while (tsk.getStatus() != TaskStatus.FINISHED || getStatus() != TaskStatus.RUNNING){
            try{
                wait();
            }catch (InterruptedException e){}
        }
        return;//return only if task was actually finished
    }

    /**
     * This should be called by {@link TaskManager} instance responsible for this task
     * to notify that the requested task was finished. If the task was not started, start it.
     * This should only be called if this task execution was previously started by {@link super.run()}
     */
    public void resume(){
        if(getStatus() == TaskStatus.NOT_STARTED){
            run();
            return;
        }
        synchronized (this){
            if(getStatus() != TaskStatus.BLOCKED){
                logger.error("Task was resumed when it's not blocked!");
            }else{
                logger.trace("Task was RESUMED at thread " + Thread.currentThread().getId());
                setStatus(TaskStatus.RUNNING);
                notifyAll();
            }
        }
    }

    /**
     * Used to retrieve the result of the task.
     * Warning! do not forget to make it thread-safe!
     * @return returns the result of the task or null if it is not finished.
     */
    @SuppressWarnings("unchecked")
    abstract public Object getResult();


    abstract protected void start();

    public void run(){
        executorThread = Thread.currentThread();
        try{
            logger.trace("Task " +this.getClass().getName()+ " was STARTED at thread " + Thread.currentThread().getId());
            started = new Date();
            setStatus(TaskStatus.RUNNING);
            taskManager.taskStarted();
            start();
        }catch(Exception e){
            logger.error(e);
        }finally {
            setStatus(TaskStatus.FINISHED);
            finished = new Date();
            taskManager.taskFinished();
            for (int i=0; i< Callbacks.size(); i++){
              Callbacks.get(i).CallMeBack();
            }
        }
    }
    
    public String toString(){
        return "";
    }
}
