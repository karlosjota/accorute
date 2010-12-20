package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.HttpAction;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 10.11.2010
 * Time: 1:51:18
 * To change this template use File | Settings | File Templates.
 */
public class ConfigBasedChStateDec implements ActionChangesStateDecision{
    public boolean changesState(HttpAction action) {
        Collection<HttpAction> stch = WebAppProperties.getInstance().getStateChangingHttpActions();
        for(HttpAction t: stch){
            if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(t,action)){
                return true;
            }
        }
        return false;
    }
}
