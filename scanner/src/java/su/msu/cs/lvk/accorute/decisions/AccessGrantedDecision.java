package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.Conversation;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 25.11.2010
 * Time: 2:15:21
 * To change this template use File | Settings | File Templates.
 */
public interface AccessGrantedDecision {
    public boolean accessWasGranted(Conversation benignOne, Conversation checkedOne);
}
