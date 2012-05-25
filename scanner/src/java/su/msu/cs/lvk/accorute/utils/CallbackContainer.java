package su.msu.cs.lvk.accorute.utils;

import su.msu.cs.lvk.accorute.storage.Listenable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 04.05.12
 * Time: 1:57
 * To change this template use File | Settings | File Templates.
 */
public class CallbackContainer implements Listenable {
    private final Collection<Callback0> callbacks = new HashSet<Callback0> ();
    public void addCallback(Callback0 cb){
        callbacks.add(cb);
    }
    public void removeCallback(Callback0 cb){
        callbacks.remove(cb);
    }
    protected void clearAll(){
        callbacks.clear();
    }
    protected void notifyCallbacks(){
        for(Callback0 cb: callbacks){
            cb.CallMeBack();
        }
    }
}
