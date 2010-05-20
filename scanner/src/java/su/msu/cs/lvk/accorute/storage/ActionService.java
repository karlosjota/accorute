package su.msu.cs.lvk.accorute.storage;

import su.msu.cs.lvk.accorute.http.model.Action;
import su.msu.cs.lvk.accorute.http.model.EntityID;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.04.2010
 * Time: 16:19:06
 * To change this template use File | Settings | File Templates.
 */
//TODO: Is it needed at all??? 
public interface ActionService {
    /**
     * adds new or modifies existing action
     * @param act
     * @throws IllegalArgumentException
     */
    public void addOrUpdateAction(Action act);
    public Action getActionByID(EntityID actID);
    public Action getFirstActionInContext(EntityID ctxID);
    public Action getLastActionInContext(EntityID ctxID);

}
