package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.Action;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.11.2010
 * Time: 13:46:15
 * To change this template use File | Settings | File Templates.
 */

/**
 * @br simple URL-based equality check
 */
public class SimpleAcEqDec implements ActionEqualityDecision{
    private static ActionParameter getParamByName(List<ActionParameter> params, String name){
        for(ActionParameter param: params ){
            if(param.getName().equals(name))
                return param;
        }
        return null;
    }
    public boolean ActionEquals(Action a, Action b) {
        //ignore action name...
        List aParams = a.getActionParameters();
        List bParams = b.getActionParameters();
        if(! getParamByName(aParams,"host").getValue().equalsIgnoreCase(getParamByName(bParams,"host").getValue()))
            return false;
        if(! getParamByName(aParams,"port").getValue().equalsIgnoreCase(getParamByName(bParams,"port").getValue()))
            return false;
        if(! getParamByName(aParams,"path").getValue().equalsIgnoreCase(getParamByName(bParams,"path").getValue()))
            return false;
        return true;
    }
}
