package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.Node;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import net.sourceforge.htmlunit.corejs.javascript.EcmaError;
import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.DOMAction;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.10.2010
 * Time: 14:52:55
 * To change this template use File | Settings | File Templates.
 */
//TODO: AJAX is not yet supported! The accorute model itself is not ready now for AJAX.
public class HtmlPageParser extends Task implements DomChangeListener {
    private final HtmlPage page;
    private HtmlPage tmpPage;
    private final Callback3<HtmlPage, ArrayList<DOMAction>, HTTPAction> callback;
    private final WebClient webClient;
    private boolean wasRequest;
    private WebConnection falseWebConn;
    private final ArrayList<DOMAction> curActionChain = new ArrayList<DOMAction>();
    public HtmlPageParser(TaskManager t, HtmlPage _page, Callback3<HtmlPage, ArrayList<DOMAction>, HTTPAction> cb) {
        super(t);
        page = _page;
        callback = cb;
        logger.trace("HtmlPageParser created");
        webClient = new WebClient();
        final HtmlPageParser thisParser = this;
        falseWebConn = new FalsifyingWebConnection(webClient){
            public WebResponse getResponse(WebRequest request) throws IOException {
                wasRequest = true;
                logger.debug("Intercepted a request");
                List<ActionParameter> param = WebAppProperties.getInstance().getRcd().decompose(request);
                callback.CallMeBack(tmpPage, (ArrayList<DOMAction>) curActionChain.clone(),new HTTPAction("tmp", param));
                /*WebResponseData wrd = new WebResponseData(
                        new byte[0],
                        200,
                        "200 OK",
                        new ArrayList< NameValuePair >()
                );
                return new WebResponse(wrd, request, 1); */
                throw new IOException("goes just as planned");
            }
        };
        //create WebConnectionWrapper that calls back the callback on each request
        webClient.setWebConnection(falseWebConn);
    }
    private void tryClick(HtmlElement htmlElement){
        String path = htmlElement.getCanonicalXPath();
        logger.trace("Trying to click "+ path);
        //clone page
        //set a window
        WebWindow w = webClient.openWindow(null,"tmpWindow");
        tmpPage =  HtmlUnitUtils.clonePage(page,w);
        //get corresponding element...
        HtmlElement el = tmpPage.getFirstByXPath(path);
        wasRequest = false;
        curActionChain.add(new DOMAction(path, DOMActionType.CLICK));
        //dry-run the action
        try{
            el.click();
        }catch(IOException ex) {
            logger.warn("io exception while clicking",ex);
        }
        catch(EcmaError ee){
            logger.error("error in javascript!!!! Will continue. ",ee);
        }
        catch(RuntimeException ex){
            //Dirty, dirty hack!!!
            if(ex.getMessage() == null)
                logger.error("got runtime exception during click!", ex);
            if(!ex.getMessage().equalsIgnoreCase("java.io.IOException: goes just as planned"))
                logger.error("got runtime exception during click!", ex);
        }
        //if there was no request, we can happily reproduce this action on current page
        if(!wasRequest){
            logger.trace("click didn't produce a request");
            try{
                htmlElement.click();
            }catch(IOException ex) {
                logger.warn("exception while clicking",ex);
            }catch(RuntimeException ex){
                logger.error("got runtime exception during click!", ex);                
            }
        }else{
            curActionChain.remove(curActionChain.size() - 1);    
        }
    }

    private void emulateUserActions(HtmlPage htmlPage) {
        logger.trace("emulateUserActions on " + htmlPage);
        Iterable<HtmlElement> htmlChildren = htmlPage.getHtmlElementDescendants();
        Iterator<HtmlElement> it = htmlChildren.iterator();
        while (it.hasNext()) {
            doJsActions(it.next());
        }
        List<HtmlAnchor> anchors = htmlPage.getAnchors();
        for(HtmlAnchor a:anchors){
            tryClick(a);
        }
    }
    private void doJsActions(HtmlElement htmlElement) {
        String path = htmlElement.getCanonicalXPath();
        logger.trace("doJsActions on " + path);
        ScriptableObject scriptableObject = htmlElement.getScriptObject();
        if (scriptableObject != null &&
                scriptableObject instanceof Node)
        {
            Node jsNode = (Node) scriptableObject;
            //what we care is onclick event capability
            if (jsNode.getEventHandler("onclick") != null) {
                tryClick(htmlElement);
            }
            //TODO: currently we do not support the situation when request is made while doing this!
            wasRequest = false;
            if (jsNode.getEventHandler("onmouseover") != null) {
                curActionChain.add(new DOMAction(path, DOMActionType.MOUSEOVER));
                htmlElement.mouseOver();
            }
            if (jsNode.getEventHandler("onmousemove") != null) {
                curActionChain.add(new DOMAction(path, DOMActionType.MOUSEMOVE));
                htmlElement.mouseMove();
            }
            if(wasRequest){
                logger.fatal("we do not support the situation when request is made while doing mouseOver and mouseMove!!!!");
            }
        }
    }
    public void nodeAdded(final DomChangeEvent event) {
        logger.trace("DOM changed");
        DomNode newNode = event.getChangedNode();
        /*
        String path = newNode.getCanonicalXPath();
        if(page.getFirstByXPath(path) != newNode){
            logger.warn("Event triggered from cloned page, this is a bug in HtmlUnit");
            return;
        }
        */
        if (newNode instanceof HtmlElement) {
            doJsActions((HtmlElement) newNode);
            Iterable<HtmlElement> htmlChildren = newNode.getHtmlElementDescendants();
            Iterator<HtmlElement> it = htmlChildren.iterator();
            while (it.hasNext()) {
                doJsActions(it.next());
            }
        }
    }

    public void nodeDeleted(final DomChangeEvent event) {/* we have nothing to do with it */}

    @Override
    public Object getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void start() {
        /*URL origUrl = page.getRequest().getURL();
        //1. create WebResponse from page : WebResponse(WebResponseData responseData, WebRequest request, long loadTime)
        WebResponse resp = page.getResponse().genWebResponse(origUrl);
        TopLevelWindow baseWindow = (TopLevelWindow) webClient.openWindow(null,"");
        try {
            webClient.loadWebResponseInto(resp, baseWindow);
        } catch (IOException ioex) {
            baseWindow.close();
            throw new IllegalStateException("Cannot receive IOException on converted Response object");
        }
        Page page = baseWindow.getEnclosedPage();
        if (page instanceof HtmlPage) {
            HtmlPage htmlPage = (HtmlPage) page;
            //find and save all comments

        }*/
        page.addDomChangeListener(this);
        //find all HtmlElement nodes that have actions and execute them
        webClient.setThrowExceptionOnFailingStatusCode(false);
        webClient.setThrowExceptionOnScriptError(false);
        emulateUserActions(page);
        /*baseWindow.close();*/
    }
}
