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
import java.util.*;

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
        Map<String,ContextCookie> cookieMap = new HashMap<String,ContextCookie>();
        if(coll!=null){
            for(ContextCookie c : coll){
                cookieMap.put(c.getName(),c);
            }
        }
        for(int i = 0; i< params.size(); i++){
            ActionParameter param = params.get(i);
            if(param.getLocation() == ActionParameterLocation.COOKIE){
                if(cookieMap.containsKey(param.getName())){
                    param.setAdditionalData(cookieMap.get(param.getName()));
                    cookieMap.remove(param.getName());
                }
            }
        }
        for(String additionalCookie:cookieMap.keySet()){
            ActionParameter cook = new ActionParameter(
                    additionalCookie,
                    "",
                    ActionParameterLocation.COOKIE,
                    ActionParameterMeaning.SESSIONTOKEN,//TODO: add a decision for this
                    ActionParameterDatatype.STRING
            );
            cook.setAdditionalData(cookieMap.get(additionalCookie));
            params.add(cook);
        }
        ListIterator<ActionParameter> iterator = params.listIterator();
        while(iterator.hasNext()){
            ActionParameter param = iterator.next();
            if(user.getDynamicCredentials().containsKey(param.getName())){ //TODO: name clashes in param names!
                ActionParameter newParam = new ActionParameter(
                        param.getName(),
                        user.getDynamicCredentials().get(param.getName()),
                        param.getLocation(),
                        param.getMeaning(),
                        param.getDatatype(),
                        param.getRole()

                );
                newParam.setAdditionalData(param.getAdditionalData());
                iterator.set(newParam);
            }
        }
    }
}
