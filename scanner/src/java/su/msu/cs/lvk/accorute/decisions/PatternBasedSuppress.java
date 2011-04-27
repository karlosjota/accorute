package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.HttpAction;
import su.msu.cs.lvk.accorute.http.model.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 4/26/11
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternBasedSuppress implements SuppressDetectionDecision{
    List<Pattern> suppressions = new ArrayList<Pattern>();

    public PatternBasedSuppress(List<Pattern> suppressions) {
        this.suppressions = suppressions;
    }

    public boolean shouldSuppressAction(HttpAction action, EntityID ctxid) {
        Request r = WebAppProperties.getInstance().getRcd().compose(
                action.getActionParameters(),
                WebAppProperties.getInstance().getContextService().getContextByID(ctxid)
        );
        for(Pattern p : suppressions){
            if(p.matcher(r.toString()).matches()){
                return true;
            }
        }
        return false;
    }
}
