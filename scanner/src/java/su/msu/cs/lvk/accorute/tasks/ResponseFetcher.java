package su.msu.cs.lvk.accorute.tasks;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.MalformedInputException;


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
    final static private HttpClient client = WebAppProperties.getInstance().getHttpClient();
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
        boolean isPublic = u.getUserRole().getRoleName().equals("public");
        HttpUriRequest httpUriReq;
        try{
            SessionValidityWatcher.RespCheckStatus respCheckStatus;
            Response resp;
            boolean tried=false;
            Request req;
            do{
                if(!isPublic)
                    SessionValidityWatcher.getInstanceForContext(contextID).prepareForRequest(tried);
                req = WebAppProperties.getInstance().getRcd().compose(action.getActionParameters(), WebAppProperties.getInstance().getContextService().getContextByID(contextID));
                httpUriReq = req.genHTTPClientMethod();
                logger.trace("Will perform " + req);
                HttpResponse responce = client.execute(httpUriReq, new BasicHttpContext());
                resp = new Response(responce);
                logger.trace("Got: " + resp);
                resp.setRequest(req);
                resp.setCtxID(contextID);
                res = new Conversation(req, resp);
                if(!isPublic)
                    respCheckStatus = SessionValidityWatcher.getInstanceForContext(contextID).analyzeResponce(res, taskManager);
                else
                    respCheckStatus = SessionValidityWatcher.RespCheckStatus.NOT_EXPIRED;
                tried = true;
            }while(respCheckStatus == SessionValidityWatcher.RespCheckStatus.RETRY);
            if(respCheckStatus == SessionValidityWatcher.RespCheckStatus.NOT_EXPIRED ){
                res = new Conversation(req,resp);
                WebAppProperties.getInstance().getDynCredUpd().updateCredentials(u.getUserID(),res);
            }
            setSuccessful(true);
        /*} catch (HttpException e) {
            logger.error("Fatal protocol violation: " + e.getMessage());*/
        } catch (IOException e) {
            logger.error("Fatal transport error: " + e.getMessage());
        }
    }
}