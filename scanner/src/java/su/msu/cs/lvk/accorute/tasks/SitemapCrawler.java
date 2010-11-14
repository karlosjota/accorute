package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.DOMAction;
import su.msu.cs.lvk.accorute.decisions.ResponseClassificator;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback3;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.10.2010
 * Time: 13:33:37
 * To change this template use File | Settings | File Templates.
 */
public class SitemapCrawler extends Task implements Callback0{
    //TODO: Add various constraints etc...
    private final HTTPAction startingAct;
    private final EntityID contextID;
    private final Sitemap siteMap;
    private int spawnedTasks;
    private final List<HTTPAction> performedHTTPActions = new ArrayList<HTTPAction>();
    private final Map<HTTPAction,Conversation> actionConversationMap = new HashMap<HTTPAction,Conversation> ();
    private final Map<HTTPAction,EntityID> actionNodeMap = new HashMap<HTTPAction,EntityID> ();
    private final Map<HTTPAction,List<EntityID> > unresolvedActions = new HashMap<HTTPAction,List<EntityID> >();
    private boolean wasErr;
    synchronized public void CallMeBack(){
        spawnedTasks--;
        logger.trace("task finished; "+spawnedTasks+" left");
        notify();
    }
    public SitemapCrawler(TaskManager t, HTTPAction startReq, EntityID ctxID){
        super(t);
        siteMap = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(ctxID);
        startingAct = startReq;
        contextID = ctxID;
        spawnedTasks = 0;
        wasErr = false;
    }

