package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.browserUI.HttpElementAction;
import su.msu.cs.lvk.accorute.browserUI.HttpElementActionType;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback3;

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
    final private HttpElementAction action;
    final private EntityID ctx;
    final private Callback3<Conversation, Action, HtmlPage> callback;
    final private WebClient webClient = new WebClient();
    final private WebConnection falseWebConn;
    private boolean wasReq = false;
    private Conversation conv;
    private Action act;
    public HtmlElementActionPerformer(
            TaskManager t,
            HtmlPage pg,
            HttpElementAction act_,
            EntityID ctxID,
            Callback3<Conversation, Action, HtmlPage> cb
    ) {
        super(t);
        page = pg;
        action = act_;
        ctx = ctxID;
        callback = cb;
        falseWebConn = new FalsifyingWebConnection(webClient){
            public WebResponse getResponse(WebRequest request) throws IOException {
                //TODO:not so easy!!!
                wasReq = true;
                List<ActionParameter> param = WebAppProperties.getInstance().getRcd().decompose(request);
                act = new Action(action + " on "+ page, param);
                logger.trace("Will request Action " + act);
                ResponseFetcher tsk = new ResponseFetcher(taskManager, act, ctx);
                waitForTask(tsk);
                logger.trace("got responce from fetcher");
                if(tsk.isSuccessful()){
                    conv = (Conversation) tsk.getResult();
                    return conv.getResponse().genWebResponse(conv.getRequest().getURL(),0, request);
                }
                throw new IOException();
            }
        };
        webClient.setWebConnection(falseWebConn);
    }

    @Override
    public Object getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void start() {
        WebWindow w = webClient.openWindow(null,"tmpWindow");
        page.setEnclosingWindow(w);
        w.setEnclosedPage(page);
        page.getWebClient().setWebConnection(falseWebConn);
        HtmlElement el = page.getFirstByXPath(action.getXpathElString());
        switch (action.getType()){
        case CLICK:
            try{
                logger.trace("Will click on " + action.getXpathElString());
                Page  newPage = el.click();
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
