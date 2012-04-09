package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.Node;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import net.sourceforge.htmlunit.corejs.javascript.EcmaError;
import net.sourceforge.htmlunit.corejs.javascript.NativeObject;
import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.decisions.FormFiller;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback4;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.10.2010
 * Time: 14:52:55
 * To change this template use File | Settings | File Templates.
 */
public class HtmlPageParser extends Task implements DomChangeListener, HtmlAttributeChangeListener{
    public static int numInvokations = 0;

    public HtmlPage getPage() {
        return page;
    }

    private final HtmlPage page;
    private final HtmlPage _oldp;
    private HtmlPage tmpPage;
    private final Callback4<HtmlPage,  ArrayList<DomAction>, HttpAction, Boolean> callback;
    private final WebClient webClient;
    private boolean wasRequest;
    private Collection<String> userControllableFormFields = new ArrayList<String>();
    private WebConnection falseWebConn;
    private final EntityID ctxID;
    private ArrayList<DomNode> affectedNodes = new ArrayList<DomNode>();
    private boolean weMayHaveCausedRequest = false;
    private boolean weMayHaveCausedDomOrAttrChange = false;
    private final ArrayList<DomAction> curActionChain = new ArrayList<DomAction>();
    public class ResultItem{
        public ArrayList<DomAction> getDomActionChain() {
            return domActionChain;
        }

        public HttpAction getAction() {
            return action;
        }
        final ArrayList<DomAction> domActionChain;
        final HttpAction action;

        ResultItem(ArrayList<DomAction> domActionChain, HttpAction action) {
            this.domActionChain = domActionChain;
            this.action = action;
        }
    }
    final ArrayList<ResultItem> results = new ArrayList<ResultItem>();
    @Override
    public String toString() {
        return super.toString() + "{" + ctxID.getId().toString() + "}: " + page.getUrl();

    }
    public HtmlPageParser(TaskManager t, HtmlPage _page, EntityID ctx, Callback4<HtmlPage,  ArrayList<DomAction>, HttpAction, Boolean> cb) {
        super(t);
        _oldp = _page;
        callback = cb;
        ctxID = ctx;
        logger.trace("HtmlPageParser created");
        webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        falseWebConn = new FalsifyingWebConnection(webClient){
            public WebResponse getResponse(WebRequest request) throws IOException {
                wasRequest = true;
                logger.debug("Intercepted a request: "+ request);
                List<ActionParameter> param = WebAppProperties.getInstance().getRcd().decompose(request,userControllableFormFields);
                URL u = WebAppProperties.getInstance().getRcd().getURL(param);
                boolean cancellable = false;
                if(!weMayHaveCausedRequest){
                    logger.warn("Request not forced by us!!!");
                }
                ArrayList<DomAction> acChain= (ArrayList<DomAction>)  curActionChain.clone();
                HttpAction act =  new HttpAction("tmp", param);
                callback.CallMeBack(tmpPage, acChain, act, cancellable);
                results.add(new ResultItem(acChain, act));
                WebResponseData wrd = new WebResponseData(
                        "<html></html>".getBytes(),
                        200,
                        "200 OK",
                        new ArrayList<NameValuePair>()
                );
                return new WebResponse(wrd, request, 1);
            }
        };
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
        //TODO: requests here may follow, need to intercept and add cookies.
        //TODO: Still, content loaded on load (usually js and css, is almost always not protected by cookies.
        page = HtmlUnitUtils.clonePage(_page,webClient.getCurrentWindow(),ctxID);
        webClient.setWebConnection(falseWebConn);
    }
    private void tryClick(HtmlElement htmlElement) throws IOException{
        userControllableFormFields.clear();
        HtmlForm form = htmlElement.getEnclosingForm();
        if(form != null){
            userControllableFormFields.addAll(HtmlUnitUtils.getUserControllableFormFields(form));
        }
        try{
            String path = htmlElement.getCanonicalXPath();
            //clone page
            //set a window
            WebWindow cur_window = webClient.getCurrentWindow();

            WebWindow w = webClient.openWindow(null,"tmpWindow");
            tmpPage =  HtmlUnitUtils.clonePage(page,w,ctxID);
            //get corresponding element...
            HtmlElement el = tmpPage.getFirstByXPath(path);
            if(el == null){
                logger.fatal("Error after cloning a page: element not found by xpath " + path);
                return;
            }
            wasRequest = false;
            curActionChain.add(new DomAction(path, DomActionType.CLICK));
            //dry-run the action
            weMayHaveCausedRequest = true;
            logger.trace("Trying to click "+ path);
            el.click();
            weMayHaveCausedRequest = false;
            userControllableFormFields.clear();
            webClient.setCurrentWindow(cur_window);
            webClient.getCache().clear();
            if(!wasRequest){
                logger.trace("click didn't produce a request");
                affectedNodes.clear();
                weMayHaveCausedDomOrAttrChange = true;
                htmlElement.click();
                weMayHaveCausedDomOrAttrChange = false;
                for(DomNode node: affectedNodes){
                    if(node instanceof HtmlElement)
                        emulateUserActions((HtmlElement)node);
                }
            }
            curActionChain.remove(curActionChain.size() - 1);
        }catch (NullPointerException ex){
            //getFirstByXpath sometimes throws it
        }
    }
    private void emulateUserActions(HtmlElement el) throws IOException{
        /*
            TODO: need to determine an order in which we do the actions
            E.g. if an action would delete a block of DOM with attached event handlers,
            we should first check that deleted block and then proceed with the action
         */
        doJsActions(el);
        logger.trace("emulateUserActions on " + el);
        for (HtmlElement elToClick : el.getHtmlElementDescendants()) {
            if(!doJsActions(elToClick) && (elToClick instanceof  HtmlAnchor) && (!((HtmlAnchor)elToClick).getHrefAttribute().equals(DomElement.ATTRIBUTE_NOT_DEFINED))){
                tryClick(elToClick);
            }
        }
        for(HtmlElement f: el.getHtmlElementsByTagName("form")){
            HtmlForm form = (HtmlForm) f;
            FormFiller filler;
            filler = WebAppProperties.getInstance().getFormFillerFactory().generate(form,ctxID);
            while(filler.hasNext()){
                tryClick(filler.next());
            }
        }
    }
    private boolean isClickSensible(ScriptableObject scriptableObject){
        if(! (scriptableObject instanceof  Node))
            return false;
        Node jsNode = (Node) scriptableObject;
        if(
                jsNode.hasEventHandlers("click") || NativeObject.hasProperty(scriptableObject, "onclick") && NativeObject.getProperty(scriptableObject, "onclick") != null ||
                        jsNode.hasEventHandlers("mousedown") || NativeObject.hasProperty(scriptableObject, "onmousedown") && NativeObject.getProperty(scriptableObject, "onmousedown") != null ||
                        jsNode.hasEventHandlers("mouseup") || NativeObject.hasProperty(scriptableObject, "onmouseup") && NativeObject.getProperty(scriptableObject, "onmouseup") != null
                ){
            return true; // Handler directly attached
        }
        Node parent = jsNode.getParent();
        return (parent != null) && isClickSensible(parent);
    }

