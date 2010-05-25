package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.NamedValue;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 25.05.2010
 * Time: 18:36:59
 * To change this template use File | Settings | File Templates.
 */
public class SimplePVD extends ParameterValueDecision{
    public void resolve(ActionParameter param, WebAppUser user){
        List<NamedValue> creds = user.getStaticCredentials();
        for(NamedValue cred: creds){
            if(cred.getName() == param.getName()){
                param.setValue(cred.getValue());
            }
        }
    }
}
