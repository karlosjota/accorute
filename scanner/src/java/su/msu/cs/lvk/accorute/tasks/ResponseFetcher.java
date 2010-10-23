package su.msu.cs.lvk.accorute.tasks;

import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback1;
import su.msu.cs.lvk.accorute.utils.Callback2;
import com.gargoylesoftware.htmlunit.HttpWebConnection;


/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.10.2010
 * Time: 0:09:25
 * To change this template use File | Settings | File Templates.
 */
public class ResponseFetcher extends Task {
    final private Callback1<Conversation> callback;
    final private Action action;
    final private EntityID contextID;
    public ResponseFetcher(TaskManager t, Action act, EntityID ctxID, Callback1<Conversation> c) {
        super(t);
        callback = c;
        action = act;
        contextID = ctxID;
    }

    public Object getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void start() {
        WebAppUser u = WebAppProperties.getInstance().getUserService().getUserByID(
                WebAppProperties.getInstance().getContextService().getContextByID(contextID).getUserID()
        );
        Request req = WebAppProperties.getInstance().getRcd().compose(action.getActionParameters(), u);
        //TODO: ...

    }
}
