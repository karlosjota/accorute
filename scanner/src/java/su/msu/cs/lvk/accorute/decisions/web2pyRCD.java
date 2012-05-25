package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 4/28/11
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class web2pyRCD extends SimpleRCD{

    @Override
    public List<ActionParameter> decomposeURL(String u) throws MalformedURLException {
        return decomposeURL(new URL(u));
    }
    private static List<ActionParameter> getParamsStartsWith(List<ActionParameter> params, String pattern){
        List<ActionParameter> ret = new ArrayList<ActionParameter>();
        for(ActionParameter param: params ){
            if(param.getName().startsWith(pattern))
                ret.add(param);
        }
        return ret;
    }

    @Override
    public URL getURL(List<ActionParameter> params) {
        String Query = "";
        String proto = "http";
        String host = getParamByName(params,"host").getValue();
        int port = new Integer(getParamByName(params,"port").getValue());
        if(port == -1){
            port = 80;
        }
        String path = "/" + getParamByName(params,"__web2py_application").getValue() +
                      "/" + getParamByName(params,"__web2py_controller").getValue()  +
                      "/" + getParamByName(params,"__web2py_function").getValue();
        List<ActionParameter> positionalArgs = getParamsStartsWith(params, "__web2py_positional_argument");
        List<String> positionalArgValues = new LinkedList<String>();
        List<Integer> positionalArgNums = new LinkedList<Integer>();
        for(ActionParameter param: positionalArgs){
            Integer num = Integer.valueOf(param.getName().replaceAll("__web2py_positional_argument",""));
            Integer where = null;
            for(int i=0;i<positionalArgNums.size();i++){
                if(positionalArgNums.get(i) > num){
                    where =  positionalArgNums.get(i);
                    break;
                }
            }
            if(where == null){
                positionalArgNums.add(num);
                positionalArgValues.add(param.getValue());
            }else{
                positionalArgNums.add(where,num);
                positionalArgValues.add(where,param.getValue());
            }
        }
        for(int i=0; i< positionalArgValues.size();i++){
            path += ("/"+positionalArgValues.get(i));
        }
        for(ActionParameter param: params){
            try{
                if(param.getLocation() == ActionParameterLocation.QUERY){
                    Query +=(URLEncoder.encode(param.getName(), "UTF-8")
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

    @Override
    public List<ActionParameter> decomposeURL(URL url) {
        return decomposeURL(url, new ArrayList<String>());
    }

    @Override
    public List<ActionParameter> decomposeURL(URL url, Collection<String> userControllable) {
        ArrayList<ActionParameter> params = new ArrayList<ActionParameter>();
        params.add(new ActionParameter(
                "host",
                url.getHost(),
                ActionParameterLocation.URL,
                super.decideActionMeaning("host", url.getHost(), ActionParameterLocation.URL, userControllable),
                ActionParameterDatatype.STRING)
        );
        int port = url.getPort();
        if(port < 0)
            port = 80;
        params.add(new ActionParameter(
                "port",
                Integer.toString(port),
                ActionParameterLocation.URL,
                super.decideActionMeaning("port", Integer.toString(port), ActionParameterLocation.URL, userControllable),
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
                        super.decideActionMeaning(URLDecoder.decode(nameValue[0]),URLDecoder.decode( (nameValue.length>1)?nameValue[1]:""),  ActionParameterLocation.QUERY, userControllable),
                        ActionParameterDatatype.STRING)
                );
            }
        }
        String path = url.getPath();
        while(path.startsWith("/"))
            path = path.replaceFirst("/","");
        String [] ar = path.split("\\/");
        params.add(new ActionParameter(
                "__web2py_application",
                (ar.length >= 1)?ar[0]:"",
                ActionParameterLocation.URL,
                super.decideActionMeaning("__web2py_application",(ar.length >= 1)?ar[0]:"",ActionParameterLocation.URL,userControllable),
                ActionParameterDatatype.STRING)
        );
        params.add(new ActionParameter(
                "__web2py_controller",
                (ar.length >= 2)?ar[1]:"",
                ActionParameterLocation.URL,
                super.decideActionMeaning("__web2py_controller",(ar.length >= 2)?ar[1]:"",ActionParameterLocation.URL,userControllable),
                ActionParameterDatatype.STRING)
        );
        params.add(new ActionParameter(
                "__web2py_function",
                (ar.length >= 3)?ar[2]:"",
                ActionParameterLocation.URL,
                super.decideActionMeaning("__web2py_function",(ar.length >= 3)?ar[2]:"",ActionParameterLocation.URL,userControllable),
                ActionParameterDatatype.STRING)
        );
        for(int i=3; i< ar.length; i++){
            String pname = "__web2py_positional_argument"+(i-3);
            params.add(new ActionParameter(
                    pname,
                    ar[i],
                    ActionParameterLocation.URL,
                    super.decideActionMeaning(pname, ar[i], ActionParameterLocation.URL,userControllable),
                    ActionParameterDatatype.STRING)
            );
        }
        return params;
    }
}
