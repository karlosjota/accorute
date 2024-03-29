package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.WebAppProperties;
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
    private final HtmlPage startPage;
    private final List<Task> pendingTasks = Collections.synchronizedList(new LinkedList<Task>());
    synchronized public void CallMeBack(){
        spawnedTasks--;
        logger.trace(contextID + ":task finished; "+spawnedTasks+" left");
        notify();
    }
    public SitemapCrawler(TaskManager t, HttpAction startReq, EntityID ctxID){
        super(t);
        siteMap = new Sitemap(ctxID);
        startingAct = startReq;
        startPage = null;
        contextID = ctxID;
        spawnedTasks = 0;
        wasErr = false;
    }
    public SitemapCrawler(TaskManager t, HtmlPage startPage, EntityID ctxID){
            super(t);
            siteMap = new Sitemap(ctxID);
            this.startPage = startPage;
            startingAct = null;
            contextID = ctxID;
            spawnedTasks = 0;
            wasErr = false;
        }
    synchronized private void addNewAction(
            final EntityID fromNodeID,
            final HtmlPage p,
            final CorrespondentActions acts,
            final boolean cancellable
    ){
        ArrayList<HttpAction> httpActs = acts.getHttpActions();
        if(httpActs.size() != 1){
            logger.error(contextID + ":addNewAction: trying to add multiple actions at once, ignoring!");
            return;
        }
        HttpAction httpAct = httpActs.get(0);
        URL u = WebAppProperties.getInstance().getRcd().getURL(httpAct.getActionParameters());
        synchronized (performedHttpActions){
            HttpAction equalHttpAction = null;
            if(!cancellable){
                for(HttpAction act: performedHttpActions){
                    if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(act,httpAct)){
                        equalHttpAction = act;
                        break;
                    }
                }
                if(equalHttpAction != null){
                    logger.trace(contextID + ":Will not perform action " + httpAct + " that was already performed");
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
                                new CorrespondentActions(acts.getHttpActions(),acts.getDomActions() ),
                                c
                        );
                    }
                    return;
                }
                performedHttpActions.add(httpAct);
            }
        }
        String url = WebAppProperties.getInstance().getRcd().getURL(httpAct.getActionParameters()).toString();
        Matcher urlincl = WebAppProperties.getInstance().getUrlIncludeScope().matcher(url);
        Matcher urlexcl = WebAppProperties.getInstance().getUrlExcludeScope().matcher(url);
        if(!urlincl.matches() || urlexcl.matches()){
            logger.trace(contextID + ":Will not access url "+ url+" because it's out of scope");
            siteMap.addEdge(siteMap.getNodeByID(fromNodeID), siteMap.getInvalidNode(), acts, null);
            return;
        }
        if(WebAppProperties.getInstance().getChStateDec().changesState(httpAct)){
            logger.trace(contextID + ":Will not perform action because it will change the state");
            final Sitemap.SitemapNode from = siteMap.getNodeByID(fromNodeID);
            siteMap.addEdge(from, siteMap.getExitNode(), acts, null);
            return;
        }
        HtmlElementActionPerformer tsk = new HtmlElementActionPerformer(
                taskManager,
                p,
                acts.getDomActions(),
                contextID,
                new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                    public void CallMeBack(ArrayList<Conversation> c , ArrayList<HttpAction> a, HtmlPage p){
                        addConversationsForActionFromNode(fromNodeID,c,p,new CorrespondentActions(a,acts.getDomActions()), cancellable);
                    }
                }
        );
        tsk.registerCallback(this);
        pendingTasks.add(tsk);
    }

    synchronized private void addConversationsForActionFromNode(
            final EntityID fromNodeID,
            final ArrayList<Conversation> convs,
            final HtmlPage p,
            final CorrespondentActions corActs,
            boolean wasCancellable
    ){
        final ArrayList<DomAction> domActs = corActs.getDomActions();
        if(domActs.size() < 0 || convs.size() < 0 || corActs.getHttpActions().size() != convs.size()){
            logger.error(contextID + ":invalid params: " +domActs + " " + convs);
            return;
        }
        logger.trace(contextID + ":addConversationForActionFromNode: node " + fromNodeID
                + " actions " + corActs.getHttpActions()
                + " page " + p
                + " convs " + convs
        );
        if(convs.size() > 1){
            logger.info(contextID + ":Multi-request action, will take into account only first one");
        }
        Conversation conv = convs.get(0);
        Matcher respExcl = WebAppProperties.getInstance().getResponceExcludeScope().matcher(conv.getResponse().toString());

        ResponseClassificator.ResponseType respType =  WebAppProperties.getInstance().getRespClassificator().getResponseType(conv);
        final Sitemap.SitemapNode from = siteMap.getNodeByID(fromNodeID);
        Sitemap.SitemapNode resultingNode;
        if(respExcl.matches()){
            logger.trace(contextID + ":Will not process response "+ conv.getResponse()+" because it's out of scope");
            resultingNode = siteMap.getExitNode();
        }else if(respType == ResponseClassificator.ResponseType.OKAY){
            resultingNode = siteMap.getNode(p);
            if( resultingNode == null){
                resultingNode = siteMap.createNode(conv,p);
                final EntityID nodeID = resultingNode.getNodeID();
                HtmlPageParser tsk = new HtmlPageParser(
                        super.taskManager,
                        p,
                        contextID,
                        new Callback4<HtmlPage,  ArrayList<DomAction>, HttpAction, Boolean>(){
                            public void CallMeBack(HtmlPage p, ArrayList<DomAction> domActs, HttpAction httpAction, Boolean isAjax){
                                ArrayList<HttpAction> acts = new  ArrayList<HttpAction>();
                                acts.add(httpAction);
                                addNewAction(nodeID,p,new CorrespondentActions(acts,domActs), isAjax);
                            }
                        }
                );
                tsk.registerCallback(this);
                pendingTasks.add(tsk);
            }else{
                logger.trace(contextID + ":Node already exists.");
            }
        }else if(respType == ResponseClassificator.ResponseType.NOT_FOUND
                || respType == ResponseClassificator.ResponseType.ERROR
                || respType == ResponseClassificator.ResponseType.PROHIBITED
                || respType == ResponseClassificator.ResponseType.EXPIRED
        ){
            logger.trace(contextID + ":action leads to page that isn't OK!");
            resultingNode = siteMap.getInvalidNode();
        }else{
            throw new NotImplementedException("not yet supported");
        }
        logger.trace(from.getNodeID() + " -> " + resultingNode.getNodeID());
        HttpAction equalHttpAction = null;
        if(!wasCancellable){
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
                actionActionChainMap.put(equalHttpAction,corActs.getHttpActions());
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
        Task firstTask;
        if(startingAct != null){
            performedHttpActions.add(startingAct);
            firstTask = new HtmlElementActionPerformer(
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
        }else{
            firstTask = new HtmlPageParser(
                    super.taskManager,
                    startPage,
                    contextID,
                    new Callback4<HtmlPage,  ArrayList<DomAction>, HttpAction, Boolean>(){
                        public void CallMeBack(HtmlPage p, ArrayList<DomAction> domActs, HttpAction httpAction, Boolean isAjax){
                            ArrayList<HttpAction> acts = new  ArrayList<HttpAction>();
                            acts.add(httpAction);
                            addNewAction(siteMap.getEntryNode().getNodeID(),p,new CorrespondentActions(acts,domActs), isAjax);
                        }
                    }
            );
        }
        firstTask.registerCallback(this);
        if(addWaitedTask(firstTask))
            spawnedTasks++;
        else{
            logger.error(contextID + ":Could not add task!");
            wasErr = true;
        }
        synchronized(this){
            while(true){
                while(spawnedTasks!=0){
                    try{
                        wait();
                    }catch(InterruptedException ex){
                    }
                }
                if(pendingTasks.size() == 0)
                    break;
                //ListIterator<Task> iter = pendingTasks.listIterator();
                int size = pendingTasks.size();
                //while(iter.hasNext()){
                for(int i = 0 ; i < size ; i ++){
                    /*if(addWaitedTask(iter.next()))
                        spawnedTasks++;
                    else{
                        logger.error(contextID + ":Could not add task!");
                        wasErr = true;
                    } */
                    spawnedTasks++;
                    waitForTask(pendingTasks.get(i));
                    //iter.remove();
                //}
                }
                for(int i = 0 ; i < size ; i ++)
                    pendingTasks.remove(0);
            }
        }
        WebAppProperties.getInstance().getSitemapService().setSitemapForContext(contextID,siteMap);
        setSuccessful(!wasErr);
    }
    public Object getResult(){
        return siteMap;
    }
}
