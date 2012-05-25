package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.javascript.host.EventListenersContainer;
import com.gargoylesoftware.htmlunit.javascript.host.Node;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import net.sourceforge.htmlunit.corejs.javascript.*;
import org.apache.commons.collections.list.UnmodifiableList;
import org.w3c.dom.NodeList;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.decisions.FormFiller;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback4;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.10.2010
 * Time: 14:52:55
 * To change this template use File | Settings | File Templates.
 */
public class HtmlPageParser extends Task{
    public static int numInvokations = 0;
    public HtmlPage getPage() {
        return page;
    }
    private HtmlPage page;
    private int count = 0;
    private final HtmlPage origPage;
    private HtmlPage clonedPage;
    private final Callback4<HtmlPage,  ArrayList<DomAction>, HttpAction, Boolean> callback;
    private final WebClient webClient;
    private boolean wasRequest;
    private boolean canReusePage = false;
    private Collection<String> userControllableFormFields = new ArrayList<String>();
    private WebConnection falseWebConn;
    private final EntityID ctxID;

    public List<TraversalStep> getTraversalSteps() {
        return Collections.unmodifiableList((traversalSteps));
    }

    private final List<TraversalStep> traversalSteps = Collections.synchronizedList(new LinkedList<TraversalStep>());
    private boolean weMayHaveCausedRequest = false;
    private ArrayList<DomAction> curActionChain = null;
    
    private Map<String,Set<DomNode>> nodesWithHandlerAttachedByEvent = new HashMap<String, Set<DomNode>>();
    private Map<String,Map<DomNode, List<String>>> selectorsByNodeByEvent = new HashMap<String, Map<DomNode, List<String>>>();
    
    public class TraversalStep{
        public Collection<DomNode> getNodes() {
            return Collections.unmodifiableCollection(nodes);
        }

        public Collection<DomNode> getNodeProcessed() {
            return Collections.unmodifiableSet(nodeProcessed);
        }

        public Collection<DomAction> getPrerequisiteActions() {
            return Collections.unmodifiableCollection(prerequisiteActions);
        }

        final Collection<DomNode> nodes;
        final HashSet<DomNode> nodeProcessed = new HashSet<DomNode>();
        final ArrayList<DomAction> prerequisiteActions;
        final HtmlPage page;

        public boolean isDone() {
            return done;
        }

        boolean done = false;
        private TraversalStep(Collection<DomNode> nodes, ArrayList<DomAction> prerequisiteActions, HtmlPage page) {
            this.nodes = nodes;
            this.prerequisiteActions = prerequisiteActions;
            this.page = page;
        }
    }
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
    private class CallBackArgs{
        private final HtmlPage page;
        private final ArrayList<DomAction> acts;
        private final HttpAction act;
        private final boolean cancellable;

