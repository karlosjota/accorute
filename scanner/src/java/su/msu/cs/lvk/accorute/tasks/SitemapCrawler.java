package su.msu.cs.lvk.accorute.tasks;

import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.decisions.ResponseClassificator;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback1;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.10.2010
 * Time: 13:33:37
 * To change this template use File | Settings | File Templates.
 */
public class SitemapCrawler extends Task {
    //TODO: Add various constraints etc...
    private final Action startingAct;
    private final EntityID contextID;
    private final Sitemap siteMap;

    public SitemapCrawler(TaskManager t, Action startReq, EntityID ctxID){
        super(t);
        siteMap = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(ctxID);
        startingAct = startReq;
        contextID = ctxID;
    }

    synchronized private void addConversationForActionFromNode(final EntityID fromNodeID, final Action action, final Conversation conv){
        ResponseClassificator.ResponseType respType =  WebAppProperties.getInstance().getRespClassificator().getResponseType(conv);
        final Sitemap.SitemapNode from = siteMap.getNodeByID(fromNodeID);
        if(respType == ResponseClassificator.ResponseType.OKAY){
            Sitemap.SitemapNode n = siteMap.getNode(conv);
            if( n == null){
                n = siteMap.createNode(conv);
                final EntityID nodeID = n.getNodeID();
                class myCb implements Callback1<Action>{
                    public void CallMeBack(Action act){
                        addNewAction(nodeID, act);  // Ho! closures break into the thread!
                    }
                };
                myCb cb = new myCb();
                ResponseParser tsk = new ResponseParser(super.taskManager, conv.getResponse(), cb);
                addTask(tsk);
            }
            siteMap.addEdge(from, n, action);
        }else if(respType == ResponseClassificator.ResponseType.NOT_FOUND
                || respType == ResponseClassificator.ResponseType.ERROR
                || respType == ResponseClassificator.ResponseType.PROHIBITED
        ){
            siteMap.addEdge(from, siteMap.getInvalidNode(), action);
        }else if(respType == ResponseClassificator.ResponseType.EXPIRED){
            //TODO: do smth here!
        }
    }

    synchronized private void  addNewAction(final EntityID fromNodeID,final Action action){
        final Sitemap.SitemapNode from = siteMap.getNodeByID(fromNodeID);
        from.addAction(action);
        if(WebAppProperties.getInstance().getChStateDec().changesState(action)){
            siteMap.addEdge(from, siteMap.getExitNode(),action);
            return;
        }
        class myCb implements Callback1<Conversation>{
                public void CallMeBack(Conversation conv){
                    addConversationForActionFromNode(fromNodeID, action, conv);  // Ho! closures break into the thread!
                }
            }
        myCb cb = new myCb();
        ResponseFetcher tsk = new ResponseFetcher(super.taskManager,action, cb);
    }

    public void start(){
        addNewAction(siteMap.getEntryNode().getNodeID(), startingAct);      
    }
    public Object getResult(){
        return null;
    }
}
