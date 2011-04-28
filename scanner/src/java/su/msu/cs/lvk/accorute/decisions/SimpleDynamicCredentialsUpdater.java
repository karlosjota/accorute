package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.log4j.Logger;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterRole;
import su.msu.cs.lvk.accorute.http.model.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.12.2010
 * Time: 22:43:03
 * To change this template use File | Settings | File Templates.
 */

public class SimpleDynamicCredentialsUpdater implements DynamicCredentialsUpdater {
    private static Logger logger = Logger.getLogger(SimpleDynamicCredentialsUpdater.class.getName());
    public void updateCredentials(EntityID userID, List<ActionParameter> params){
        WebAppUser u = WebAppProperties.getInstance().getUserService().getUserByID(userID);
        for(ActionParameter p : params){
            String name = p.getName();
            if(WebAppProperties.getInstance().getDynTokenLoc(name) == p.getLocation()){
                u.getDynamicCredentials().put(name, p.getValue());
                logger.trace(userID + "Updated cred: " + name + ": " + p.getValue());
            }
        }
        WebAppProperties.getInstance().getUserService().addOrModifyUser(u);
    }
    public void updateCredentials(EntityID userID, HtmlPage p){
        logger.trace(userID+"USING THE RESPONSE TO THE FOLLOWING REQUEST \n" + p.getWebResponse().getWebRequest() + "\nWITH RESPONSE:"+p.getWebResponse().getContentAsString());
        WebClient client = p.getWebClient();
        Set<com.gargoylesoftware.htmlunit.util.Cookie> cooks = client.getCookieManager().getCookies();
        URL u = p.getUrl();
        CookieOrigin origin = new CookieOrigin(
                u.getHost(),
                (u.getPort()==-1)?u.getDefaultPort():u.getPort(),
                u.getPath(),
                false
        ); // TODO: false here
        List<Cookie> cookies = new ArrayList<Cookie>();
        for(com.gargoylesoftware.htmlunit.util.Cookie cook: cooks){
            cookies.add(new BasicClientCookie(cook.getName(), cook.getValue()));
        }
        EntityID ctxID = WebAppProperties.getInstance().getContextService().getContextsByUserID(userID).get(0).getContextID();
        CookieDescriptor desc = new CookieDescriptor(cookies,origin,ctxID);
        WebAppProperties.getInstance().getCookieService().setCookies(desc);
        //Update dynamic credentials
        EntityID id = WebAppProperties.getInstance().getContextService().getContextByID(ctxID).getUserID();
        WebAppUser user = WebAppProperties.getInstance().getUserService().getUserByID(id);
        for(Cookie c: desc.getCookies()){
            user.getDynamicCredentials().put(c.getName(),c.getValue());
            logger.trace(userID+"Updated cred: " + c.getName() + ":" + c.getValue());
        }
        WebAppProperties.getInstance().getUserService().addOrModifyUser(user);

        List<ActionParameter> params = new ArrayList<ActionParameter>();
        for(HtmlForm f: p.getForms()){
            for(HtmlElement i: f.getHtmlElementsByTagName("input")){
                HtmlInput input = (HtmlInput) i;
                String name = input.getNameAttribute();
                String val = input.getValueAttribute();
                ActionParameterLocation loc = f.getMethodAttribute().equalsIgnoreCase("POST") ?
                        ActionParameterLocation.BODY : ActionParameterLocation.QUERY;
                params.add(new ActionParameter(
                        name,
                        val,
                        loc,
                        ActionParameterMeaning.UNKNOWN,
                        ActionParameterDatatype.STRING,
                        ActionParameterRole.UNKNOWN
                ));
            }
        }
        //TODO: also parse links!
        //TODO: parse dynamic links and forms!
        if(params.size() != 0)
            updateCredentials(userID,params);
    }
    public void updateCredentials(EntityID userID,Conversation conv){
        WebAppUser u = WebAppProperties.getInstance().getUserService().getUserByID(userID);
        Response resp = conv.getResponse();
        try{
            CookieDescriptor desc = resp.getCookieDescriptor();
            WebAppProperties.getInstance().getCookieService().setCookies(desc);
            //Update dynamic credentials
            for(Cookie c: desc.getCookies()){
                u.getDynamicCredentials().put(c.getName(),c.getValue());

                logger.trace(userID + "Updated cred: " + c.getName() + ": " + c.getValue());
            }
            WebAppProperties.getInstance().getCookieService().setCookies(desc);
        }catch (MalformedCookieException ex){
            logger.warn("Malforming cookie, will skip");
        }
        WebAppProperties.getInstance().getUserService().addOrModifyUser(u);
    }
}
