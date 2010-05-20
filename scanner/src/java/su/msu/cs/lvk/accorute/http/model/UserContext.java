package su.msu.cs.lvk.accorute.http.model;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.04.2010
 * Time: 19:43:38
 * To change this template use File | Settings | File Templates.
 */
public class UserContext {
    private EntityID userID = EntityID.NOT_INITIALIZED;
    private EntityID contextID = EntityID.NOT_INITIALIZED;

    public EntityID getUserID() {
        return userID;
    }

    public void setUserID(EntityID userID) {
        this.userID = userID;
    }
    
    public EntityID getContextID() {
        return contextID;
    }

    public void setContextID(EntityID contextID) {
        this.contextID = contextID;
    }

}
