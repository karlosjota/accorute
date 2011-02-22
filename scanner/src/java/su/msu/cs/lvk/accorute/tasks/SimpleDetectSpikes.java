package su.msu.cs.lvk.accorute.tasks;

import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.HttpAction;
import su.msu.cs.lvk.accorute.http.model.Sitemap;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            ResponseFetcher fetcher = new ResponseFetcher(taskManager, act,attackCtx);
            waitForTask(fetcher);
            if(fetcher.isSuccessful()){
                Conversation conv = (Conversation) fetcher.getResult();
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
        logger.trace("S1 :" + s1);
        System.out.println("S2 :" + s2);
        Map<HttpAction,Conversation> map1 = s1.getValidHttpActions();
        Map<HttpAction,Conversation> map2 = s2.getValidHttpActions();
        Set<HttpAction> acts1 = map1.keySet();
        Set<HttpAction> acts2 = map2.keySet();
        Set<HttpAction> test12 = new HashSet<HttpAction>(acts2);
        test12.removeAll(acts1);
        logger.trace("Test set "+ctxID1 + "->" + ctxID2+ test12);
        Set<HttpAction> sp1 = detectSpikes(ctxID1,test12,map2);
        logger.trace("Spikes "+ctxID1 + "->" + ctxID2+ sp1);
        res.addAll(sp1);
        setSuccessful(true);
    }
}
