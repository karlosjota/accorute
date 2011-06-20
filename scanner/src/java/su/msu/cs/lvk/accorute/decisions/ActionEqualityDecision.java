package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.HttpAction;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.11.2010
 * Time: 13:39:28
 * To change this template use File | Settings | File Templates.
 */
public interface ActionEqualityDecision {
    public boolean ActionEqualsIgnoreIdentifiers(HttpAction a, HttpAction b);
    public boolean ActionEquals(HttpAction a, HttpAction b);
}
