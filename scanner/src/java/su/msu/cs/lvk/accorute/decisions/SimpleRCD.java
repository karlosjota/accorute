package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.commons.lang.NotImplementedException;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.impl.cookie.BasicClientCookie;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.model.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
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
public class SimpleRCD extends RequestComposerDecomposer{
    public URL getURL(List<ActionParameter> params){
        String Query = "";
        String proto = "http";
        String host = getParamByName(params,"host").getValue();
        int port = new Integer(getParamByName(params,"port").getValue());
        if(port == -1){
            port = 80;
        }
        String path = getParamByName(params,"path").getValue();
        for(ActionParameter param: params){
            try{
                if(param.getLocation() == ActionParameterLocation.QUERY){
                    Query +=(URLEncoder.encode(param.getName(),"UTF-8")
                            + "="
                            + URLEncoder.encode(param.getValue(),"UTF-8")
                            + "&"
                    );
                }
            }catch(UnsupportedEncodingException ueex){}
        }
        try{
            return new URL(
                    proto,
                    host,
                    port,
                    path + ((Query.equals("")) ? "":("?"+Query.substring(0,Query.length()-1)))
                );
        }catch(final MalformedURLException muex){
            return null;
        }
    }
    public List<ActionParameter> decompose(WebRequest r){
        //TODO: not complete at all!
        ArrayList<ActionParameter> params = new ArrayList<ActionParameter>();
        params.addAll(decomposeURL(r.getUrl()));
        HttpMethod m = r.getHttpMethod();
        boolean isPost = false;
        if(m == HttpMethod.POST){
            isPost = true;
        }else if(m!=HttpMethod.GET){
            throw new NotImplementedException("Methods other than get and post are not supported");  
        }
        if(isPost){
            String body = r.getRequestBody();
            if(body != null){
                String [] parameters = body.split("&");
                for(String par: parameters){
                    String nameval[] = par.split("=",2);
                    nameval[0] = URLDecoder.decode(nameval[0]);
                    nameval[1] = URLDecoder.decode((nameval.length > 1)?nameval[1]:"");
                    params.add(new ActionParameter(
                            nameval[0],
                            nameval[1],
                            ActionParameterLocation.BODY,
                            decideActionMeaning(nameval[0],ActionParameterLocation.BODY),
                            ActionParameterDatatype.STRING
                    ));
                }
            }else{
                List<NameValuePair> par = r.getRequestParameters();
                for(NameValuePair nvp : par){
                    params.add(new ActionParameter(
                            URLDecoder.decode(nvp.getName()),
                            URLDecoder.decode(nvp.getValue()),
                            ActionParameterLocation.BODY,
                            decideActionMeaning(URLDecoder.decode(nvp.getName()),ActionParameterLocation.BODY),
                            ActionParameterDatatype.STRING
                    ));
                }

            }
        }
        return params;

    }
    public List<ActionParameter> decompose(Request r){
        throw new NotImplementedException(); //TODO: implement
    }
    private static ActionParameter getParamByName(List<ActionParameter> params, String name){
        for(ActionParameter param: params ){
            if(param.getName().equals(name))
                return param;
        }
        return null;
    }

    public Request compose(List<ActionParameter> parameters, UserContext ctx){
        //clone list and work with its copy
        List<ActionParameter>  params = new ArrayList<ActionParameter> ();
        params.addAll(parameters);
        URL url = getURL(params); //preliminary(for cookies)
        if(ctx != null){
            WebAppProperties.getInstance().getPvd().resolve(params,ctx, url);
        }
        Request req = new Request();
        url = getURL(params);
        req.setURL(url);
        String Body="";
        List<Cookie> cookies = new ArrayList<Cookie>();
        for(ActionParameter param: params){
            if(param.getLocation() == ActionParameterLocation.BODY){
                Body +=(URLEncoder.encode(param.getName())
                        + "="
                        + URLEncoder.encode(param.getValue())
                        + "&"
                );
            } else if(param.getLocation() == ActionParameterLocation.HEADER){
                req.addHeader(param);
            } else if (param.getLocation() == ActionParameterLocation.COOKIE){
                cookies.add (new BasicClientCookie(param.getName(), param.getValue()));
            }
        }
        req.setCookies(new CookieDescriptor(cookies,new CookieOrigin(url.getHost(),url.getPort(),"",false), "Cookie", EntityID.NOT_INITIALIZED));
        if(!Body.equals("")){      
            req.setHeader("Content-Type", "application/x-www-form-urlencoded");
            req.setMethod("POST");
            Body = Body.substring(0,Body.length()-1);
            req.setContent(Body.getBytes());
        }
        return req;
    }
    public List<ActionParameter> decomposeURL(URL url) {
        ArrayList<ActionParameter> params = new ArrayList<ActionParameter>();

        params.add(new ActionParameter(
                "host",
                url.getHost(),
                ActionParameterLocation.URL,
                decideActionMeaning("host",ActionParameterLocation.URL),
                ActionParameterDatatype.STRING)
        );
        int port = url.getPort();
        if(port < 0)
            port = 80;
        params.add(new ActionParameter(
                "port",
                Integer.toString(port),
                ActionParameterLocation.URL,
                decideActionMeaning("port",ActionParameterLocation.URL),
                ActionParameterDatatype.NUMBER)
        );
        String query = url.getQuery();
        if(query != null){
            String [] ar = query.split("&");
            for(String pair : ar){
                String [] nameValue = pair.split("=",2);
                params.add(new ActionParameter(
                    URLDecoder.decode(nameValue[0]),
                    URLDecoder.decode( (nameValue.length>1)?nameValue[1]:""),
                    ActionParameterLocation.QUERY,
                    decideActionMeaning(URLDecoder.decode(nameValue[0]),ActionParameterLocation.QUERY),
                    ActionParameterDatatype.STRING)
                );
            }
        }
        String path = url.getPath();
        params.add(new ActionParameter(
                "path",
                path,
                ActionParameterLocation.URL,
                decideActionMeaning("path",ActionParameterLocation.URL),
                ActionParameterDatatype.STRING)
        );

        return params;
    }

    private ActionParameterMeaning decideActionMeaning(String name, ActionParameterLocation loc){
        if(loc == null)
            throw new RuntimeException("decideActionMeaning: location cannot be null");
        if(loc == WebAppProperties.getInstance().getDynTokenLoc(name))
            return WebAppProperties.getInstance().getDynTokenMeaning(name);
        switch(loc){
            case BODY:
                return ActionParameterMeaning.AUTOMATIC;
            case COOKIE:
                return ActionParameterMeaning.SESSIONTOKEN;
            case HEADER:
                return ActionParameterMeaning.SESSIONTOKEN;
            case QUERY:
                return ActionParameterMeaning.AUTOMATIC;
            case URL:
                return ActionParameterMeaning.AUTOMATIC;
            default:
                return ActionParameterMeaning.UNKNOWN;
        }
    }

    public List<ActionParameter> decomposeCookies(String cookies){
        ArrayList<ActionParameter> params = new ArrayList<ActionParameter>();
        String [] cooks = cookies.split("[,;]");
        for(String cook: cooks){
            String [] namevalue = cook.split("=");
            params.add(new ActionParameter(
                    namevalue[0].trim(),
                    namevalue[1].trim(),
                    ActionParameterLocation.COOKIE,
                    decideActionMeaning(namevalue[0].trim(),ActionParameterLocation.COOKIE),
                    ActionParameterDatatype.STRING)
            );
        }
        // TODO: $Version, $Path etc. are not supported yet!
        return params;
        
    }

}
