package su.msu.cs.lvk.accorute.http.model;

import su.msu.cs.lvk.accorute.RBAC.Role;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 29.11.2010
 * Time: 0:59:55
 * To change this template use File | Settings | File Templates.
 */
public class UseCase {
    private final Role userRole;
    private final HttpAction httpAct;
    public UseCase(Role role, HttpAction act){
        userRole = role;
        httpAct = act;
    }

    public HttpAction getHttpAct() {
        return httpAct;
    }

    public Role getUserRole() {
        return userRole;
    }

    @Override
    public String toString() {
        return "{" + httpAct.getName() +
                " by " + userRole.getRoleName() +
                '}';
    }
}
