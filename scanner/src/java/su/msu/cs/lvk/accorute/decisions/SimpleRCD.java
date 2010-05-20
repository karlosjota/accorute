package su.msu.cs.lvk.accorute.decisions;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.commons.httpclient.cookie.RFC2965Spec;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.Request;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 11.05.2010
 * Time: 13:58:48
 * To change this template use File | Settings | File Templates.
 */
public class SimpleRCD extends  RequestComposerDecomposer{
    public List<ActionParameter> decompose(Request r){
        throw new NotImplementedException();
    }
    public Request compose(List<ActionParameter> params, WebAppUser user){
        throw new NotImplementedException();        
    }
    public List<ActionParameter> decomposeURL(String u) throws MalformedURLException {
        URL url = new URL(u);
        ArrayList<ActionParameter> params = new ArrayList<ActionParameter>();

        params.add(new ActionParameter(
                "host",
                url.getHost(),
                ActionParameterLocation.URL,
                ActionParameterMeaning.AUTOMATIC,
                ActionParameterDatatype.STRING)
        );
        params.add(new ActionParameter(
                "port",
                new Integer(url.getPort()).toString(),
                ActionParameterLocation.URL,
                ActionParameterMeaning.AUTOMATIC,
                ActionParameterDatatype.NUMBER)
        );
        params.add(new ActionParameter(
                "path",
                url.getPath(),
                ActionParameterLocation.URL,
                ActionParameterMeaning.AUTOMATIC,
                ActionParameterDatatype.STRING)
        );
        String query = url.getQuery();
        if(query != null){
            String [] ar = query.split("&");
            for(String pair : ar){
                String [] nameValue = pair.split("=",2);
                params.add(new ActionParameter(
                    nameValue[0],
                    nameValue[1],
                    ActionParameterLocation.QUERY,
                    ActionParameterMeaning.AUTOMATIC,
                    ActionParameterDatatype.STRING)
                );
            }
        }
        return params;
    }

    public List<ActionParameter> decomposeCookies(String cookies) throws MalformedCookieException {
        Cookie cookie = new Cookie();
        ArrayList<ActionParameter> params = new ArrayList<ActionParameter>();
        // TODO: implement
        return params;
        
    }

}
