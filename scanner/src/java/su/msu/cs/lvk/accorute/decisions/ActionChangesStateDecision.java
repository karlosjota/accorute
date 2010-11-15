package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.HttpAction;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.10.2010
 * Time: 16:57:34
 * To change this template use File | Settings | File Templates.
 */
public interface ActionChangesStateDecision {
    public boolean changesState(HttpAction action);
}
