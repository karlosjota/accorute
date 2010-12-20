package su.msu.cs.lvk.accorute.storage;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.HttpAction;

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
    public void addOrUpdateAction(HttpAction act);
    public HttpAction getActionByID(EntityID actID);
    public HttpAction getFirstActionInContext(EntityID ctxID);
    public HttpAction getLastActionInContext(EntityID ctxID);

}