        private CallBackArgs(HtmlPage page,  ArrayList<DomAction> acts, HttpAction act, boolean cancellable) {
            this.act = act;
            this.acts = acts;
            this.cancellable = cancellable;
            this.page = page;
        }
    }
    private final List<CallBackArgs> cbArgs = Collections.synchronizedList(new LinkedList<CallBackArgs>());
    @Override
    public String toString() {
        return "{" + ctxID.getId().toString() + "}: " + origPage.getUrl();
    }
    public HtmlPageParser(TaskManager t, HtmlPage page_, EntityID ctx, Callback4<HtmlPage,  ArrayList<DomAction>, HttpAction, Boolean> cb) {
        super(t);
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
                //cbArgs.add(new CallBackArgs(clonedPage, acChain, act, cancellable));
                callback.CallMeBack(clonedPage, acChain, act, cancellable);
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
                new ConfirmHandler() {
                    public boolean handleConfirm(Page page, String message) {
                        return true;
                    }
                }
        );
        webClient.setAlertHandler(
                new AlertHandler() {
                    public void handleAlert(Page page, String message) {}
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
        origPage = page_;
        webClient.setWebConnection(falseWebConn);
    }
    private void tryClick(HtmlElement htmlElement) throws IOException{
        if(htmlElement.getParentNode() == null){
            logger.warn("Element not attached, will not click " + htmlElement);
            return;
        }
        userControllableFormFields.clear();
        HtmlForm form = htmlElement.getEnclosingForm();
        if(form != null){
            userControllableFormFields.addAll(HtmlUnitUtils.getUserControllableFormFields(form));
        }
        String path = htmlElement.getCanonicalXPath();
        //clone page
        //set a window
        WebWindow cur_window = webClient.getCurrentWindow();
        if(!canReusePage){
            webClient.setCurrentWindow(webClient.getWebWindows().get(0));
            TopLevelWindow tmpWindow = (TopLevelWindow) webClient.openWindow(null,"tempWindow#"+Integer.toString(count++));
            webClient.setCurrentWindow(tmpWindow);
            PageCloner theCloner = new PageCloner(taskManager,page,tmpWindow);
            waitForTask(theCloner);
            if(!theCloner.isSuccessful()){
                throw new RuntimeException("Clonning the page was unsuccessful!");
            }
            clonedPage = (HtmlPage) theCloner.getResult();
        }else{
            webClient.setCurrentWindow(clonedPage.getEnclosingWindow());
        }
        //get corresponding element...
        HtmlElement clonedElement = clonedPage.getFirstByXPath(path);
        if(clonedElement == null)
            throw  new RuntimeException("Error after cloning a page: element not found by xpath " + path);
        wasRequest = false;
        //cbArgs.clear();
        //dry-run the action
        weMayHaveCausedRequest = true;
        logger.trace("Trying to click " + path);
        DomAndAttrListener listener = new DomAndAttrListener(new HashSet<DomNode>());
        clonedPage.addDomChangeListener(listener);
        curActionChain.add(new DomAction(path, DomActionType.CLICK));
        clonedElement.click();
        webClient.waitForBackgroundJavaScript(100);
        clonedPage.removeDomChangeListener(listener);
        weMayHaveCausedRequest = false;
        userControllableFormFields.clear();
        webClient.setCurrentWindow(cur_window);
        webClient.getCache().clear();
        if(wasRequest){
            logger.trace("click produced a request");
            /*
            canReusePage = false;
            synchronized (cbArgs){
                for(CallBackArgs args : cbArgs){
                    callback.CallMeBack(args.page, args.acts, args.act, args.cancellable);
                }
            }
            cbArgs.clear();
            */
            ((TopLevelWindow)clonedPage.getEnclosingWindow()).close();
            webClient.deregisterWebWindow(clonedPage.getEnclosingWindow());
        }else if(listener.wasChanged()&& listener.getAffectedNodes().size() != 0){
            logger.trace("click produced a DOM change");
            canReusePage = false;
            ArrayList<DomAction> theActionChain = new ArrayList<DomAction>(curActionChain);
            Set<DomNode> nodesToProcess = new HashSet<DomNode>();
            for(DomNode n: listener.getAffectedNodes()){
                nodesToProcess.add(n);
                for(DomNode child: n.getHtmlElementDescendants()){
                    nodesToProcess.add(child);
                }
            }
            traversalSteps.add(new TraversalStep(nodesToProcess, theActionChain, clonedPage));
        }else{
            canReusePage = !listener.wasChanged();
        }
        curActionChain.remove(curActionChain.size() - 1);
    }
    private String getJsByNode(DomNode node){
        String js = "document";
        if(!(node instanceof  HtmlPage)){
            String tagName = ((HtmlElement)node).getTagName();
            NodeList nodeList = node.getPage().getElementsByTagName(tagName);
            int i = 0;
            for(; i < nodeList.getLength(); i++){
                if(nodeList.item(i) == node){
                    break;
                }
            }
            js += ".getElementsByTagName(\"" + tagName+ "\")["+Integer.toString(i)+"]";
        }
        return js;
    }
    private void populateHandlerMaps(HtmlPage thePage){
        try {
            Method getELC = Node.class.getDeclaredMethod("getEventListenersContainer");
            Method getHandlers =  EventListenersContainer.class.getDeclaredMethod("getHandlers", String.class, Boolean.TYPE);
            getHandlers.setAccessible(true);
            getELC.setAccessible(true);
            List<DomNode> nodes = new ArrayList<DomNode>();
            nodes.add(thePage);
            for(DomNode node : thePage.getHtmlElementDescendants()){
                nodes.add(node);
            }
            String [] events = {"click", "mousedown", "mouseup"};
            nodesWithHandlerAttachedByEvent.clear();
            selectorsByNodeByEvent.clear();
            for(String eventName : events){
                HashSet<DomNode> nodesWithHandlerAttached = new HashSet<DomNode>();
                HashMap<DomNode, List<String>> selectorsByNode = new HashMap<DomNode, List<String>>();
                for(DomNode node : nodes){
                    String js = getJsByNode(node);
                    Node jsNode = (Node) node.getScriptObject();
                    EventListenersContainer elc = (EventListenersContainer) getELC.invoke(jsNode);
                    if(elc != null){
                        List<Function> bubblingHandlers = (List<Function>) getHandlers.invoke(elc, eventName, false);
                        List<Function> capturingHandlers = (List<Function>) getHandlers.invoke(elc, eventName, true);
                        Object handlerProp = elc.getEventHandlerProp(eventName);
                        List<Function> allHandlers = new ArrayList<Function>();
                        if(bubblingHandlers != null){
                            allHandlers.addAll(bubblingHandlers);
                        }
                        if (capturingHandlers != null) {
                            allHandlers.addAll(capturingHandlers);
                        }
                        if (handlerProp != null && (handlerProp instanceof Function)) {
                            allHandlers.add((Function) handlerProp);
                        }
                        if(allHandlers.size() != 0){
                            nodesWithHandlerAttached.add(node);
                        }
                        boolean hasJQ = true; // TODO: static analysis!!!
                        if(hasJQ){
                            Object h = thePage.getWebClient().getJavaScriptEngine().execute(thePage, "jQuery(" + js + ").data(\"events\")" ,"",0);
                            if(h instanceof Undefined)
                                continue;
                            Object hdlrs = thePage.getWebClient().getJavaScriptEngine().execute(thePage, "jQuery(" + js + ").data(\"events\")."+eventName,"",0);
                            if(!(hdlrs instanceof NativeArray)){
                                continue;
                            }
                            NativeArray handlers = (NativeArray) hdlrs;
                            for(int i = 0 ; i < handlers.getLength(); i++){
                                NativeObject handler = (NativeObject) handlers.get(i);
                                Object selector = handler.get("selector", handler);
                                if(selector == null || selector instanceof Undefined){
                                    nodesWithHandlerAttached.add(node);
                                }else{
                                    if(!selectorsByNode.containsKey(node)){
                                        selectorsByNode.put(node,new ArrayList<String>());
                                    }
                                    selectorsByNode.get(node).add(selector.toString());
                                }
                            }
                        }
                    }
                }
                nodesWithHandlerAttachedByEvent.put(eventName, nodesWithHandlerAttached);
                selectorsByNodeByEvent.put(eventName,selectorsByNode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void loop() throws IOException {
        int cur_step = 0;
        while(cur_step < traversalSteps.size()){
            TraversalStep step = traversalSteps.get(cur_step++);
            curActionChain = step.prerequisiteActions;
            logger.trace("NOW TRAVERSING " + curActionChain);
            page = step.page;
            populateHandlerMaps(page);
            canReusePage = false;
            for(DomNode  theNode : step.nodes){
                if(! (theNode instanceof HtmlElement)){
                    continue;
                }
                HtmlElement subjectElement = (HtmlElement) theNode;
                if(!doJsActions(subjectElement) && (subjectElement instanceof  HtmlAnchor) && (!((HtmlAnchor)subjectElement).getHrefAttribute().equals(DomElement.ATTRIBUTE_NOT_DEFINED))){
                    tryClick(subjectElement);
                }
                if(subjectElement instanceof  HtmlForm){
                    HtmlForm form = (HtmlForm) subjectElement;
                    FormFiller filler;
                    filler = WebAppProperties.getInstance().getFormFillerFactory().generate(form,ctxID);
                    if(! filler.hasNext()){
                        logger.warn("Form which cannot be submitted!!!");
                    }
                    while(filler.hasNext()){
                        tryClick(filler.next());
                    }
                }
                step.nodeProcessed.add(theNode);
            }
            step.done = true;
        }
    }
    private boolean shouldPerformEvent(DomNode domNode, String event){
        ScriptableObject scriptableObject = domNode.getScriptObject();
        if (scriptableObject != null && scriptableObject instanceof Node){
            Node jsNode = (Node) scriptableObject;
            if(nodesWithHandlerAttachedByEvent.get(event).contains(domNode)){
                return true;
            }
            DomNode curNode = domNode.getParentNode();
            String js = getJsByNode(domNode);
            while(curNode != null){
                if(selectorsByNodeByEvent.get(event).containsKey(curNode)){
                    List<String> selectors = selectorsByNodeByEvent.get(event).get(curNode);
                    for(String selector : selectors){
                        String jsQuery = "jQuery(\"" + selector + "\").filter("+js+")";
                        Object rez = domNode.getPage().getWebClient().getJavaScriptEngine().execute((HtmlPage) domNode.getPage(), jsQuery,"",0);
                        if(!(rez instanceof NativeObject)){
                            continue;
                        }
                        Double len = (Double) ((NativeObject) rez).get("length", (Scriptable) rez);
                        if(len != 0){
                            return true;
                        }
                    }
                }
                curNode = curNode.getParentNode();
            }
        }
        return false;
    }

    /**
     * Trigger various events that might have handlers installed
     * @param htmlElement element on which to trigger actions
     * @return true, iff we clicked on this element, false otherwise
     * @throws IOException
     */
    private boolean doJsActions(HtmlElement htmlElement) throws IOException{
        if(shouldPerformEvent(htmlElement, "click") || shouldPerformEvent(htmlElement, "mousedown") || shouldPerformEvent(htmlElement, "mouseup")){
            tryClick(htmlElement);
            return true;
        }
        return false;
    }
    @Override
    public Object getResult() {
        return results;
    }
    private class DomAndAttrListener implements DomChangeListener, HtmlAttributeChangeListener{
        final Collection<DomNode> affectedNodes;
        boolean wasChange = false;
        public boolean wasChanged() {
            return wasChange;
        }
        public Collection<DomNode> getAffectedNodes() {
            return affectedNodes;
        }
        private DomAndAttrListener(Collection<DomNode> affectedNodes) {
            this.affectedNodes = affectedNodes;
        }
        private void recordChangedNode(DomNode changedNode){
            if(affectedNodes!= null){
                synchronized (affectedNodes){
                    affectedNodes.add(changedNode);
                }
            }
            wasChange = true;
            logger.trace("Change: " + changedNode.getCanonicalXPath());
        }
        public void nodeAdded(DomChangeEvent event) {
            DomNode changedNode = event.getChangedNode();
            recordChangedNode(changedNode);
        }
        public void nodeDeleted(DomChangeEvent event) {
            wasChange = true;
        }
        public void attributeAdded(HtmlAttributeChangeEvent htmlAttributeChangeEvent) {
            if(htmlAttributeChangeEvent.getName().startsWith("on")){
                DomNode changedNode = htmlAttributeChangeEvent.getHtmlElement();
                recordChangedNode(changedNode);
            }
        }
        public void attributeRemoved(HtmlAttributeChangeEvent htmlAttributeChangeEvent) {
        }
        public void attributeReplaced(HtmlAttributeChangeEvent htmlAttributeChangeEvent) {
            if(htmlAttributeChangeEvent.getName().startsWith("on")){
                DomNode changedNode = htmlAttributeChangeEvent.getHtmlElement();
                recordChangedNode(changedNode);
            }
        }
    }     
    @Override
    protected void start() {
        numInvokations++;
        webClient.setThrowExceptionOnFailingStatusCode(false);
        webClient.setThrowExceptionOnScriptError(false);
        webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
            public void scriptException(HtmlPage htmlPage, ScriptException scriptException) {
                logger.warn("exception on " + htmlPage.getUrl() + " : " + scriptException.getMessage());
            }

            public void timeoutError(HtmlPage htmlPage, long allowedTime, long executionTime) {
            }

            public void malformedScriptURL(HtmlPage htmlPage, String url, MalformedURLException malformedURLException) {
            }

            public void loadScriptError(HtmlPage htmlPage, URL scriptUrl, Exception exception) {
                logger.warn("Error loading JS  on " + htmlPage.getUrl() + " from  " + scriptUrl.toString() + " : " + exception.getMessage());
            }
        });
        try{
            PageCloner theCloner = new PageCloner(taskManager,origPage,webClient.openWindow(null,"tempWindow#"+Integer.toString(count++)));
            waitForTask(theCloner);
            if(!theCloner.isSuccessful())
                throw  new RuntimeException("Clone unsuccessful");
            page = (HtmlPage) theCloner.getResult();
            ArrayList<DomNode> nodes = new ArrayList<DomNode>();
            for(HtmlElement el : page.getDocumentElement().getHtmlElementDescendants()){
                nodes.add(el);
            }
            traversalSteps.add(new TraversalStep(nodes, new ArrayList<DomAction>(), page));
            loop();
        }catch(Exception ex){
            setSuccessful(false);
            logger.error(ex);
            return;
        }
        webClient.closeAllWindows();
        setSuccessful(true);
    }
}
