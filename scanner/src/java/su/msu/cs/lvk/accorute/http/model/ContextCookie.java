package su.msu.cs.lvk.accorute.http.model;

import org.apache.commons.httpclient.Cookie;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 20.04.2010
 * Time: 21:43:28
 * To change this template use File | Settings | File Templates.
 */
public class ContextCookie extends Cookie {
    private EntityID ctxID = EntityID.NOT_INITIALIZED;

    public EntityID getCtxID() {
        return ctxID;
    }

    public void setCtxID(EntityID ctxID) {
        this.ctxID = ctxID;
    }

    public ContextCookie() {
        super();
    }

    public ContextCookie(java.lang.String domain, java.lang.String name, java.lang.String value) {
        super(domain,name,value);
    }

    public ContextCookie(java.lang.String domain, java.lang.String name, java.lang.String value, java.lang.String path, java.util.Date expires, boolean secure) {
        super(domain,name,value,path,expires, secure);
    }

    public ContextCookie(java.lang.String domain, java.lang.String name, java.lang.String value, java.lang.String path, int maxAge, boolean secure) {
        super(domain,name,value,path,maxAge, secure);
    }

    public ContextCookie(Cookie other){
        super(other.getDomain(),other.getName(), other.getValue(), other.getPath(), other.getExpiryDate(), other.getSecure());

    }
}
