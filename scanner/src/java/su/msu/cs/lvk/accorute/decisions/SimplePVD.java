package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.ContextCookie;
import su.msu.cs.lvk.accorute.http.model.UserContext;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 25.05.2010
 * Time: 18:36:59
 * To change this template use File | Settings | File Templates.
 */
public class SimplePVD implements ParameterValueDecision{
    public void resolve(List<ActionParameter> params, UserContext ctx, URL url){
        WebAppUser user = WebAppProperties.getInstance().getUserService().getUserByID(ctx.getUserID());
        Collection<ContextCookie> coll = WebAppProperties.getInstance().getCookieService().getCookiesForUrlInContext(ctx.getContextID(),url);
        Map<String,String> cookieMap = new HashMap<String,String>();
        if(coll!=null){
            for(ContextCookie c : coll){
                cookieMap.put(c.getName(),c.getValue());
            }
        }
        for(int i = 0; i< params.size(); i++){
            ActionParameter param = params.get(i);
            if(param.getLocation() == ActionParameterLocation.COOKIE){
                cookieMap.remove(param.getName());
            }
        }
        for(String additionalCookie:cookieMap.keySet()){
            params.add(new ActionParameter(
                    additionalCookie,
                    "",
                    ActionParameterLocation.COOKIE,
                    ActionParameterMeaning.SESSIONTOKEN,//TODO: add a decision for this
                    ActionParameterDatatype.STRING
            ));
        }
        for(ActionParameter param:params){
            if(user.getDynamicCredentials().containsKey(param.getName())){ //TODO: name clashes in param names!
                param.setValue(user.getDynamicCredentials().get(param.getName()));
            }else{
                Map<String, String> creds = user.getStaticCredentials();
                if(creds.containsKey(param.getName()))
                    param.setValue(creds.get(param.getName()));
            }
        }
    }
}
