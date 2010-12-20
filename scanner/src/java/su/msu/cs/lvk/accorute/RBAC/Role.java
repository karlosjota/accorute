package su.msu.cs.lvk.accorute.RBAC;

import java.util.List;

/**
 * The interface represents a role in the RBAC model. <br>
 * According to the RBAC model the roles are organized into role hierarchy using the partial order relation.
 * Thus, the interface contains methods to traverse and modify the role hierarchy.
 * We assume that the role hierarchy is a connected graph.
 */
public interface Role {
    /**
     * Should be used to detect whether the role is the root. Having in mind the partial order definition it is possible
     * to imagine situation where there is no single role preceeding the rest.
     * However, in web applications domain we mark out a special 'public' role, which will always be the root one.
     *
     * @return true iff the role is the root (or public) role.
     */
    public boolean isRootRole();

    /**
     * Should be used to get the root role from the role hierarchy.
     *
     * @return the root role of the current role hierarchy.
     */
    public Role getRootRole();

    /**
     * Should be used to get the name of this role.
     *
     * @return name of this role as a string.
     */
    public String getRoleName();

    /**
     * Should be used to get the parent role of this one.
     * The role is called a parent if it preceeds this one according to the partial order relation. In other words, the current
     * role extends the parent one.
     *
     * @return the parent role of this role.
     */
    public Role getParentRole();

    /**
     * Should be used to get the list of all child roles of this one.
     *
     * @return the list of child roles.
     */
    public List<Role> getChildRoles();

    /**
     * Should be used to add a role to the current role hierarchy as a child of this one.
     *
     * @param childRole the name of the new child role.
     * @return the newly created child role linked in the current hierarchy.
     */
    public Role addChildRole(String childRole);

    /**
     * Should be used to delete a child role of the this one from the current role hierarchy.
     * Nothing happens if there is no role with such name.
     *
     * @param rolename the name of the child role, which is subject for removal.
     */
    public void deleteChildRole(String rolename);

    /**
     * Should be used to get the child role by name.
     *
     * @param rolename the name of the requested child role.
     * @return the object representing the requested role. Null if there is no role with the specified name.
     */
    public Role getChildRole(String rolename);

    public boolean equals(Object o);
}
