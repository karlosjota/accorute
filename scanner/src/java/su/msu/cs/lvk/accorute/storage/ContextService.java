package su.msu.cs.lvk.accorute.storage;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.UserContext;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.04.2010
 * Time: 16:18:32
 * To change this template use File | Settings | File Templates.
 */
public interface ContextService {
    /**
     * Adds new or modifies existing context.
     * @param ctx - context to be added or modified
     * @throws IllegalArgumentException iff context id is not valid;
     * ContextID should be either NOT_INITIALIZED or a value that is present in the collection.
     * Note, if contextID was NOT_INITIALIZED, then after call to addOrModifyContext
     * the contextID will receive a unique value;  
     */
    public void addOrUpdateContext(UserContext ctx);

    /**
     *
     * @param ctxID
     * @return UserContext that has this ID
     * @throws IllegalArgumentException iff ctxID is NOT_INITIALIZED or is not present in the collection.
     */
    public UserContext getContextByID(EntityID ctxID);
}
