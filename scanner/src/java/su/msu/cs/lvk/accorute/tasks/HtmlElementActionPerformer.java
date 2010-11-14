package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.DOMAction;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 11.11.2010
 * Time: 17:38:06
 * To change this template use File | Settings | File Templates.
 */
public class HtmlElementActionPerformer extends Task {
    final private HtmlPage page;
    final private ArrayList<DOMAction> actions;
    final private EntityID ctx;
    final private Callback3<Conversation, HTTPAction, HtmlPage> callback;
    final private WebClient webClient = new WebClient();
    final private WebConnection falseWebConn;
    private boolean wasReq = false;
    private Conversation conv;
    private HTTPAction act;
    public HtmlElementActionPerformer(
            TaskManager t,
            HtmlPage pg,
            ArrayList<DOMAction> act_,
            EntityID ctxID,
            Callback3<Conversation, HTTPAction, HtmlPage> cb
    ) {
        super(t);
        actions = act_;
        ctx = ctxID;
        callback = cb;
        falseWebConn = new FalsifyingWebConnection(webClient){
            public WebResponse getResponse(WebRequest request) throws IOException {
                //TODO:not so easy!!!
                wasReq = true;
                List<ActionParameter> param = WebAppProperties.getInstance().getRcd().decompose(request);
                act = new HTTPAction(actions + " on "+ page, param);
                logger.trace("Will request HTTPAction " + act);
                ResponseFetcher tsk = new ResponseFetcher(taskManager, act, ctx);
                waitForTask(tsk);
                while(tsk.getStatus()!=TaskStatus.FINISHED){
                    ;
                }
                logger.trace("got responce from fetcher");
                if(tsk.isSuccessful()){
                    conv = (Conversation) tsk.getResult();
                    return conv.getResponse().genWebResponse(conv.getRequest().getURL(),0, request);
                }
                throw new IOException();
            }
        };
        webClient.setWebConnection(falseWebConn);
        page = HtmlUnitUtils.clonePage(pg,webClient.getCurrentWindow());
    }

    @Override
    public Object getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void start() {
        WebWindow w = webClient.openWindow(null,"tmpWindow");
        webClient.setThrowExceptionOnFailingStatusCode(false);
        webClient.setThrowExceptionOnScriptError(false);
        page.setEnclosingWindow(w);
        w.setEnclosedPage(page);
        page.getWebClient().setWebConnection(falseWebConn);
        DOMAction last = actions.get(actions.size() - 1);
        HtmlElement el = page.getFirstByXPath(last.getXpathElString());
        switch (last.getType()){
        case CLICK:
            try{
                logger.trace("Will click on " + last.getXpathElString());
                logger.trace(page);
                Page  newPage = el.click();
                logger.trace(newPage);
                if(!wasReq){
                    logger.error("No request intercepted!!!");
                    setSuccessful(false);
                    return;
                }
                if(newPage instanceof HtmlPage){
                    callback.CallMeBack(conv,act,(HtmlPage)newPage);
                }else{
                    logger.warn("Not a html page?!");
                    setSuccessful(false);
                    return;
                }
            }catch ( IOException ex){
                setSuccessful(false);
                return;
            }
            break;
        default:
            throw new NotImplementedException("Actions other than click are not supported!");
        }
        setSuccessful(true);
    }
}
