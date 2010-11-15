package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.HttpAction;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 10.11.2010
 * Time: 1:51:18
 * To change this template use File | Settings | File Templates.
 */
public class DummyChStateDec implements ActionChangesStateDecision{
    public boolean changesState(HttpAction action) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
