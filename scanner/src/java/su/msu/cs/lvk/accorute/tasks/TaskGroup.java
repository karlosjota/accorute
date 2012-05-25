package su.msu.cs.lvk.accorute.tasks;

import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 09.05.12
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public class TaskGroup extends Task {
    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    private boolean concurrent = true;
    final private List<Task> tasks = new LinkedList<Task>();
    private final String name;
    @Override
    public Object getResult() {
        return null;
    }
    public void addTsk(Task task){
        synchronized (this){
            if(getStatus() != TaskStatus.NOT_STARTED){
                logger.error("Cannot add if already started!!!");
                return;
            }
            tasks.add(task);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected void start() {
        if(concurrent){
            setSuccessful(false);
            for(Task t : tasks)
                addWaitedTask(t);
            waitForSpawnedTasks();
            for(Task t : tasks){
                if(!t.isSuccessful())
                    return;
            }
        }else{
            for(Task t : tasks)
                waitForTask(t);
            for(Task t : tasks){
                if(!t.isSuccessful())
                    return;
            }
        }
        setSuccessful(true);
    }
    public TaskGroup(TaskManager t) {
            super(t);
            this.name = "";
        }
    public TaskGroup(TaskManager t, String name) {
        super(t);
        this.name = name;
    }
}
