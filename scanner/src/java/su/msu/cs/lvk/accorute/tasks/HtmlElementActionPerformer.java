package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.DomAction;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    final private ArrayList<DomAction> actions;
    final private EntityID ctx;
    final private Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage> callback;
    final private WebClient webClient = new WebClient();
    private final WebConnection falseWebConn;
    private boolean wasReq = false;
    private final ArrayList<Conversation> convs = new ArrayList<Conversation>();
    private final ArrayList<HttpAction> acts = new ArrayList<HttpAction>();
    private final HttpAction startHttpAct;

    public HtmlElementActionPerformer(
            TaskManager t,
            HttpAction _startHttpAct,
            EntityID ctxID,
            Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage> cb
    ){
        super(t);
        actions = null;
        page = null;
        ctx = ctxID;
        callback = cb;
        falseWebConn = initConn();
        startHttpAct = _startHttpAct;
    }
    public HtmlElementActionPerformer(
            TaskManager t,
            HtmlPage pg,
            ArrayList<DomAction> act_,
            EntityID ctxID,
            Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage> cb
    ) {
        super(t);
        actions = act_;
        ctx = ctxID;
        callback = cb;
        falseWebConn = initConn();
        page = HtmlUnitUtils.clonePage(pg,webClient.getCurrentWindow());
        startHttpAct = null;
    }
    private WebConnection initConn(){
        WebConnection falseWebC= new FalsifyingWebConnection(webClient){
            public WebResponse getResponse(WebRequest request) throws IOException {
                //TODO:not so easy!!!
                wasReq = true;
                List<ActionParameter> param = WebAppProperties.getInstance().getRcd().decompose(request);
                HttpAction act = new HttpAction("tmp", param);
                acts.add(act);
                logger.trace("Will request HttpAction " + act);
                ResponseFetcher tsk = new ResponseFetcher(taskManager, act, ctx);
                waitForTask(tsk);
                while(tsk.getStatus()!=TaskStatus.FINISHED){
                    ;
                }
                logger.trace("got responce from fetcher");
                if(tsk.isSuccessful()){
                    Conversation conv = (Conversation) tsk.getResult();
                    convs.add(conv);
                    return conv.getResponse().genWebResponse(conv.getRequest().getURL(),0, request);
                }
                throw new IOException();
            }
        };
        webClient.setWebConnection(falseWebC);
        return falseWebC;
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
        if(page!=null){
            page.setEnclosingWindow(w);
            w.setEnclosedPage(page);
            page.getWebClient().setWebConnection(falseWebConn);
            DomAction last = actions.get(actions.size() - 1);
            HtmlElement el = page.getFirstByXPath(last.getXpathElString());
            switch (last.getType()){
            case CLICK:
                try{
                    logger.trace("Will click on " + last.getXpathElString());
                    Page  newPage = el.click();
                    if(!wasReq){
                        logger.error("No request intercepted!!!");
                        setSuccessful(false);
                        return;
                    }
                    if(newPage instanceof HtmlPage){
                        callback.CallMeBack(convs,acts,(HtmlPage)newPage);
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
                throw new NotImplementedException("Actions other than click are not yet supported!");
            }
            setSuccessful(true);
        }else if(startHttpAct != null){
            ResponseFetcher tsk = new ResponseFetcher(super.taskManager, startHttpAct, ctx);
            waitForTask(tsk);
            if(!tsk.isSuccessful()){
                logger.error("Could not fetch responce!!!");
                return; //no success
            }
            Conversation conv = (Conversation) tsk.getResult();
            convs.add(conv);
            acts.add(startHttpAct);
            URL origUrl = conv.getRequest().getURL();
            //1. create WebResponse from page : WebResponse(WebResponseData responseData, WebRequest request, long loadTime)
            WebResponse resp = conv.getResponse().genWebResponse(origUrl,1,conv.getRequest().genWebRequest());
            TopLevelWindow baseWindow = (TopLevelWindow) webClient.openWindow(null,"");
            Page page = baseWindow.getEnclosedPage();
            try{
                Page newPage=webClient.loadWebResponseInto(resp, baseWindow);
                if(newPage instanceof HtmlPage){
                    callback.CallMeBack(convs,acts,(HtmlPage)newPage);
                }else{
                    logger.warn("Not a html page?!");
                    setSuccessful(false);
                    return;
                }
            }catch(MalformedURLException muex){
                logger.error("Wrong url!");
                setSuccessful(false);
                return;
            }catch(IOException ex){
                setSuccessful(false);
                return;
            }

        }
    }
}
