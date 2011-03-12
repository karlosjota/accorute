package su.msu.cs.lvk.accorute.taskmanager;

import org.apache.log4j.Logger;
import su.msu.cs.lvk.accorute.utils.Callback0;

import java.util.ArrayList;
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
    final public TaskManager taskManager;

    public boolean isSerial() {
        return serial;
    }

    final private boolean serial;
    
    public enum TaskStatus {
        NOT_STARTED,RUNNING, BLOCKED, FINISHED
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
    protected boolean addWaitedTask(Task tsk){
        return taskManager.addWaitedTask(tsk);
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
        while(true){
            setStatus(TaskStatus.BLOCKED);
            class myCallback implements Callback0 {
                public void CallMeBack(){
                    resume();
                }
            };
            myCallback cb = new myCallback();
            tsk.registerCallback(cb);
            taskManager.addWaitedTask(tsk);
            while (getStatus() == TaskStatus.BLOCKED){
                try{
                    wait();
                }catch (InterruptedException e){}
            }
            if(tsk.getStatus() == TaskStatus.FINISHED){
                return;//return only if task was actually finished
            }else{
                logger.warn("Task WAS RESUMED FROM WAITING THAT DIDN'T FINISH YET, at thread " + Thread.currentThread().getId());
            }
        }
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
            if(getStatus() != TaskStatus.NOT_STARTED){
                logger.trace("Task was RESUMED at thread " + Thread.currentThread().getId());
                setStatus(TaskStatus.RUNNING);
                notify();
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
        try{
            logger.trace("Task " +this.getClass().getName()+ " was STARTED at thread " + Thread.currentThread().getId());
            setStatus(TaskStatus.RUNNING);
            start();
        }  finally {
            setStatus(TaskStatus.FINISHED);
            taskManager.taskFinished();
            for (int i=0; i< Callbacks.size(); i++){
              Callbacks.get(i).CallMeBack();
            }
        }
    }
}
