package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.browserUI.HttpElementAction;
import su.msu.cs.lvk.accorute.decisions.ResponseClassificator;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback3;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.10.2010
 * Time: 13:33:37
 * To change this template use File | Settings | File Templates.
 */
public class SitemapCrawler extends Task implements Callback0{
    //TODO: Add various constraints etc...
    private final Action startingAct;
    private final EntityID contextID;
    private final Sitemap siteMap;
    private int spawnedTasks;
    private final List<Action> performedActions = new ArrayList<Action>();
    private final Map<Action,Conversation> actionConversationMap = new HashMap<Action,Conversation> ();
    private final Map<Action,EntityID> actionNodeMap = new HashMap<Action,EntityID> ();
    private final Map<Action,List<EntityID> > unresolvedActions = new HashMap<Action,List<EntityID> >();
    private boolean wasErr;
    synchronized public void CallMeBack(){
        spawnedTasks--;
        logger.trace("task finished; "+spawnedTasks+" left");
        notify();
    }
    public SitemapCrawler(TaskManager t, Action startReq, EntityID ctxID){
        super(t);
        siteMap = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(ctxID);
        startingAct = startReq;
        contextID = ctxID;
        spawnedTasks = 0;
        wasErr = false;
    }

    private void addNewAction(
            final EntityID fromNodeID,
            HtmlPage p,
            HttpElementAction domAct,
            Action  httpAct
    ){
        if(WebAppProperties.getInstance().getChStateDec().changesState(httpAct)){
            logger.trace("Will not perform action, because it will change the state");
            final Sitemap.SitemapNode from = siteMap.getNodeByID(fromNodeID);
            siteMap.addEdge(from, siteMap.getExitNode(), httpAct, null);
            return;
        }
        synchronized (performedActions){
            Action equalAction = null;
            for(Action act: performedActions){
                if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(act,httpAct)){
                    equalAction = act;
                    break;
                }
            }
            if(equalAction != null){
                logger.trace("Will not perform action " + httpAct + " that was already performed");
                Conversation c = actionConversationMap.get(equalAction);
                EntityID nId = actionNodeMap.get(equalAction);
                if(c == null || nId == null){
                    List<EntityID> l= unresolvedActions.get(equalAction);
                    if(l == null){
                        l = new ArrayList<EntityID>();
                        unresolvedActions.put(equalAction,l);
                    }
                    l.add(fromNodeID);
                }else{
                    siteMap.addEdge(siteMap.getNodeByID(fromNodeID),siteMap.getNodeByID(nId), equalAction, c);
                }
                return;
            }
            performedActions.add(httpAct);
        }
        HtmlElementActionPerformer tsk = new HtmlElementActionPerformer(
                taskManager,
                p,
                domAct,
                contextID,
                new Callback3<Conversation, Action, HtmlPage>(){
                    public void CallMeBack(Conversation c , Action a, HtmlPage p){
                        addConversationForActionFromNode(fromNodeID,a,c,p);
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

    private void addConversationForActionFromNode(
            final EntityID fromNodeID,
            final Action action,
            final Conversation conv,
            final HtmlPage p
    ){
        logger.trace("addConversationForActionFromNode: node " + fromNodeID
                + " action " + action
                + " conv " + conv
                + " page " + p             
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
                        new Callback3<HtmlPage, HttpElementAction, Action>(){
                            public void CallMeBack(HtmlPage p, HttpElementAction domAct, Action httpAction){
                                addNewAction(nodeID,p,domAct,httpAction);
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
            throw new NotImplementedException("expired sessions are not yes supported");
        }
        logger.trace(from.getNodeID() + " -> " + resultingNode.getNodeID());
        Action equalAction = null;
        synchronized (performedActions){
            for(Action act: performedActions){
                if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(act,action)){
                    equalAction = act;
                    break;
                }
            }
            actionConversationMap.put(equalAction,conv);
            actionNodeMap.put(equalAction,resultingNode.getNodeID());
        }
        siteMap.addEdge(from, resultingNode, equalAction, conv);
        synchronized (unresolvedActions){
            List<EntityID> unresNodes =  unresolvedActions.get(equalAction);
            if(unresNodes != null){
                for(EntityID nid : unresNodes){
                    logger.trace(nid + " -> " + resultingNode.getNodeID());  
                    siteMap.addEdge(siteMap.getNodeByID(nid),resultingNode, equalAction, conv);
                }
            }
            unresolvedActions.remove(equalAction);
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
            performedActions.add(startingAct);
            addConversationForActionFromNode(siteMap.getEntryNode().getNodeID(), startingAct, conv, (HtmlPage)page);
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
