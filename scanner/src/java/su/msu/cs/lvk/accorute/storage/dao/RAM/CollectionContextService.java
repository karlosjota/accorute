package su.msu.cs.lvk.accorute.storage.dao.RAM;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.UserContext;
import su.msu.cs.lvk.accorute.storage.ContextService;
import su.msu.cs.lvk.accorute.utils.CallbackContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.04.2010
 * Time: 19:09:42
 * To change this template use File | Settings | File Templates.
 */
public class CollectionContextService  extends CallbackContainer implements ContextService {
    private final Map<EntityID, UserContext> database = new HashMap<EntityID, UserContext>();
    private Long nextId = 1l;

    public void addOrUpdateContext(UserContext ctx){
        EntityID cid = ctx.getContextID();
        if(cid == EntityID.NOT_INITIALIZED){
            EntityID id = new EntityID(nextId ++);
            ctx.setContextID(id);
            database.put(id, ctx);
        }else{
            UserContext dbUserContext = database.get(cid);
            if(dbUserContext == null){
                throw new IllegalArgumentException("Invalid UserContextID");
            }
            database.put(cid, ctx);
        }
        notifyCallbacks();
    }
    public UserContext getContextByID(EntityID actID){
        UserContext dbUserContext = database.get(actID);
        if(actID == EntityID.NOT_INITIALIZED || dbUserContext == null){
            throw new IllegalArgumentException("invalid UserContextID");
        }
        return dbUserContext;            
    }

    public List<UserContext> getContextsByUserID(EntityID userID) {
        List<UserContext> l = new ArrayList<UserContext>();
        for(UserContext u: database.values()){
            if(u.getUserID().equals(userID)){
                l.add(u);
            }
        }
        return l;
    }

}
