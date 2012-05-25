package su.msu.cs.lvk.accorute.tasks;

import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 15.05.12
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class TaskDeletedForGC extends Task {
    private final String str;
    @Override
    public Object getResult() {
        return null;
    }

    public TaskDeletedForGC(TaskManager t, String str) {
        super(t);
        setSuccessful(true);
        setStatus(TaskStatus.FINISHED);
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }

    @Override
    protected void start() {
        //
    }
}
