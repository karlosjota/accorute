package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.Response;
import su.msu.cs.lvk.accorute.utils.LevenshteinDistance;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 25.11.2010
 * Time: 11:26:39
 * To change this template use File | Settings | File Templates.
 */
public class LevensteinAGD implements AccessGrantedDecision{
    public boolean accessWasGranted(Conversation benignOne, Conversation checkedOne) {
        if(benignOne != null){
            Response check = checkedOne.getResponse();
            Response benign = benignOne.getResponse();
            if(WebAppProperties.getInstance().getRespClassificator().getResponseType(checkedOne)!=ResponseClassificator.ResponseType.OKAY){
                return false;
            }
            String s1 = check.getTotallyDecodedBody();
            String s2 = benign.getTotallyDecodedBody();

            int distance = LevenshteinDistance.getLevenshteinDistance(
                s1,
                s2
            );
            int avglen = (s1.length() + s2.length())/2;
            return ((double) avglen / (double)distance) > 0.5 ;
        }else{
            return WebAppProperties.getInstance().getRespClassificator().getResponseType(checkedOne) ==  ResponseClassificator.ResponseType.OKAY;
        }
    }
}
