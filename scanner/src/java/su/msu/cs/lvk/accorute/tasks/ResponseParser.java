package su.msu.cs.lvk.accorute.tasks;

import su.msu.cs.lvk.accorute.http.model.Action;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.Response;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback1;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.10.2010
 * Time: 14:52:55
 * To change this template use File | Settings | File Templates.
 */
public class ResponseParser extends Task {
    public ResponseParser(TaskManager t, Response r, Callback1<Action> cb) {
        super(t);
    }

    @Override
    public Object getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void start() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
