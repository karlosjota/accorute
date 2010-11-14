package su.msu.cs.lvk.accorute.storage.dao.RAM;

import su.msu.cs.lvk.accorute.http.model.HTTPAction;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.storage.ActionService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.04.2010
 * Time: 17:20:54
 * To change this template use File | Settings | File Templates.
 */
public class CollectionActionService implements ActionService{
    private final Map<EntityID, HTTPAction> database = new HashMap<EntityID, HTTPAction>();
    private Long nextId = 1l;

    public void addOrUpdateAction(HTTPAction act){
        EntityID aid = act.getActionID();
        if(aid == EntityID.NOT_INITIALIZED){
            EntityID id = new EntityID(nextId ++);
            act.setActionID(id);
            database.put(id, act);
        }else{
            HTTPAction dbAction = database.get(aid);
            if(dbAction == null){
                throw new IllegalArgumentException("Invalid actionID");
            }
            database.put(aid, act);
        }

    }
    public HTTPAction getActionByID(EntityID actID){
        HTTPAction dbAction = database.get(actID);
        if(actID == EntityID.NOT_INITIALIZED || dbAction == null){
            throw new IllegalArgumentException("invalid actionID");
        }
        return dbAction;            
    }
    public HTTPAction getFirstActionInContext(EntityID ctxID){
        throw new NotImplementedException();
    }
    public HTTPAction getLastActionInContext(EntityID ctxID){
        throw new NotImplementedException();
    }
}
