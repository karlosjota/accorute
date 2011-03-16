package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.HttpAction;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 3/16/11
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class NeverChangeState implements ActionChangesStateDecision{
    public boolean changesState(HttpAction action) {
        return false;
    }
}
