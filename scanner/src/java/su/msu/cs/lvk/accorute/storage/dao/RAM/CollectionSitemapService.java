package su.msu.cs.lvk.accorute.storage.dao.RAM;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.Sitemap;
import su.msu.cs.lvk.accorute.storage.SitemapService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 20.04.2010
 * Time: 16:55:06
 * To change this template use File | Settings | File Templates.
 */
public class CollectionSitemapService implements SitemapService {
    private final Map<EntityID, Sitemap> database = new HashMap<EntityID, Sitemap>();

    public void setSitemapForContext(EntityID ctxID, Sitemap map){
        database.put(ctxID, map);
    }
    public Sitemap getSitemapForContext(EntityID ctxID){
        Sitemap map = database.get(ctxID);
        if(map == null){
            map = new Sitemap(ctxID);
            database.put(ctxID, map);
        }
        return map;
    }
}
