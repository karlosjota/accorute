package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.HttpAction;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.12.2010
 * Time: 22:04:03
 * To change this template use File | Settings | File Templates.
 */
public class PostChangeStateDecision implements ActionChangesStateDecision{
    public boolean changesState(HttpAction action) {
        List<ActionParameter> params = action.getActionParameters();
        for(ActionParameter p : params){
            if(p.getLocation() == ActionParameterLocation.BODY)
                return true;
        }
        return false;
    }
}
