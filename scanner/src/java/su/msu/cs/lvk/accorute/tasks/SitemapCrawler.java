package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.DomAction;
import su.msu.cs.lvk.accorute.decisions.ResponseClassificator;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.Callback4;

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
    private final HttpAction startingAct;
    private final EntityID contextID;
    private final Sitemap siteMap;
    private int spawnedTasks;
    private final List<HttpAction> performedHttpActions = new ArrayList<HttpAction>();
    private final Map<HttpAction,ArrayList<Conversation> > actionConversationMap = new HashMap<HttpAction,ArrayList<Conversation> > ();
    private final Map<HttpAction,ArrayList<HttpAction> > actionActionChainMap = new HashMap<HttpAction,ArrayList<HttpAction>> ();
    private final Map<HttpAction,EntityID> actionNodeMap = new HashMap<HttpAction,EntityID> ();
    private final Map<HttpAction,List<EntityID> > unresolvedActions = new HashMap<HttpAction,List<EntityID> >();
    private boolean wasErr;
    synchronized public void CallMeBack(){
        spawnedTasks--;
        logger.trace("task finished; "+spawnedTasks+" left");
        notify();
    }
    public SitemapCrawler(TaskManager t, HttpAction startReq, EntityID ctxID){
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
            final CorrespondentActions acts,
            final boolean isAjax
    ){
        ArrayList<HttpAction> httpActs = acts.getHttpActions();
        if(httpActs.size() != 1){
            logger.error("addNewAction: trying to add multiple actions at once, ignoring!");
            return;
        }
        HttpAction httpAct = httpActs.get(0);
        String url = WebAppProperties.getInstance().getRcd().getURL(httpAct.getActionParameters()).toString();
        Matcher m = WebAppProperties.getInstance().getScope().matcher(url);
        if(!m.matches()){
            logger.trace("Will not access url "+ url+" because it's out of scope");
            siteMap.addEdge(siteMap.getNodeByID(fromNodeID), siteMap.getExitNode(), acts, null);
            return;
        }
        if(WebAppProperties.getInstance().getChStateDec().changesState(httpAct)){
            logger.trace("Will not perform action because it will change the state");
            final Sitemap.SitemapNode from = siteMap.getNodeByID(fromNodeID);
            siteMap.addEdge(from, siteMap.getExitNode(), acts, null);
            return;
        }
        synchronized (performedHttpActions){
            HttpAction equalHttpAction = null;
            if(!isAjax){// if ajax, we cannot rely on cache
                for(HttpAction act: performedHttpActions){
                    if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(act,httpAct)){
                        equalHttpAction = act;
                        break;
                    }
                }
                if(equalHttpAction != null){
                    logger.trace("Will not perform action " + httpAct + " that was already performed");
                    ArrayList<Conversation>  c = actionConversationMap.get(equalHttpAction);
                    ArrayList<HttpAction> chain = actionActionChainMap.get(equalHttpAction);
                    EntityID nId = actionNodeMap.get(equalHttpAction);
                    if(c == null || nId == null){
                        List<EntityID> l= unresolvedActions.get(equalHttpAction);
                        if(l == null){
                            l = new ArrayList<EntityID>();
                            unresolvedActions.put(equalHttpAction,l);
                        }
                        l.add(fromNodeID);
                    }else{
                        siteMap.addEdge(
                                siteMap.getNodeByID(fromNodeID),
                                siteMap.getNodeByID(nId),
                                new CorrespondentActions(chain,acts.getDomActions() ),
                                c
                        );
                    }
                    return;
                }
                performedHttpActions.add(httpAct);
            }
        }
        HtmlElementActionPerformer tsk = new HtmlElementActionPerformer(
                taskManager,
                p,
                acts.getDomActions(),
                contextID,
                new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                    public void CallMeBack(ArrayList<Conversation> c , ArrayList<HttpAction> a, HtmlPage p){
                        addConversationsForActionFromNode(fromNodeID,c,p,new CorrespondentActions(a,acts.getDomActions()), isAjax);
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

    synchronized private void addConversationsForActionFromNode(
            final EntityID fromNodeID,
            final ArrayList<Conversation> convs,
            final HtmlPage p,
            final CorrespondentActions corActs,
            boolean wasAjax
    ){
        final ArrayList<DomAction> domActs = corActs.getDomActions();
        if(domActs.size() < 0 || convs.size() < 0 || corActs.getHttpActions().size() != convs.size()){
            logger.error("invalid params: " +domActs + " " + convs);
            return;
        }
        logger.trace("addConversationForActionFromNode: node " + fromNodeID
                + " actions " + corActs.getHttpActions()
                + " page " + p
                + " convs " + convs
        );
        if(convs.size() > 1){
            logger.info("Multi-request action, will take into account only first one");
        }
        Conversation conv = convs.get(convs.size() - 1);
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
                        new Callback4<HtmlPage,  ArrayList<DomAction>, HttpAction, Boolean>(){
                            public void CallMeBack(HtmlPage p, ArrayList<DomAction> domActs, HttpAction httpAction, Boolean isAjax){
                                ArrayList<HttpAction> acts = new  ArrayList<HttpAction>();
                                acts.add(httpAction);
                                addNewAction(nodeID,p,new CorrespondentActions(acts,domActs), isAjax);
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
        HttpAction equalHttpAction = null;
        if(!wasAjax){
            final HttpAction action = corActs.getHttpActions().get(0);
            synchronized (performedHttpActions){
                for(HttpAction act: performedHttpActions){
                    if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(act,action)){
                        equalHttpAction = act;
                        break;
                    }
                }
                actionConversationMap.put(equalHttpAction,convs);
                actionNodeMap.put(equalHttpAction,resultingNode.getNodeID());
                actionActionChainMap.put(action,corActs.getHttpActions());
            }      
            synchronized (unresolvedActions){
                List<EntityID> unresNodes =  unresolvedActions.get(equalHttpAction);
                if(unresNodes != null){
                    for(EntityID nid : unresNodes){
                        logger.trace(nid + " -> " + resultingNode.getNodeID());
                        siteMap.addEdge(siteMap.getNodeByID(nid),resultingNode, corActs, convs);
                    }
                }
                unresolvedActions.remove(equalHttpAction);
            }
        }
        siteMap.addEdge(from, resultingNode, corActs, convs);
    }

    public void start(){
        performedHttpActions.add(startingAct);
        HtmlElementActionPerformer tsk = new HtmlElementActionPerformer(
                taskManager,
                startingAct,
                contextID,
                new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                    public void CallMeBack(ArrayList<Conversation> c , ArrayList<HttpAction> a, HtmlPage p){
                        ArrayList<DomAction > lst = new ArrayList<DomAction>();
                        lst.add(new DomAction("", DomActionType.REFRESH));
                        addConversationsForActionFromNode(
                                siteMap.getEntryNode().getNodeID(),
                                c,
                                p,
                                new CorrespondentActions(a,lst),
                                false
                        );
                    }
                }
        );
        tsk.registerCallback(this);
        if(addTask(tsk))
            spawnedTasks++;
        else{
            logger.error("Could not add task!");
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
