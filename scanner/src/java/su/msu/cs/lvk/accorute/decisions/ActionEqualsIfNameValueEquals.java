package su.msu.cs.lvk.accorute.decisions;

import org.apache.log4j.Logger;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.HttpAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
public class ActionEqualsIfNameValueEquals implements ActionEqualityDecision{
    private Logger logger = Logger.getLogger(ActionEqualsIfNameValueEquals.class.getName());
    private static ActionParameter getParamByName(List<ActionParameter> params, String name){
        for(ActionParameter param: params ){
            if(param.getName().equals(name))
                return param;
        }
        return null;
    }
    public boolean ActionEquals(HttpAction a, HttpAction b) {
        //TODO: indeed very simple and incomplete
        List<ActionParameter> aParams = a.getActionParameters();
        List<ActionParameter> bParams = b.getActionParameters();
        Set<String> names = new HashSet<String>();
        Set<String> noValueCheck = new HashSet<String>();
        for(ActionParameter aParam:aParams){
            names.add(aParam.getName());
        }
        for(ActionParameter bParam:bParams){
            names.add(bParam.getName());
        }
        for(ActionParameter aParam:aParams){
            if(aParam.getMeaning() == ActionParameterMeaning.SESSIONTOKEN
                    || aParam.getMeaning() == ActionParameterMeaning.ONETIMETOKEN)
            {
                names.remove(aParam.getName());
            }else if(aParam.getMeaning() == ActionParameterMeaning.USERCONTROLLABLE){
                noValueCheck.add(aParam.getName());
            }
        }
        for(ActionParameter bParam:bParams){
            if(bParam.getMeaning() == ActionParameterMeaning.SESSIONTOKEN
                    || bParam.getMeaning() == ActionParameterMeaning.ONETIMETOKEN)
            {
                names.remove(bParam.getName());
            }else if(bParam.getMeaning() == ActionParameterMeaning.USERCONTROLLABLE){
                noValueCheck.add(bParam.getName());
            }
        }
        for(String name : names){
            ActionParameter aParam = getParamByName(aParams,name);
            ActionParameter bParam =  getParamByName(bParams,name);
            if(bParam==null || aParam==null)
                return false;
            if(!noValueCheck.contains(name)){
                if(aParam.getLocation() != bParam.getLocation()){
                    logger.trace( a + " != " +b +" because of location of " + name);
                    return false;
                }
                String aVal = aParam.getValue();
                String bVal = bParam.getValue();
                if(name=="path"){
                    if (aParam.getLocation() == ActionParameterLocation.URL){
                        if(!aVal.endsWith("/"))
                            aVal += "/";
                        if(!bVal.endsWith("/"))
                            bVal += "/";
                    }
                }
                if(!aVal.equals(bVal)){
                    logger.trace( a + " != " +b +" because of " + name);
                    return false;
                }
            }
        }
        return true;
    }
}
