package su.msu.cs.lvk.accorute.utils;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 10.11.2010
 * Time: 23:33:26
 * To change this template use File | Settings | File Templates.
 */
public interface Callback3<T1,T2,T3> {
    public void CallMeBack(T1 param1, T2 param2, T3 param3);
}