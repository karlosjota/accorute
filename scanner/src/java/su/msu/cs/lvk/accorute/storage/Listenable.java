package su.msu.cs.lvk.accorute.storage;

import su.msu.cs.lvk.accorute.utils.Callback0;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 06.05.12
 * Time: 2:49
 * To change this template use File | Settings | File Templates.
 */
public interface Listenable {
    public void addCallback(Callback0 cb);
    public void removeCallback(Callback0 cb);
}
