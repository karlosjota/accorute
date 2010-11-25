package su.msu.cs.lvk.accorute.tasks;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
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
        HttpMethod httpM = null;
        try{
            SessionValidityWatcher.RespCheckStatus respCheckStatus;
            Response resp;
            boolean tried=false;
            do{
                SessionValidityWatcher.getInstanceForContext(contextID).prepareForRequest(tried);
                Request req = WebAppProperties.getInstance().getRcd().compose(action.getActionParameters(), WebAppProperties.getInstance().getContextService().getContextByID(contextID));
                httpM = req.genHTTPClientMethod();
                if(httpM instanceof PostMethod){
                    PostMethod pm = (PostMethod) httpM;
                    logger.trace(pm.getRequestHeader("Content-length"));
                    logger.trace(pm.getParameters());
                }
                logger.trace("Will perform " + req);
                client.getState().clear();
                int statusCode = client.executeMethod(httpM);
                resp = new Response(httpM);
                logger.trace("Got: " + resp);
                resp.setRequest(req);
                resp.setCtxID(contextID);
                res = new Conversation(req, resp);
                respCheckStatus = SessionValidityWatcher.getInstanceForContext(contextID).analyzeResponce(res, taskManager);
                tried = true;
            }while(respCheckStatus == SessionValidityWatcher.RespCheckStatus.RETRY);
            if(respCheckStatus == SessionValidityWatcher.RespCheckStatus.NOT_EXPIRED ){
                CookieDescriptor desc = resp.getCookieDescriptor();
                //Save cookies
                WebAppProperties.getInstance().getCookieService().setCookies(desc);
                //Update dynamic credentials
                for(Cookie c: desc.getCookies()){
                    u.getDynamicCredentials().put(c.getName(),c.getValue());
                }
            }
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