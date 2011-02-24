package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback3;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 25.11.2010
 * Time: 1:21:17
 * To change this template use File | Settings | File Templates.
 */
public class SimpleDetectSpikes extends Task{
    private final EntityID ctxID1;
    private final EntityID ctxID2;
    private final Set<HttpAction>  res = new HashSet<HttpAction>();
    public SimpleDetectSpikes(TaskManager t, EntityID ctx1, EntityID ctx2) {
        super(t, false);
        ctxID1 = ctx1;
        ctxID2 = ctx2;
    }

    @Override
    public Object getResult() {
        return res;
    }

    private Set<HttpAction> detectSpikes(EntityID attackCtx, Set<HttpAction> testActs, Map<HttpAction,Conversation> map){
        Set<HttpAction> spikes = new HashSet<HttpAction>();
        for(HttpAction act: testActs){
            boolean successful = false;
            final List<Conversation> convs = new ArrayList<Conversation>();
            HtmlElementActionPerformer performer = new HtmlElementActionPerformer(
                taskManager,
                act,
                attackCtx,
                    new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                        public void CallMeBack(ArrayList<Conversation> convers, ArrayList<HttpAction> acts, HtmlPage page) {
                            convs.add(convers.get(0));
                        }
                    }
            );
            waitForTask(performer);
            if(performer.isSuccessful()){
                Conversation conv = convs.get(0);
                if(WebAppProperties.getInstance().getAgd().accessWasGranted(map.get(act), conv))
                    successful = true;
            }
            if(successful){
                spikes.add(act);
            }
        }
        return spikes;
    }

    @Override
    protected void start() {
        Sitemap s1 = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(ctxID1);
        Sitemap s2 = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(ctxID2);
        Map<HttpAction,Conversation> map1 = s1.getValidHttpActions();
        Map<HttpAction,Conversation> map2 = s2.getValidHttpActions();
        Set<HttpAction> acts1 = map1.keySet();
        Set<HttpAction> acts2 = map2.keySet();
        Set<HttpAction> test12 = new HashSet<HttpAction>(acts2);
        Iterator<HttpAction> it = acts1.iterator();
        Set<HttpAction> toDelete = new HashSet<HttpAction>();
        logger.trace("acts1 :" + acts1);
        logger.trace("acts2 :" + acts2);
        while(it.hasNext()){
            HttpAction act = it.next();
            Iterator<HttpAction> it2 = test12.iterator();
            while(it2.hasNext()){
                HttpAction act2 = it2.next();
                if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(act,act2))
                    toDelete.add(act2);
            }
            test12.removeAll(toDelete);
        }
        logger.trace("Test set "+ctxID1 + "->" + ctxID2+ test12);
        Set<HttpAction> sp1 = detectSpikes(ctxID1,test12,map2);
        logger.trace("Spikes "+ctxID1 + "->" + ctxID2+ sp1);
        res.addAll(sp1);
        setSuccessful(true);
    }
}