    synchronized private void addNewAction(
            final EntityID fromNodeID,
            final HtmlPage p,
            final CorrespondentActions acts
    ){
        HTTPAction  httpAct = acts.getHttpAction();
        String url = WebAppProperties.getInstance().getRcd().getURL(httpAct.getActionParameters()).toString();
        Matcher m = WebAppProperties.getInstance().getScope().matcher(url);
        if(!m.matches()){
            logger.trace("Will not access url "+ url+" because it's out of scope");
            siteMap.addEdge(siteMap.getNodeByID(fromNodeID), siteMap.getExitNode(), acts, null);
            return;
        }
        logger.trace("url " + url + " is in scope");
        if(WebAppProperties.getInstance().getChStateDec().changesState(httpAct)){
            logger.trace("Will not perform action because it will change the state");
            final Sitemap.SitemapNode from = siteMap.getNodeByID(fromNodeID);
            siteMap.addEdge(from, siteMap.getExitNode(), acts, null);
            return;
        }
        synchronized (performedHTTPActions){
            HTTPAction equalHTTPAction = null;
            for(HTTPAction act: performedHTTPActions){
                if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(act,httpAct)){
                    equalHTTPAction = act;
                    break;
                }
            }
            if(equalHTTPAction != null){
                logger.trace("Will not perform action " + httpAct + " that was already performed");
                Conversation c = actionConversationMap.get(equalHTTPAction);
                EntityID nId = actionNodeMap.get(equalHTTPAction);
                if(c == null || nId == null){
                    List<EntityID> l= unresolvedActions.get(equalHTTPAction);
                    if(l == null){
                        l = new ArrayList<EntityID>();
                        unresolvedActions.put(equalHTTPAction,l);
                    }
                    l.add(fromNodeID);
                }else{
                    siteMap.addEdge(siteMap.getNodeByID(fromNodeID),siteMap.getNodeByID(nId), acts, c);
                }
                return;
            }
            performedHTTPActions.add(httpAct);
        }
        HtmlElementActionPerformer tsk = new HtmlElementActionPerformer(
                taskManager,
                p,
                acts.getDomActions(),
                contextID,
                new Callback3<Conversation, HTTPAction, HtmlPage>(){
                    public void CallMeBack(Conversation c , HTTPAction a, HtmlPage p){
                        addConversationForActionFromNode(fromNodeID,c,p,new CorrespondentActions(a,acts.getDomActions()));
                    }
                }
        );
        tsk.registerCallback(this);
        if(addTask(tsk))
            spawnedTasks++;
        else{
            logger.error("Could not add task!");
            wasErr = true;
        }
    }

    synchronized private void addConversationForActionFromNode(
            final EntityID fromNodeID,
            final Conversation conv,
            final HtmlPage p,
            final CorrespondentActions corActs
    ){
        final HTTPAction action = corActs.getHttpAction();
        final ArrayList<DOMAction> domActs = corActs.getDomActions();
        logger.trace("addConversationForActionFromNode: node " + fromNodeID
                + " action " + action
                + " page " + p
                + " conv " + conv
        );
        ResponseClassificator.ResponseType respType =  WebAppProperties.getInstance().getRespClassificator().getResponseType(conv);
        final Sitemap.SitemapNode from = siteMap.getNodeByID(fromNodeID);
        Sitemap.SitemapNode resultingNode;
        if(respType == ResponseClassificator.ResponseType.OKAY){
            resultingNode = siteMap.getNode(p);
            if( resultingNode == null){
                resultingNode = siteMap.createNode(conv,p);
                final EntityID nodeID = resultingNode.getNodeID();
                HtmlPageParser tsk = new HtmlPageParser(
                        super.taskManager,
                        p,
                        new Callback3<HtmlPage,  ArrayList<DOMAction>, HTTPAction>(){
                            public void CallMeBack(HtmlPage p, ArrayList<DOMAction> domActs, HTTPAction httpAction){
                                addNewAction(nodeID,p,new CorrespondentActions(httpAction,domActs));
                            }
                        }
                );
                tsk.registerCallback(this);
                if(addTask(tsk))
                    spawnedTasks++;
                else{
                    logger.error("Could not add task!");
                    wasErr = true;
                }
            }else{
                logger.trace("Node already exists.");
            }
        }else if(respType == ResponseClassificator.ResponseType.NOT_FOUND
                || respType == ResponseClassificator.ResponseType.ERROR
                || respType == ResponseClassificator.ResponseType.PROHIBITED
        ){
            logger.trace("action leads to not found page!");
            resultingNode = siteMap.getInvalidNode();
        }else if(respType == ResponseClassificator.ResponseType.EXPIRED){
            throw new NotImplementedException("expired sessions are not yes supported");
            //TODO: do smth here!
        }else{
            throw new NotImplementedException("not yes supported");
        }
        logger.trace(from.getNodeID() + " -> " + resultingNode.getNodeID());
        HTTPAction equalHTTPAction = null;
        synchronized (performedHTTPActions){
            for(HTTPAction act: performedHTTPActions){
                if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(act,action)){
                    equalHTTPAction = act;
                    break;
                }
            }
            actionConversationMap.put(equalHTTPAction,conv);
            actionNodeMap.put(equalHTTPAction,resultingNode.getNodeID());
        }
        siteMap.addEdge(from, resultingNode, corActs, conv);
        synchronized (unresolvedActions){
            List<EntityID> unresNodes =  unresolvedActions.get(equalHTTPAction);
            if(unresNodes != null){
                for(EntityID nid : unresNodes){
                    logger.trace(nid + " -> " + resultingNode.getNodeID());  
                    siteMap.addEdge(siteMap.getNodeByID(nid),resultingNode, corActs, conv);
                }
            }
            unresolvedActions.remove(equalHTTPAction);
        }
    }

    public void start(){
        ResponseFetcher tsk = new ResponseFetcher(super.taskManager, startingAct, contextID);
        waitForTask(tsk);
        if(!tsk.isSuccessful()){
            logger.error("Could not fetch responce!!!");
            return; //no success
        }
        Conversation conv = (Conversation) tsk.getResult();
        WebClient webClient = new WebClient();                                                        
        URL origUrl = conv.getRequest().getURL();
        //1. create WebResponse from page : WebResponse(WebResponseData responseData, WebRequest request, long loadTime)
        WebResponse resp = conv.getResponse().genWebResponse(origUrl,1,conv.getRequest().genWebRequest());
        TopLevelWindow baseWindow = (TopLevelWindow) webClient.openWindow(null,"");
        try {
            webClient.loadWebResponseInto(resp, baseWindow);
        } catch (IOException ioex) {
            baseWindow.close();
            throw new IllegalStateException("Cannot receive IOException on converted Response object");
        }
        Page page = baseWindow.getEnclosedPage();
        if(page instanceof HtmlPage){
            performedHTTPActions.add(startingAct);
            ArrayList<DOMAction> act = new ArrayList<DOMAction>();
            act.add(
                    new DOMAction(
                    "",
                    DOMActionType.REFRESH)
            );
            addConversationForActionFromNode(
                    siteMap.getEntryNode().getNodeID(),
                    conv,
                    (HtmlPage)page,
                    new CorrespondentActions(startingAct,act)
            );
        }else{
            logger.warn("Returned page is not a HTML page!");
            setSuccessful(true);
            return;
        }
        synchronized(this){
            while(spawnedTasks!=0){
                try{
                    wait();
                }catch(InterruptedException ex){
                }
           }
        }
        setSuccessful(wasErr);
    }
    public Object getResult(){
        return null;
    }
}
