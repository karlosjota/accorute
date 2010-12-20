package su.msu.cs.lvk.accorute.storage;

import su.msu.cs.lvk.accorute.http.model.ContextCookie;
import su.msu.cs.lvk.accorute.http.model.CookieDescriptor;
import su.msu.cs.lvk.accorute.http.model.EntityID;

import java.net.URL;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.04.2010
 * Time: 11:58:29
 * To change this template use File | Settings | File Templates.
 */
public interface CookieService {
    Collection<ContextCookie> getCookiesForUrlInContext(EntityID ctxID, URL url);

    Collection<ContextCookie> getCookiesInContext(EntityID ctxID);

    void clearCookiesInContext(EntityID ctxID);

    void setCookies(CookieDescriptor cookies);
}