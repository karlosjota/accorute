package su.msu.cs.lvk.accorute.tasks;

import org.apache.commons.httpclient.*;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback1;
import su.msu.cs.lvk.accorute.utils.Callback2;
import com.gargoylesoftware.htmlunit.HttpWebConnection;

import java.io.IOException;


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
    final static private HttpClient client = new HttpClient();
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
        setSuccessful(false);
        WebAppUser u = WebAppProperties.getInstance().getUserService().getUserByID(
                WebAppProperties.getInstance().getContextService().getContextByID(contextID).getUserID()
        );
        Request req = WebAppProperties.getInstance().getRcd().compose(action.getActionParameters(), u);
        HttpMethod httpM;
        try{
            httpM = req.genHTTPClientMethod();
        }catch(URIException uex){
            //TODO: warning here!
            return; // no success
        }
        try{
            int statusCode = client.executeMethod(httpM);
            if (statusCode != HttpStatus.SC_OK) {
                logger.error("Method failed: " + httpM.getStatusLine());
            }
            Response resp = new Response(httpM);
            Conversation conv = new Conversation(req, resp);
            setSuccessful(true);
            callback.CallMeBack(conv);
            return;
        } catch (HttpException e) {
            logger.error("Fatal protocol violation: " + e.getMessage());
            return; // no success
        } catch (IOException e) {
            logger.error("Fatal transport error: " + e.getMessage());
            return; // no success
        } finally {
            httpM.releaseConnection();
        }
    }
}
