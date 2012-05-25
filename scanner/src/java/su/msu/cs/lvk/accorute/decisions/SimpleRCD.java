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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String proto = getParamByName(params,"protocol").getValue();
        String host = getParamByName(params,"host").getValue();
        int port = new Integer(getParamByName(params,"port").getValue());
        if(proto.equalsIgnoreCase("http") && port == 80 || proto.equalsIgnoreCase("https") && port == 443){
            port = -1;
        }
        String path = getParamByName(params,"path").getValue();
        ActionParameter frag =  getParamByName(params,"frag");
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
            if(port != -1){
                return new URL(
                        proto,
                        host,
                        port,
                        path + ((Query.equals("")) ? "":("?"+Query.substring(0,Query.length()-1))) + ((frag != null )?"#"+frag.getValue():"")
                );
            }else{
                return new URL(
                        proto,
                        host,
                        path + ((Query.equals("")) ? "":("?"+Query.substring(0,Query.length()-1)))  + ((frag != null )?"#"+frag.getValue():"")
                );
            }
        }catch(final MalformedURLException muex){
            return null;
        }
    }
    public List<ActionParameter> decompose(WebRequest r, Collection<String> userControllable){
        //TODO: not complete at all!
        ArrayList<ActionParameter> params = new ArrayList<ActionParameter>();
        params.addAll(decomposeURL(r.getUrl(), userControllable));
        HttpMethod m = r.getHttpMethod();
        boolean isPost = false;
        if(m == HttpMethod.POST){
            isPost = true;
        }else if(m!=HttpMethod.GET){
            throw new NotImplementedException("Methods other than get and post are not supported");
        }
        for(Map.Entry<String, String> entry : r.getAdditionalHeaders().entrySet()){
            if(entry.getKey().equalsIgnoreCase("Cookie")){
                String cookies = entry.getValue();
                if(cookies != null)
                    params.addAll(decomposeCookies(cookies));
            }else{
                params.add(new ActionParameter(
                        entry.getKey(),
                        entry.getValue(),
                        ActionParameterLocation.HEADER,
                        decideActionMeaning(entry.getKey(),entry.getValue(),ActionParameterLocation.HEADER,userControllable),
                        ActionParameterDatatype.STRING
                ));
            }
        }
        if(isPost){
            String body = r.getRequestBody();
            if(body != null){    
                List<NamedValue> paramList = new ArrayList<NamedValue>();
                if(r.getAdditionalHeaders().containsKey("Content-Type") && r.getAdditionalHeaders().get("Content-Type").contains("application/x-www-form-urlencoded") || !r.getAdditionalHeaders().containsKey("Content-Type")){
                    String []parameters = body.split("&");
                    for(String par: parameters){
                        String nameval[] = par.split("=",2);
                        paramList.add(new NamedValue(nameval[0], (nameval.length > 1)?nameval[1]:""));
                    }
                }else if(r.getAdditionalHeaders().containsKey("Content-Type") && r.getAdditionalHeaders().get("Content-Type").contains("multipart/form-data") ){
                    int index = r.getAdditionalHeaders().get("Content-Type").lastIndexOf("boundary=");
                    String boundary = "--" + r.getAdditionalHeaders().get("Content-Type").substring(index+9).trim();
                    int state = 0; //0 = header, 1: collecting body
                    Pattern p = Pattern.compile(boundary + "\r\nContent-Disposition: form-data; name=\"([^\"]+)\".*?\r\n\r\n(.*?)\r\n"+boundary, Pattern.MULTILINE | Pattern.DOTALL);
                    Matcher matcher = p.matcher(r.getRequestBody());
                    int idx = 0;
                    while(matcher.find(idx)){
                        String name =  matcher.group(1);
                        String value = matcher.group(2);
                        idx = matcher.start(2) + 1;
                        matcher.reset();
                        paramList.add(new NamedValue(name, value));
                    }
                }
                for(NamedValue p: paramList){
                    String name = URLDecoder.decode(p.getName());
                    String val = URLDecoder.decode(p.getValue());
                    params.add(new ActionParameter(
                            name,
                            val,
                            ActionParameterLocation.BODY,
                            decideActionMeaning(name,val,ActionParameterLocation.BODY,userControllable),
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
                            decideActionMeaning(URLDecoder.decode(nvp.getName()),URLDecoder.decode(nvp.getValue()), ActionParameterLocation.BODY,userControllable),
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
    protected static ActionParameter getParamByName(List<ActionParameter> params, String name){
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
                req.addHeader(new NamedValue(param.getName(), param.getValue()));
            } else if (param.getLocation() == ActionParameterLocation.COOKIE){
                BasicClientCookie cookie;
                if(param.getAdditionalData() != null){
                    BasicClientCookie additionalData = (BasicClientCookie) param.getAdditionalData();
                    try {
                        cookie = (BasicClientCookie) additionalData.clone();
                        cookie.setValue(param.getValue());
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    cookie = new BasicClientCookie(param.getName(), param.getValue());
                }
                cookies.add (cookie);
            }
        }
        if(url.getPort() != -1){
            req.setCookies(new CookieDescriptor(cookies,new CookieOrigin(url.getHost(),url.getPort(),"",false), "Cookie", EntityID.NOT_INITIALIZED));
        }else{
            req.setCookies(new CookieDescriptor(cookies,new CookieOrigin(url.getHost(),url.getDefaultPort(),"",false), "Cookie", EntityID.NOT_INITIALIZED));
        }
        if(!Body.equals("")){
            req.setHeader("Content-Type", "application/x-www-form-urlencoded");
            req.setMethod("POST");
            Body = Body.substring(0,Body.length()-1);
            req.setContent(Body.getBytes());
        }
        return req;
    }
    public List<ActionParameter> decomposeURL(URL url){
        return decomposeURL(url, new ArrayList<String>());
    }
    public List<ActionParameter> decomposeURL(URL url, Collection<String> userControllable) {
        ArrayList<ActionParameter> params = new ArrayList<ActionParameter>();
        if(url.getRef() != null){
            params.add(new ActionParameter(
                    "frag",
                    url.getRef(),
                    ActionParameterLocation.URL,
                    ActionParameterMeaning.USERCONTROLLABLE,
                    ActionParameterDatatype.STRING
                )
            );
        }
        params.add(new ActionParameter(
                "host",
                url.getHost(),
                ActionParameterLocation.URL,
                decideActionMeaning("host",url.getHost(), ActionParameterLocation.URL,userControllable),
                ActionParameterDatatype.STRING)
        );
        params.add(new ActionParameter(
                "protocol",
                url.getProtocol(),
                ActionParameterLocation.URL,
                decideActionMeaning("protocol",url.getProtocol(), ActionParameterLocation.URL,userControllable),
                ActionParameterDatatype.STRING)
        );
        int port = url.getPort();
        if(port < 0){
            port = url.getDefaultPort();
        }
        params.add(new ActionParameter(
                "port",
                Integer.toString(port),
                ActionParameterLocation.URL,
                decideActionMeaning("port",Integer.toString(port), ActionParameterLocation.URL,userControllable),
                ActionParameterDatatype.NUMBER)
        );
        String query = url.getQuery();
        if(query != null){
            String [] ar = query.split("&");
            for(String pair : ar){
                String [] nameValue = pair.split("=",2);
                String name =URLDecoder.decode(nameValue[0]);
                String value = URLDecoder.decode((nameValue.length > 1) ? nameValue[1] : "");
                params.add(new ActionParameter(
                        name,
                        value,
                        ActionParameterLocation.QUERY,
                        decideActionMeaning(name, value,ActionParameterLocation.QUERY,userControllable),
                        ActionParameterDatatype.STRING)
                );
            }
        }
        String path = url.getPath();
        params.add(new ActionParameter(
                "path",
                path,
                ActionParameterLocation.URL,
                decideActionMeaning("path",path, ActionParameterLocation.URL,userControllable),
                ActionParameterDatatype.STRING)
        );

        return params;
    }
    protected ActionParameterMeaning decideActionMeaning(String name,String value, ActionParameterLocation loc){
        return decideActionMeaning(name, value, loc, new ArrayList<String>());
    }
    protected ActionParameterMeaning decideActionMeaning(String name,String value, ActionParameterLocation loc, Collection<String> uControllable){
        if(loc == null)
            throw new RuntimeException("decideActionMeaning: location cannot be null");
        if(WebAppProperties.getInstance().isDynToken(name, value,loc))
            return WebAppProperties.getInstance().getDynTokenMeaning(name, value, loc);
        List<Pattern> regexList = WebAppProperties.getInstance().getIdParamNameRegexList();
        for(Pattern p : regexList){
            Matcher idNameMatcher = p.matcher(name);
            if(idNameMatcher.matches())
                return ActionParameterMeaning.IDENTIFIER;
        }
        switch(loc){
            case BODY:{
                if(uControllable.contains(name))
                    return ActionParameterMeaning.USERCONTROLLABLE;
                return ActionParameterMeaning.AUTOMATIC;
            }
            case COOKIE:
                return ActionParameterMeaning.SESSIONTOKEN;
            case HEADER:
                return ActionParameterMeaning.USERCONTROLLABLE;
            case QUERY:{
                if(uControllable.contains(name))
                    return ActionParameterMeaning.USERCONTROLLABLE;
                return ActionParameterMeaning.AUTOMATIC;
            }
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
            String [] namevalue = cook.split("=",2);
            if(namevalue[0].trim().startsWith("$"))
                continue;
            params.add(new ActionParameter(
                    namevalue[0].trim(),
                    namevalue[1].trim(),
                    ActionParameterLocation.COOKIE,
                    decideActionMeaning(namevalue[0].trim(),namevalue[1].trim(), ActionParameterLocation.COOKIE),
                    ActionParameterDatatype.STRING)
            );
        }
        // TODO: $Version, $Path etc. are not supported yet!
        return params;

    }

}