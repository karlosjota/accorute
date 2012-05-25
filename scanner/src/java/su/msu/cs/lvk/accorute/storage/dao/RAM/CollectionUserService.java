package su.msu.cs.lvk.accorute.storage.dao.RAM;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;
import su.msu.cs.lvk.accorute.storage.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 20.04.2010
 * Time: 16:57:36
 * To change this template use File | Settings | File Templates.
 */
public class CollectionUserService implements UserService {
    private final Map<EntityID, WebAppUser> database = new HashMap<EntityID, WebAppUser>();
    private Long nextId = 1l;

    public void clear() {
        database.clear();
        nextId = 1l;
    }

    public void addOrModifyUser(WebAppUser user){
        if(user.getUserID() == EntityID.NOT_INITIALIZED ){
            EntityID id = new EntityID(nextId++);
            user.setUserID(id);
            database.put(id,user);
            return;
        }
        if(! database.containsKey(user.getUserID()) ){
            throw new IllegalArgumentException("No user with such id in the database");
        }
        database.remove(user.getUserID());
        database.put(user.getUserID(), user);
    }
    public WebAppUser getUserByID(EntityID userID){
        if(! database.containsKey(userID)){
            return null;
        }
        return database.get(userID);
    }

    public List<WebAppUser> getUsersByCredential(String name, String value){
        ArrayList<WebAppUser> matches = new ArrayList<WebAppUser>();
        for(WebAppUser u: database.values()){
            if( u.getStaticCredentials().containsKey(name) && u.getStaticCredentials().get(name).equals(value)){
                matches.add(u);
            }
        }
        return matches;
    }
    public List<WebAppUser> getUsersByRole(String roleName){
        ArrayList<WebAppUser> matches = new ArrayList<WebAppUser>();
        for(WebAppUser u: database.values()){
            if(u.getUserRole().getRoleName().equals(roleName))
                matches.add(u);
        }
        return matches;
    }
}
