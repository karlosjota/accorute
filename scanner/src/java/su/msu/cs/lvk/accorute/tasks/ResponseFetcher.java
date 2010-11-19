package su.msu.cs.lvk.accorute.tasks;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.storage.dao.RAM.CollectionContextService;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


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
        Request req = WebAppProperties.getInstance().getRcd().compose(action.getActionParameters(), WebAppProperties.getInstance().getContextService().getContextByID(contextID));
        logger.trace(req);
        HttpMethod httpM;
        try{
            httpM = req.genHTTPClientMethod();
        }catch(URIException uex){
            //TODO: warning here!
            return; // no success
        }
        try{
            if(httpM instanceof PostMethod){
                PostMethod pm = (PostMethod) httpM;
                logger.trace(pm.getRequestHeader("Content-length"));
                logger.trace(pm.getParameters());
            }
            int statusCode = client.executeMethod(httpM);
            Response resp = new Response(httpM);
            resp.setRequest(req);
            resp.setCtxID(contextID);
            CookieDescriptor desc = resp.getCookieDescriptor();
            //Save cookies
            WebAppProperties.getInstance().getCookieService().setCookies(desc);
            //Update dynamic credentials
            for(Cookie c: desc.getCookies()){
                u.getDynamicCredentials().put(c.getName(),c.getValue());
            }
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