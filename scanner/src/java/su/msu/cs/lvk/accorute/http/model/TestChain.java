package su.msu.cs.lvk.accorute.http.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 20.04.2010
 * Time: 17:32:12
 * To change this template use File | Settings | File Templates.
 */
public class TestChain {
    public static class ActionByUser {
        public final Action action;
        public final WebAppUser user;
        public ActionByUser(Action act, WebAppUser u){
            action = act;
            user = u;
        }
    }
    private List<ActionByUser> acts = new ArrayList<ActionByUser>();

    public void add(Action a,  WebAppUser u){
        acts.add(new ActionByUser(a,u));
    }

    public ActionByUser get(int i){
        return acts.get(i);
    }

    public int size(){
        return acts.size();
    }

    public void clear(){
        acts.clear();
    }
}
