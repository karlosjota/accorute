/*
* CookieDescriptor.java
*
*/

package su.msu.cs.lvk.accorute.http.model;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.cookie.CookieOrigin;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import su.msu.cs.lvk.accorute.http.constants.HTTPHeader;

import java.util.ArrayList;
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


    /**
     * Creates a new instance of Cookie
     *
     * @param cookies
     * @param origin
     * @param cookieHeaderName
     */
    public CookieDescriptor(List<Cookie> cookies, CookieOrigin origin, String cookieHeaderName, EntityID ctxID) {
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
        this.spec = CookiePolicy.getCookieSpec((HTTPHeader.HTTP_SET_COOKIE2.equalsIgnoreCase(cookieHeaderName) ? CookiePolicy.RFC_2965 : CookiePolicy.RFC_2109));
    }

    public List<ContextCookie> getCookies() {
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
