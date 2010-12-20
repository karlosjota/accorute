package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.Conversation;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.11.2010
 * Time: 1:26:44
 * To change this template use File | Settings | File Templates.
 */

public class PatternResponceClassifier implements  ResponseClassificator{
    private final Pattern notFound;
    private final Pattern prohibited;
    private final Pattern expired;
    private final Pattern error;
    public PatternResponceClassifier(
            Pattern NOT_FOUNDRegexp,
            Pattern PROHIBITEDRegexp,
            Pattern EXPIREDRegexp,
            Pattern ERRORegexp)
    {
        notFound = NOT_FOUNDRegexp;
        prohibited = PROHIBITEDRegexp;
        expired = EXPIREDRegexp;
        error = ERRORegexp;
    }
    public ResponseType getResponseType(Conversation conv) {
        String conversation = conv.getResponse().getTotallyDecodedBody();
        if(notFound.matcher(conversation).matches()){
            return ResponseType.NOT_FOUND;
        }
        if(prohibited.matcher(conversation).matches()){
            return ResponseType.PROHIBITED;
        }
        if(expired.matcher(conversation).matches()){
            return ResponseType.EXPIRED;
        }
        if(error.matcher(conversation).matches()){
            return ResponseType.ERROR;
        }
        return ResponseType.OKAY;
    }
}
