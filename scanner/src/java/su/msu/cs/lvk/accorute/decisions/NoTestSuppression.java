package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.HttpAction;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 4/26/11
 * Time: 6:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoTestSuppression implements SuppressDetectionDecision{
    public boolean shouldSuppressAction(HttpAction action, EntityID ctxid) {
        return false;
    }
}
