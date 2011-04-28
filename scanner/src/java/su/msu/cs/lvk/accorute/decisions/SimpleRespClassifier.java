package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.Conversation;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 10.11.2010
 * Time: 2:13:07
 * To change this template use File | Settings | File Templates.
 */
public class SimpleRespClassifier implements ResponseClassificator{
    public ResponseType getResponseType(Conversation conv) {
        String code = conv.getResponse().getStatusCode();
        if(code.equals("404"))
            return ResponseType.NOT_FOUND;
        if(code.startsWith("2") ||  code.startsWith("3"))
            return ResponseType.OKAY;
        if(code.startsWith("4"))
            return ResponseType.PROHIBITED;
        return ResponseType.ERROR;
    }
}
