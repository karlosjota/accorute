package su.msu.cs.lvk.accorute.storage;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.04.2010
 * Time: 16:18:48
 * To change this template use File | Settings | File Templates.
 */

//TODO: Is it needed at all??? 
public interface UserService {
    /**
     * Adds new user or modifies existing one
     * @param user
     * @throws IllegalArgumentException iff there are troubles with user.userID:
     * user.userID != NOT_INITIALIZED and user.userID is not present.
     * Note: the decision on what user is actually updated is based on userID param,
     * setting this to userID of another user will have disastrous consequences; DON'T DO THAT!
     */
    public void addOrModifyUser(WebAppUser user);

    /**
     * Gets user by userid.
     * @throws IllegalArgumentException iff userID is invalid: NOT_INITIALIZED or not present.
     * @param userID
     */
    public WebAppUser getUserByID(EntityID userID);
    /**
     * Gets user by credential;
     * Example: getUsersByCredential("username", "john") 
     * @param name - name
     * @param Value - value
     * @throws IllegalArgumentException if there are multiple users with the same name
     */
    public List<WebAppUser> getUsersByCredential(String name, String Value);



}

