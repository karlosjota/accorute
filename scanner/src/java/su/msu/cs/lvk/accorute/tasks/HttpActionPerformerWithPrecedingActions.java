package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.HttpAction;
import su.msu.cs.lvk.accorute.http.model.Sitemap;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.12.2010
 * Time: 0:21:06
 * To change this template use File | Settings | File Templates.
 */
public class HttpActionPerformerWithPrecedingActions extends Task{
    EntityID ctxID;
    HttpAction act;
    public HttpActionPerformerWithPrecedingActions(TaskManager t,  EntityID ctxID, HttpAction act) {
        this(t, ctxID,act, false);
    }
    public HttpActionPerformerWithPrecedingActions(TaskManager t,  EntityID ctxID, HttpAction act,boolean ser) {
        super(t, ser);
        this.ctxID = ctxID;
        this.act = act;
    }

    @Override
    public Object getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void start() {
        Sitemap smap = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(ctxID);
        Sitemap.SitemapNode n = smap.getNodePreceedingNeededAction(act);
        if(n == null){
            logger.error("Action " + act + " not found in the sitemap!!!");
            setSuccessful(false);
            return;
        }
        Set<HttpAction> preActs = smap.getInbound(n);
        if(preActs.size() == 0){
            logger.error("Inbound actions are absent!");
            setSuccessful(false);
            return;
        }
        HttpAction preAct = preActs.iterator().next();

        Task preTask = new HtmlElementActionPerformer(
                    taskManager,
                    preAct,
                    ctxID,
                    new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                        public void CallMeBack(ArrayList<Conversation> c , ArrayList<HttpAction> a, HtmlPage p){
                            // do nothing
                        }
                    }
        );
        waitForTask(preTask);
        if(!preTask.isSuccessful()){
            logger.error("Pre task not successful!");
            setSuccessful(false);
            return;
        }
        Task task = new HtmlElementActionPerformer(
                    taskManager,
                    act,
                    ctxID,
                    new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                        public void CallMeBack(ArrayList<Conversation> c , ArrayList<HttpAction> a, HtmlPage p){
                            // do nothing
                        }
                    }
        );
        waitForTask(task);
        if(!task.isSuccessful()){
            logger.error("task not successful!");
            setSuccessful(false);
        }else{
            setSuccessful(true);
        }
    }
}
