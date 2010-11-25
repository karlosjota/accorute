package su.msu.cs.lvk.accorute.taskmanager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 25.11.2010
 * Time: 0:44:43
 * To change this template use File | Settings | File Templates.
 */
public class SerialAdapter extends Task{
    private final Collection<Task> tasks = new ArrayList<Task>();
    private boolean wasErr = true;
    private boolean stopOnError;
    public SerialAdapter(TaskManager t, boolean isSerial, boolean stopOnError, Task ... tasks) {
        super(t, isSerial);
        for(Task tsk: tasks){
            this.tasks.add(tsk);
        }
        this.stopOnError = stopOnError;
    }
    @Override
    public Object getResult() {
        return null;
    }
    @Override
    protected void start() {
        for(Task t:tasks){
            logger.trace("will run task " + t.getClass().getName());
            t.run();
            wasErr = wasErr && t.isSuccessful();
            if(wasErr && stopOnError){
                setSuccessful(false);
                return;
            }
        }
        setSuccessful(true);
    }
}
