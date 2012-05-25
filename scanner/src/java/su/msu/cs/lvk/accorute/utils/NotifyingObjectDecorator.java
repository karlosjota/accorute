package su.msu.cs.lvk.accorute.utils;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 04.05.12
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */
public class NotifyingObjectDecorator<T> extends CallbackContainer{
    private T object;

    public NotifyingObjectDecorator(T object) {
        this.object = object;
    }
    public T getObject(){
        return object;
    }
    public void setObject(T newObj){
        object = newObj;
        notifyCallbacks();
    }
}
