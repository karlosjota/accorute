package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 11.11.2010
 * Time: 17:38:06
 * To change this template use File | Settings | File Templates.
 */
public class HtmlElementActionPerformer extends Task {
    public enum Result{
        NOTFINISHED,
        SUCCESSFUL,
        FETCHERROR,
        NOTHTML,
        NOREQUEST
    };
    final private HtmlPage page;
    final private ArrayList<DomAction> actions;
    final private EntityID ctx;
    final private Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage> callback;
    final private WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
    private final WebConnection falseWebConn;
    private Result result = Result.NOTFINISHED;
    private boolean wasReq = false;
    private Collection<String> userControllableFormFields = new ArrayList<String>();
    private final ArrayList<Conversation> convs = new ArrayList<Conversation>();
    private final ArrayList<HttpAction> acts = new ArrayList<HttpAction>();
    private final HttpAction startHttpAct;

    @Override
    public String toString() {
        String prefix = "{" + ctx.getId().toString() + "}: ";
        if(page != null){
            return prefix + actions + " on " + page.getUrl();
        }else{
            URL url = WebAppProperties.getInstance().getRcd().getURL(startHttpAct.getActionParameters());
            boolean isPost = false;
            for(ActionParameter p : startHttpAct.getActionParameters()){
                if(p.getLocation() == ActionParameterLocation.BODY){
                    isPost = true;
                    break;
                }

            }
            return prefix + (isPost?"POST":"GET") + " " + url.toString();
        }
    }

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
        page = HtmlUnitUtils.clonePage(pg,webClient.getCurrentWindow(),ctx);
        startHttpAct = null;
    }
    private WebConnection initConn(){
        WebConnection falseWebC= new FalsifyingWebConnection(webClient){
            final UserContext contx = WebAppProperties.getInstance().getContextService().getContextByID(ctx);
            public WebResponse getResponse(WebRequest request) throws IOException {
                //TODO:not so easy!!!
                wasReq = true;
                List<ActionParameter> param = WebAppProperties.getInstance().getRcd().decompose(
                        request,
                        userControllableFormFields
                );
                HttpAction act = new HttpAction("tmp", param);
                acts.add(act);
                logger.trace("Will request HttpAction " + act);
                ResponseFetcher tsk = new ResponseFetcher(taskManager, act, ctx);
                waitForTask(tsk);
                logger.trace("got responce from fetcher");
                if(tsk.isSuccessful()){
                    Conversation conv = (Conversation) tsk.getResult();
                    convs.add(conv);
                    WebAppProperties.getInstance().getDynCredUpd().updateCredentials(contx.getUserID(),conv);
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
        return result;
    }

    @Override
    protected void start() {
        WebWindow w = webClient.openWindow(null,"tmpWindow");
        webClient.setThrowExceptionOnFailingStatusCode(false);
        webClient.setThrowExceptionOnScriptError(false);
        webClient.setConfirmHandler(
                new  ConfirmHandler(){
                    public boolean handleConfirm(Page page, String message) {
                        return true;
                    }
                }
        );
        webClient.setAlertHandler(
                new AlertHandler() {
                    public void handleAlert(Page page, String message) {
                        return;
                    }
                }
        );
        webClient.setPromptHandler(
                new PromptHandler() {
                    public String handlePrompt(Page page, String message) {
                        return "test";
                    }
                }
        );
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        if(page!=null){
            HtmlPage ownPage = HtmlUnitUtils.clonePage(page,webClient.getCurrentWindow(),ctx);
            DomAction last = actions.get(actions.size() - 1);
            HtmlElement el = ownPage.getFirstByXPath(last.getXpathElString());              
            logger.trace("My webclient: " + webClient);
            logger.trace("Page's web client " + ownPage.getWebClient());
            switch (last.getType()){
                case CLICK:
                    try{
                        logger.trace("Will click on " + last.getXpathElString());
                        if(el.getEnclosingForm()!=null){
                            userControllableFormFields.addAll(
                                    HtmlUnitUtils.getUserControllableFormFields((HtmlForm) el.getEnclosingForm())
                            );
                        }else{
                            userControllableFormFields.clear();
                        }
                        Page  newPage = el.click();
                        if(!wasReq){
                            //This should never happen!
                            logger.error("No request intercepted!!!");
                            result = Result.NOREQUEST;
                            setSuccessful(false);
                            return;
                        }
                        webClient.waitForBackgroundJavaScript(10000);
                        webClient.getJavaScriptEngine().holdPosponedActions();
                        if(newPage instanceof HtmlPage){
                            UserContext contx = WebAppProperties.getInstance().getContextService().getContextByID(ctx);
                            WebAppProperties.getInstance().getDynCredUpd().updateCredentials(contx.getUserID(),(HtmlPage)newPage);
                            callback.CallMeBack(convs,acts,(HtmlPage)newPage);
                        }else{
                            logger.warn("Not a html page?!");
                            result = Result.NOTHTML;
                            setSuccessful(false);
                            return;
                        }
                    }catch ( IOException ex){
                        result = Result.FETCHERROR;
                        setSuccessful(false);
                        return;
                    }
                    break;
                default:
                    throw new NotImplementedException("Actions other than click are not yet supported!");
            }
        }else if(startHttpAct != null){
            ResponseFetcher tsk = new ResponseFetcher(super.taskManager, startHttpAct, ctx);
            waitForTask(tsk);
            if(!tsk.isSuccessful()){
                result = Result.FETCHERROR;
                logger.error("Could not fetch responce!!!");
                return; //no success
            }
            Conversation conver = (Conversation) tsk.getResult();
            convs.add(conver);
            acts.add(startHttpAct);
            URL origUrl = conver.getRequest().getURL();
            //1. create WebResponse from page : WebResponse(WebResponseData responseData, WebRequest request, long loadTime)
            WebResponse resp = conver.getResponse().genWebResponse(origUrl,1,conver.getRequest().genWebRequest());
            TopLevelWindow baseWindow = (TopLevelWindow) webClient.openWindow(null,"");
            Page page = baseWindow.getEnclosedPage();
            try{
                //TODO: this creates invalid pages in terms of javascript!
                Page newPage=webClient.loadWebResponseInto(resp, baseWindow);
                webClient.waitForBackgroundJavaScript(12000);
                webClient.getJavaScriptEngine().holdPosponedActions();
                if(newPage instanceof HtmlPage){
                    UserContext contx = WebAppProperties.getInstance().getContextService().getContextByID(ctx);
                    for(Conversation conv: convs){
                        WebAppProperties.getInstance().getDynCredUpd().updateCredentials(contx.getUserID(),conv);
                    }
                    WebAppProperties.getInstance().getDynCredUpd().updateCredentials(contx.getUserID(),(HtmlPage)newPage);
                    callback.CallMeBack(convs,acts,(HtmlPage)newPage);
                }else{
                    logger.warn("Not a html page?!");
                    result = Result.NOTHTML;
                    setSuccessful(false);
                    return;
                }
            }catch(MalformedURLException muex){
                throw new InvalidParameterException("Wrong url in action");
            }catch(IOException ex){
                setSuccessful(false);
                return;
            }

        }
        setSuccessful(true);
        result = Result.SUCCESSFUL;
    }
}