    /**
     * Trigger various events that might have handlers installed
     * @param htmlElement element on which to trigger actions
     * @return true, iff we clicked on this element, false otherwise
     * @throws IOException
     */
    private boolean doJsActions(HtmlElement htmlElement) throws IOException{
        String path = htmlElement.getCanonicalXPath();
        ScriptableObject scriptableObject = htmlElement.getScriptObject();
        if (scriptableObject != null &&
                scriptableObject instanceof Node)
        {
            logger.trace("doJsActions on " + path + " " + scriptableObject.toString());
            if(isClickSensible(scriptableObject)){
                tryClick(htmlElement);
                return true;
            }
        }
        return false;
    }
    public void nodeAdded(final DomChangeEvent event) {
        DomNode changedNode = event.getChangedNode();
        if(weMayHaveCausedDomOrAttrChange){
            affectedNodes.add(changedNode);
        }else{
            logger.trace("Node was changed but we didn't cause it: " + changedNode);
        }
    }

    public void nodeDeleted(final DomChangeEvent event) {/* we have nothing to do with it */}

    @Override
    public Object getResult() {
        if(getStatus() == TaskStatus.FINISHED)
            return results;
        else
            return null;
    }

    public void attributeAdded(HtmlAttributeChangeEvent htmlAttributeChangeEvent) {
        if(weMayHaveCausedDomOrAttrChange){
            logger.trace("attributeAdded: " + htmlAttributeChangeEvent);
            if(htmlAttributeChangeEvent.getName().startsWith("on"))
                affectedNodes.add(htmlAttributeChangeEvent.getHtmlElement());
        }else{
            logger.trace("attribute changed but we didn't cause this, " + htmlAttributeChangeEvent);
        }
    }

    public void attributeRemoved(HtmlAttributeChangeEvent htmlAttributeChangeEvent) {
        /* nothing to do here */
    }

    public void attributeReplaced(HtmlAttributeChangeEvent htmlAttributeChangeEvent) {
        if(weMayHaveCausedDomOrAttrChange){
            logger.trace("attributeReplaced: " + htmlAttributeChangeEvent);
            if(htmlAttributeChangeEvent.getName().startsWith("on"))
                affectedNodes.add(htmlAttributeChangeEvent.getHtmlElement());
        }else{
            logger.trace("attribute changed but we didn't cause this, " + htmlAttributeChangeEvent);
        }
    }

    @Override
    protected void start() {
        numInvokations++;
        page.addDomChangeListener(this);
        page.addHtmlAttributeChangeListener(this);
        webClient.setThrowExceptionOnFailingStatusCode(false);
        webClient.setThrowExceptionOnScriptError(false);
        try{
            emulateUserActions(page.getDocumentElement());
        }catch(IOException ex){
            logger.error("IO exception while doing stuff");
        }
        webClient.waitForBackgroundJavaScript(30000);
        webClient.closeAllWindows();
    }
}
