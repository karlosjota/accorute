package su.msu.cs.lvk.accorute.http.model;

import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.04.2010
 * Time: 14:10:01
 * To change this template use File | Settings | File Templates.
 */
public class WebAppUser implements User, Serializable {
    private EntityID userID =  EntityID.NOT_INITIALIZED;
    private Role role;

    /**
     * all static credentials - login, password, pin number.
     * Also, all static form data as Name/Surname/Age/Sex,
     * that is not directly associated with security...
     */
    private final Map<String, String> staticCredentials = new HashMap<String, String>();
    private final Map<String, String> dynamicCredentials = new HashMap<String, String>();

    public Map<String, String> getDynamicCredentials() {
        return dynamicCredentials;
    }
    public Map<String, String> getStaticCredentials() {
        return staticCredentials;
    }


    public EntityID getUserID() {
        return userID;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Role getUserRole() {
        return role;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Setting this to userID of another user will have disastrous consequences; DON'T DO THAT!
     * Don't use this method unless you know what you're doing!
     * @param userID
     */
    public void setUserID(EntityID userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return staticCredentials.toString();
    }
}
