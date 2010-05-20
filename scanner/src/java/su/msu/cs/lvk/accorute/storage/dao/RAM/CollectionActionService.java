package su.msu.cs.lvk.accorute.storage.dao.RAM;

import com.truchsess.util.HashMapTree;
import su.msu.cs.lvk.accorute.http.model.Action;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.storage.ActionService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
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
    private final Map<EntityID, Action> database = new HashMap<EntityID, Action>();
    private Long nextId = 1l;

    public void addOrUpdateAction(Action act){
        EntityID aid = act.getActionID();
        if(aid == EntityID.NOT_INITIALIZED){
            EntityID id = new EntityID(nextId ++);
            act.setActionID(id);
            database.put(id, act);
        }else{
            Action dbAction = database.get(aid);
            if(dbAction == null){
                throw new IllegalArgumentException("Invalid actionID");
            }
            database.put(aid, act);
        }

    }
    public Action getActionByID(EntityID actID){
        Action dbAction = database.get(actID);
        if(actID == EntityID.NOT_INITIALIZED || dbAction == null){
            throw new IllegalArgumentException("invalid actionID");
        }
        return dbAction;            
    }
    public Action getFirstActionInContext(EntityID ctxID){
        throw new NotImplementedException();
    }
    public Action getLastActionInContext(EntityID ctxID){
        throw new NotImplementedException();
    }
}
