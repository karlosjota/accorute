package su.msu.cs.lvk.accorute.tasks;

import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.decisions.ResponseClassificator;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.util.HashMap;
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
    private final Map<EntityID, Set<HttpAction> > spikesByContext;
    public SimpleDetectSpikes(TaskManager t, EntityID ctx1, EntityID ctx2) {
        super(t, false);
        ctxID1 = ctx1;
        ctxID2 = ctx2;
        spikesByContext = new HashMap<EntityID, Set<HttpAction> >();
    }

    @Override
    public Object getResult() {
        return spikesByContext;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Set<HttpAction> detectSpikes(EntityID attackCtx, Set<HttpAction> testActs, Map<HttpAction,Conversation> map){
        Set<HttpAction> spikes = new HashSet<HttpAction>();
        for(HttpAction act: testActs){
            boolean successful = false;
            ResponseFetcher fetcher = new ResponseFetcher(taskManager, act,attackCtx);
            waitForTask(fetcher);
            if(fetcher.isSuccessful()){
                Conversation сonv = (Conversation) fetcher.getResult();
                if(WebAppProperties.getInstance().getAgd().accessWasGranted(map.get(act),сonv))
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
        test12.removeAll(acts1);
        Set<HttpAction> test21 = new HashSet<HttpAction>(acts1);
        test21.removeAll(acts2);
        spikesByContext.put(ctxID1,detectSpikes(ctxID1,test12,map2));
        spikesByContext.put(ctxID2,detectSpikes(ctxID2,test21,map1));
        setSuccessful(true);
    }
}
