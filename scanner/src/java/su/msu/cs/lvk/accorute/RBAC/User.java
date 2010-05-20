package su.msu.cs.lvk.accorute.RBAC;

import su.msu.cs.lvk.accorute.http.model.EntityID;

/**
 * The interface represents a generic user in a RBAC model. <br>
 * A user entity has two attributes: name and role.
 */
public interface User {
    /**
     * Should be used to detect the name of this user.
     *
     * @return the name of this user as a string.
     */
    public EntityID getUserID();

    /**
     * Should be used to detect the role of this user.
     *
     * @return the link to a role of the current user.
     */
    public Role getUserRole();
}
