package su.msu.cs.lvk.accorute.RBAC;

import com.truchsess.util.HashMapTree;
import com.truchsess.util.MutableMapTreeCursor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of the {@link Role} interface.
 */
public class SimpleRBACRole implements Role {
    static HashMapTree<String, Role> _roleHierarchy = new HashMapTree<String, Role>();
    private MutableMapTreeCursor<String, Role> _cursor;
    private String _rolename;

    /**
     * The static method should be used to create the initial role hierarchy by initialization of the root role.
     *
     * @param rootRoleName the name of the root role.
     * @return the object representing the root role, which can be used in further hierarchy construction.
     */
    public static Role createRootRole(String rootRoleName) {
        MutableMapTreeCursor<String, Role> cursor = _roleHierarchy.getCursor();
        SimpleRBACRole rootRole = new SimpleRBACRole(rootRoleName);
        if (cursor.hasChildren()) {
            cursor.down();
            return cursor.getElement();
        }

        cursor.putChild(rootRoleName, rootRole);
        cursor.down(rootRoleName);
        rootRole._cursor = cursor;
        return rootRole;
    }

    private SimpleRBACRole(String roleName) {
        _rolename = roleName;
    }

    /**
     * Should be used to get the root role from the role hierarchy.
     *
     * @return the root role of the current role hierarchy.
     */
    public Role getRootRole() {
        MutableMapTreeCursor<String, Role> cursor = _roleHierarchy.getCursor();
        cursor.down();
        return cursor.getElement();
    }

    /**
     * Should be used to detect whether the role is the root. Having in mind the partial order definition it is possible
     * to imagine situation where there is no single role preceeding the rest.
     * However, in web applicationsvdomain we mark out a special 'public' role, which will always be the root one.
     *
     * @return true iff the role is the root (or public) role.
     */
    public boolean isRootRole() {
        MutableMapTreeCursor<String, Role> cursor = _cursor.clone();
        if (!_cursor.hasParent()) return true;
        cursor.up();
        return !cursor.hasParent();
    }

    /**
     * Should be used to get the name of this role.
     *
     * @return name of this role as a string.
     */
    public String getRoleName() {
        return _rolename;
    }

    /**
     * Should be used to get the parent role of this one.
     * The role is called a parent if it preceeds this one according to the partial order relation. In other words, the current
     * role extends the parent one.
     *
     * @return the parent role of this role.
     */
    public Role getParentRole() {
        MutableMapTreeCursor<String, Role> cursor = _cursor.clone();

        if (!_cursor.hasParent()) return null;
        cursor.up();
        return cursor.getElement();
    }

    /**
     * Should be used to get the list of all child roles of this one.
     *
     * @return the list of child roles.
     */
    public List<Role> getChildRoles() {
        MutableMapTreeCursor<String, Role> cursor = _cursor.clone();
        Set<String> keys = cursor.getChildKeys();
        Iterator<String> it = keys.iterator();
        ArrayList<Role> result = new ArrayList<Role>();
        if (!cursor.hasChildren()) return result;

        while (it.hasNext()) {
            cursor = _cursor.clone();
            cursor.down(it.next());
            result.add(cursor.getElement());
        }

        return result;
    }

    /**
     * Should be used to add a role to the current role hierarchy as a child of this one.
     *
     * @param childRole the name of the new child role.
     * @return the newly created child role linked in the current hierarchy.
     */
    public Role addChildRole(String childRole) {
        MutableMapTreeCursor<String, Role> cursor = _cursor.clone();
        SimpleRBACRole newRole = new SimpleRBACRole(childRole);
        _cursor.putChild(childRole, newRole);
        cursor.down(childRole);
        newRole._cursor = cursor;
        return newRole;
    }

    /**
     * Should be used to delete a child role of the this one from the current role hierarchy.
     * Nothing happens if there is no role with such name.
     *
     * @param rolename the name of the child role, which is subject for removal.
     */
    public void deleteChildRole(String rolename) {
        MutableMapTreeCursor<String, Role> cursor = _cursor.clone();
        cursor.down(rolename);
        cursor.remove();
    }

    /**
     * Should be used to get the child role by name.
     *
     * @param rolename the name of the requested child role.
     * @return the object representing the requested role. Null if there is no role with the specified name.
     */
    public Role getChildRole(String rolename) {
        MutableMapTreeCursor<String, Role> cursor = _cursor.clone();
        if (!cursor.hasChildren()) return null;
        cursor.down(rolename);
        return cursor.getElement();
    }

    /**
     * The rule for the roles equality relation: role names are equal (case insensitive).
     *
     * @param obj another role entity to compare the current one with.
     * @return true if the roles are equal, false - otherwise.
     */
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Role && _rolename.equalsIgnoreCase(((Role) obj).getRoleName());
    }

    /**
     * Used to get a string representation of a role with indication of its parent. Mainly should be used for debug purposes.
     *
     * @return a string representing this role.
     */
    public String toString() {
        return (isRootRole() ? _rolename : _rolename + " extends " + getParentRole().getRoleName());
    }
}


