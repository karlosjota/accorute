package su.msu.cs.lvk.accorute.storage;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.Sitemap;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.04.2010
 * Time: 17:08:20
 * To change this template use File | Settings | File Templates.
 */
public interface SitemapService {
    public void setSitemapForContext(EntityID ctxID, Sitemap map);
    public Sitemap getSitemapForContext(EntityID ctxID);
}
