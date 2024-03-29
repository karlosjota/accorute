package su.msu.cs.lvk.accorute.storage.dao.RAM;

import org.apache.commons.collections.map.MultiValueMap;
import su.msu.cs.lvk.accorute.http.model.ContextCookie;
import su.msu.cs.lvk.accorute.http.model.CookieDescriptor;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.storage.CookieService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.04.2010
 * Time: 19:12:52
 * To change this template use File | Settings | File Templates.
 */

public class CollectionCookieService implements CookieService{
    private final MultiValueMap database = new MultiValueMap();
    private Long nextId = 1l;
    
    public Collection<ContextCookie> getCookiesForUrlInContext(EntityID ctxID, URL url){
        if(! database.containsKey(ctxID)){
            return null;
        }
        Collection<ContextCookie> cookies = database.getCollection(ctxID);
        Collection<ContextCookie> result = new ArrayList<ContextCookie>();
        String path = url.getPath();
        String host = url.getHost();
        if(path.length() == 0)
            path = "/";
        for(ContextCookie cook : cookies){
            String pth = cook.getPath();
            String hst = cook.getDomain();
            if(hst.length() <= host.length() && host.endsWith(hst) &&
               pth.length() <= path.length() && path.startsWith(pth))
            {
                result.add(cook);
            }
        }
        return result;
    }

    public Collection<ContextCookie> getCookiesInContext(EntityID ctxID){
        return database.getCollection(ctxID);
    }
    public void setCookies(CookieDescriptor cookies){
        EntityID ctx = cookies.getCtxID();
        List<ContextCookie> toAdd = new ArrayList<ContextCookie>();
        for(ContextCookie cook: cookies.getCtxCookies()){
            ContextCookie newcook = new ContextCookie(cook);
            if(newcook.getDomain()==null || newcook.getDomain().equals("")){
                newcook.setDomain(cookies.getOrigin().getHost());
            }
            if(newcook.getPath()==null || newcook.getPath().equals("")){
                newcook.setPath("/");
            }
            newcook.setCtxID(ctx);
            toAdd.add(newcook);
        }
        if(database.containsKey(ctx)){
            Collection<ContextCookie> match = new ArrayList<ContextCookie>();
            Collection<ContextCookie> existingCooks = database.getCollection(ctx);
            for(ContextCookie newcook: toAdd){
                for(ContextCookie cook: existingCooks){
                    if(cook.getDomain().equals(newcook.getDomain()) &&
                            cook.getPath().equals(newcook.getPath()) &&
                            cook.getName().equals(newcook.getName())){
                        match.add(cook);
                        break;
                    }
                }
            }
            for(ContextCookie toRemove: match){
                database.remove(ctx,toRemove);
            }
        }
        database.putAll(ctx,toAdd);
    }
    public void clearCookiesInContext(EntityID ctxID){
        database.remove(ctxID);
    }

}
