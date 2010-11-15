package su.msu.cs.lvk.accorute.tasks;

import org.apache.commons.httpclient.*;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.10.2010
 * Time: 0:09:25
 * To change this template use File | Settings | File Templates.
 */
public class ResponseFetcher extends Task {
    final private HttpAction action;
    final private EntityID contextID;
    private Conversation res;
    final static private HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
    public ResponseFetcher(TaskManager t, HttpAction act, EntityID ctxID) {
        super(t);
        action = act;
        contextID = ctxID;
        res = null;
    }

    public Object getResult() {
        return res;  //To change body of implemented methods use File | Settings | File Templates.
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
            Response resp = new Response(httpM);
            res = new Conversation(req, resp);
            setSuccessful(true);
        } catch (HttpException e) {
            logger.error("Fatal protocol violation: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Fatal transport error: " + e.getMessage());
        } finally {
            httpM.releaseConnection();
        }
    }
}