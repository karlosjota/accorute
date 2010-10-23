package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.Conversation;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.10.2010
 * Time: 17:14:36
 * To change this template use File | Settings | File Templates.
 */
public interface ResponseClassificator {
    public static enum ResponseType{
        NOT_FOUND,
        PROHIBITED,
        ERROR,
        EXPIRED,
        OKAY
    }
    ResponseType getResponseType(Conversation conv); 
}
