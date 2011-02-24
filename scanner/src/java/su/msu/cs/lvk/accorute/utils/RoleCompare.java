package su.msu.cs.lvk.accorute.utils;

import su.msu.cs.lvk.accorute.RBAC.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 14.12.2010
 * Time: 1:45:03
 * To change this template use File | Settings | File Templates.
 */
public class RoleCompare {
    public static int less(Role one, Role other){
        List<Role> routeOne = new ArrayList<Role>();
        List<Role> routeOther = new ArrayList<Role>();
        Role cur = one;
        int c1 = 0;
        while(cur != null){
            routeOne.add(c1, cur);
            c1++;
            cur = cur.getParentRole();
        }
        cur = other;
        int c2=0;
        while(cur != null){
            routeOther.add(c2, cur);
            c2++;
            cur = cur.getParentRole();
        }
        int minSize = (c1 > c2) ? c2 : c1;
        int i;
        java.util.Collections.reverse(routeOne);
        java.util.Collections.reverse(routeOther);
        for(i=0; i<minSize; i++){
            if(!routeOne.get(i).equals(routeOther.get(i)))
                break;
        }
        if(i<minSize - 1){
            return 0; // Not comparable
        }
        return c1 - c2; // >0 if one is more privileged than other, <0 otherwise
    }
}
