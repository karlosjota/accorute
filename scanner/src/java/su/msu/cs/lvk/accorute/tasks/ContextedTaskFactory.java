package su.msu.cs.lvk.accorute.tasks;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.11.2010
 * Time: 21:47:08
 * To change this template use File | Settings | File Templates.
 */
public interface ContextedTaskFactory {
    public Task genContextedTask(EntityID ctx, TaskManager t);
}
