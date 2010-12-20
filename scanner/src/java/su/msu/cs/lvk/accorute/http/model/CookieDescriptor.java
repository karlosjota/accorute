/*
* CookieDescriptor.java
*
*/

package su.msu.cs.lvk.accorute.http.model;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.cookie.RFC2109Spec;
import org.apache.http.impl.cookie.RFC2965Spec;
import su.msu.cs.lvk.accorute.http.constants.HTTPHeader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CookieDescriptor {
    private List<ContextCookie> cookies;
    private CookieOrigin origin;
    private CookieSpec spec;
    private String headerName;
    private EntityID ctxID = EntityID.NOT_INITIALIZED;

    public EntityID getCtxID() {
        return ctxID;
    }
    public void setCtxID(EntityID ctxID) {
        this.ctxID = ctxID;
    }


    public CookieDescriptor(Collection<Cookie> cookies, CookieOrigin origin, EntityID ctxID) {
        this(cookies, origin,"Set-Cookie",ctxID);
    }

    /**
     * Creates a new instance of Cookie
     *
     * @param cookies
     * @param origin
     * @param cookieHeaderName
     */
    public CookieDescriptor(Collection<Cookie> cookies, CookieOrigin origin, String cookieHeaderName, EntityID ctxID) {
        this.ctxID = ctxID;
        List<ContextCookie> list = new ArrayList<ContextCookie>();
        for(Cookie cook: cookies){
            ContextCookie newcook = new ContextCookie(cook);
            newcook.setCtxID(this.ctxID);
            list.add(newcook);
        }
        this.cookies = list;
        this.origin = origin;
        this.headerName = cookieHeaderName;
        this.spec = HTTPHeader.HTTP_SET_COOKIE2.equalsIgnoreCase(cookieHeaderName) ?  new RFC2965Spec() : new RFC2109Spec();
    }

    public List<Cookie> getCookies() {
        List<Cookie> cooks = new ArrayList<Cookie>();
        cooks.addAll(cookies);
        return cooks;
    }
    public List<ContextCookie> getCtxCookies() {
        return cookies;
    }

    public CookieOrigin getOrigin() {
        return origin;
    }

    public CookieSpec getSpec() {
        return spec;
    }

    public String getHeaderName() {
        return headerName;
    }
}
