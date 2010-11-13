package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.browserUI.HttpElementAction;
import su.msu.cs.lvk.accorute.decisions.ResponseClassificator;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback2;
import su.msu.cs.lvk.accorute.utils.Callback3;

import java.io.IOException;
import java.net.URL;

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
            HttpElementAction act
    ){
        //TODO: check if the action wasn't performed before
        HtmlElementActionPerformer tsk = new HtmlElementActionPerformer(
                taskManager,
                p,
                act,
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
        if(respType == ResponseClassificator.ResponseType.OKAY){
            Sitemap.SitemapNode n = siteMap.getNode(p);
            if( n == null){
                n = siteMap.createNode(conv,p);
                final EntityID nodeID = n.getNodeID();
                HtmlPageParser tsk = new HtmlPageParser(
                        super.taskManager,
                        p,
                        new Callback2<HtmlPage, HttpElementAction>(){
                            public void CallMeBack(HtmlPage p, HttpElementAction a){
                                addNewAction(nodeID,p,a);
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
            siteMap.addEdge(from, n, action, conv);
            logger.trace(from.getNodeID() + " -> " + n.getNodeID());
        }else if(respType == ResponseClassificator.ResponseType.NOT_FOUND
                || respType == ResponseClassificator.ResponseType.ERROR
                || respType == ResponseClassificator.ResponseType.PROHIBITED
        ){
            logger.trace("action leads to not found page!");
            siteMap.addEdge(from, siteMap.getInvalidNode(), action, conv);
        }else if(respType == ResponseClassificator.ResponseType.EXPIRED){
            //TODO: do smth here!
        }else{
            logger.fatal("WTF?");
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
