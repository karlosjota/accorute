package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.Request;
import su.msu.cs.lvk.accorute.http.model.Response;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.10.2010
 * Time: 13:58:36
 * To change this template use File | Settings | File Templates.
 */
public interface MessageEqualityDecision {
    public boolean RequestEquals(Request a, Request b);
    public boolean ResponseEquals(Response a, Response b);
}
