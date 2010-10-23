package su.msu.cs.lvk.accorute.decisions;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.cookie.CookieOrigin;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.commons.httpclient.cookie.RFC2965Spec;
import org.apache.commons.lang.NotImplementedException;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.CookieDescriptor;
import su.msu.cs.lvk.accorute.http.model.Request;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 11.05.2010
 * Time: 13:58:48
 * To change this template use File | Settings | File Templates.
 */
public class SimpleRCD implements  RequestComposerDecomposer{
    public List<ActionParameter> decompose(Request r){
        throw new NotImplementedException();
    }
    private static ActionParameter getParamByName(List<ActionParameter> params, String name){
        for(ActionParameter param: params ){
            if(param.getName().equals(name))
                return param;
        }
        return null;
    }

    public Request compose(List<ActionParameter> params, WebAppUser user){
        Request req = new Request();

        WebAppProperties.getInstance().getPvd().resolve(params,user);
        String Query = "";
        String Body = "";
        String proto = "http";
        String host = getParamByName(params,"host").getValue();
        int port = new Integer(getParamByName(params,"port").getValue());
        String path = getParamByName(params,"path").getValue();
        List<Cookie> cookies = new ArrayList<Cookie>();
        for(ActionParameter param: params){
            try{
                if(param.getLocation() == ActionParameterLocation.QUERY){
                    Query +=(URLEncoder.encode(param.getName(),"UTF-8")
                            + "="
                            + URLEncoder.encode(param.getValue(),"UTF-8")
                            + "&"
                    );
                }else if(param.getLocation() == ActionParameterLocation.BODY){
                    Body +=(URLEncoder.encode(param.getName(),"UTF-8")
                            + "="
                            + URLEncoder.encode(param.getValue(),"UTF-8")
                            + "&"
                    );
                } else if(param.getLocation() == ActionParameterLocation.HEADER){
                    req.addHeader(param);
                } else if(param.getLocation() == ActionParameterLocation.COOKIE){
                    cookies.add (new Cookie(host,param.getName(), param.getValue()));//TODO: "host" here is a stub!
                }
            }catch(UnsupportedEncodingException ueex){}
        }
        try{
            req.setURL(new URL(
                    proto,
                    host,
                    port,
                    path + ((Query.equals("")) ? "":("?"+Query.substring(0,Query.length()-1)))
                )
            );
        }catch(MalformedURLException muex){
            //TODO: Smth here!!!
        } 
        req.setCookies(new CookieDescriptor(cookies,new CookieOrigin(host,port,"",false), "Cookie")); //TODO: CookieOrigin() params are a stub!
        if(!Body.equals("")){
            req.setContent(Body.substring(0,Body.length()-1).getBytes());
            req.setMethod("POST");
        }
        return req;
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
        ArrayList<ActionParameter> params = new ArrayList<ActionParameter>();
        String [] cooks = cookies.split("[,;]");
        for(String cook: cooks){
            String [] namevalue = cook.split("=");
            params.add(new ActionParameter(
                    namevalue[0].trim(),
                    namevalue[1].trim(),
                    ActionParameterLocation.COOKIE,
                    ActionParameterMeaning.AUTOMATIC,
                    ActionParameterDatatype.STRING)
            );
        }
        // TODO: $Version, $Path etc. are not supported yet!
        return params;
        
    }

}
