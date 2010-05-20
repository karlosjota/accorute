package su.msu.cs.lvk.accorute.http.model;

import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.04.2010
 * Time: 14:10:01
 * To change this template use File | Settings | File Templates.
 */
public class WebAppUser implements User {    
    private EntityID userID =  EntityID.NOT_INITIALIZED;
    private Role role;

    /**
     * all static credentials - login, password, pin number.
     * Also, all static form data as Name/Surname/Age/Sex,
     * that is not directly associated with security...
     */
    private final List<NamedValue> dynamicCredentials = new ArrayList<NamedValue>();
    private final List<NamedValue> staticCredentials = new ArrayList<NamedValue>();

    public List<NamedValue> getDynamicCredentials() {
        return dynamicCredentials;
    }
    public List<NamedValue> getStaticCredentials() {
        return staticCredentials;
    }


    @Override
    public EntityID getUserID() {
        return userID;  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
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
}
