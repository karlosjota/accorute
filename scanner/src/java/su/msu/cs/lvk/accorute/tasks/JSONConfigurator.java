package su.msu.cs.lvk.accorute.tasks;

import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.SimpleRBACRole;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.SerialTask;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.04.2010
 * Time: 9:13:23
 * To change this template use File | Settings | File Templates.
 */
public class JSONConfigurator extends SerialTask{
    private static Logger logger = Logger.getLogger(JSONConfigurator.class.getName());

    private static class UnsupportedDOMEventTypeException extends Exception{
        public UnsupportedDOMEventTypeException(){
            super();
        }
        public UnsupportedDOMEventTypeException(String err){
            super("Unsupported evant type: " + err);
        }
    }
    private static class JSONEventParser{
        public static HttpAction parse(JSONObject event) throws JSONException, MalformedURLException, MalformedCookieException,UnsupportedDOMEventTypeException {
            String type = event.getString("type");
            HttpAction act;
            if(type.equals("pageLoaded")){
                String URL = event.getJSONObject("document").getString("location");
                List<ActionParameter> params;
                try {
                    params = WebAppProperties.getInstance().getRcd().decomposeURL(URL);
                } catch (java.net.MalformedURLException e) {
                    throw e;
                }
                act = new HttpAction("",params);
            }else if(type.equals("formSubmitted")){
                String method = event.getString("method");
                String action = event.getString("action");
                if(action.equals("")){
                    action = event.getJSONObject("document").getString("location");
                }
                String cookies = event.getString("cookie");
                List<ActionParameter> params = WebAppProperties.getInstance().getRcd().decomposeCookies(cookies);

                JSONArray elements = event.getJSONArray("elements");
                for(int i=0; i< elements.length();i++){
                    String name = elements.getJSONObject(i).getString("name");
                    String value = elements.getJSONObject(i).getString("value");
                    String eltype = elements.getJSONObject(i).getString("type");
                    if(eltype.equalsIgnoreCase("submit") && name.equals("")){
                        name = eltype;
                    }
                    params.add(new ActionParameter(
                        name,
                        value,
                        method.equalsIgnoreCase("GET") ? ActionParameterLocation.QUERY : ActionParameterLocation.BODY,
                        (eltype.equalsIgnoreCase("hidden") || eltype.equalsIgnoreCase("submit")) ?
                                ActionParameterMeaning.AUTOMATIC : ActionParameterMeaning.USERCONTROLLABLE,
                        ActionParameterDatatype.STRING)
                    );//TODO: be more specific here!!!
                }
                params.addAll(WebAppProperties.getInstance().getRcd().decomposeURL(action));
                act = new HttpAction("",params);

            }else if(type.equals("linkClicked")){
                String URL = event.getString("href");
                String cookies = event.getString("cookie");
                List<ActionParameter> params = WebAppProperties.getInstance().getRcd().decomposeCookies(cookies);
                params.addAll(WebAppProperties.getInstance().getRcd().decomposeURL(URL));
                act = new HttpAction("",params);
            }else{
                throw new UnsupportedDOMEventTypeException(type);
            }
            return act;
        }
    }


    public final String filename;
    public final TestChain t = new TestChain();
    public JSONConfigurator(TaskManager t, String filename){
        super(t);
        this.filename = filename;
        
    }
    public void start(){
        InputStream in = null;
        try{
            try{
                in = new FileInputStream(filename);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                byte[] buffer = new byte[(int) new File(filename).length()];
                BufferedInputStream f = new BufferedInputStream(in);
                f.read(buffer);
                String contents = new String(buffer);
                JSONObject obj = new JSONObject(contents);
                String loc = obj.getString("sessTokenLocation");
                if(loc.equalsIgnoreCase("query")){
                   WebAppProperties.getInstance().setSessionTokenLocation(ActionParameterLocation.QUERY);
                }else if(loc.equalsIgnoreCase("body")){
                   WebAppProperties.getInstance().setSessionTokenLocation(ActionParameterLocation.BODY);
                }else if(loc.equalsIgnoreCase("cookie")){
                   WebAppProperties.getInstance().setSessionTokenLocation(ActionParameterLocation.COOKIE);
                }
                WebAppProperties.getInstance().setSessionTokenName(obj.getString("sessTokenName"));

                JSONArray roles = obj.getJSONArray("roles");
                for(int i=0;i < roles.length();i++){
                    JSONObject user = roles.getJSONObject(i);
                    String roleName = user.getString("name");
                    String parentRoleName = user.getString("parent");
                    Role parent = null;
                    for(Role role : WebAppProperties.getInstance().getRoles()){
                        if(role.getRoleName().equalsIgnoreCase(parentRoleName)){
                            parent = role;
                        }
                    }
                    if(parent != null){
                        WebAppProperties.getInstance().getRoles().add(parent.addChildRole(roleName));
                        logger.trace("Added role " + roleName + ", child of " + parentRoleName);
                    }else{
                        WebAppProperties.getInstance().getRoles().add(SimpleRBACRole.createRootRole(roleName));
                        logger.trace("Added role " + roleName + ", a root role");
                    }
                }
                JSONArray users = obj.getJSONArray("users");
                for(int i=0;i < users.length();i++){
                    JSONObject user = users.getJSONObject(i);
                    String userName = user.getString("name");
                    String roleName = user.getString("role");
                    Role r = null;
                    for(Role role : WebAppProperties.getInstance().getRoles()){
                        if(role.getRoleName().equalsIgnoreCase(roleName)){
                            r = role;
                        }
                    }
                    WebAppUser u = new WebAppUser();
                    JSONObject creds = user.getJSONObject("credentials");
                    Iterator it = creds.keys();
                    while(it.hasNext()){
                        String key = (String)it.next();
                        String val = creds.getString(key);
                        u.getStaticCredentials().add(new NamedValue(key, val));
                    }
                    u.getStaticCredentials().add(new NamedValue("username", userName ));
                    u.setRole(r);
                    WebAppProperties.getInstance().getUserService().addOrModifyUser(u);
                    logger.trace("Added user " + userName + " of role " + r.getRoleName());
                }

                //parse the trace...
                JSONArray sessions = obj.getJSONArray("trace");
                logger.trace("The config contains " + sessions.length() + " recorded sessions");
                int counter = 0;
                for(int i = 0; i < sessions.length();i++){
                    JSONArray evts = sessions.getJSONArray(i);
                    JSONObject sessionCreatedEvt = evts.getJSONObject(0);
                    String uname = sessionCreatedEvt.getString("user");
                    String sessname = sessionCreatedEvt.getString("name");
                    WebAppUser u = WebAppProperties.getInstance().getUserService().getUsersByCredential("username", uname).get(0);
                    logger.trace("Parsing UC \"" + sessname + "\" of user " + uname + ". The UC record contains " + evts.length() + " events");
                    for(int j=1; j < evts.length(); j++){
                        JSONObject evt = evts.getJSONObject(j);
                        String evtType = evt.getString("type");
                        if (evtType.equals("UCCreated") && j != evts.length() - 1 ){
                            j++;
                            JSONObject firstAction = evts.getJSONObject(j);
                            logger.trace("Parsing evt " + j + ", type: " +evtType);
                            try{
                                HttpAction act = JSONEventParser.parse(firstAction);
                                if(act.getName().equals("")){
                                    act.setName("#" + new Integer(counter++).toString()+"("+sessname  +")" ) ;
                                }
                                WebAppProperties.getInstance().getActionService().addOrUpdateAction(act);
                                WebAppProperties.getInstance().addStateChangingAction(act);
                                logger.trace("Recorded state-changing action:\n" +act);
                                t.add(act, u);
                            }
                            catch(UnsupportedDOMEventTypeException udatex){
                                logger.warn(udatex);
                            }
                        }
                    }
                }
                WebAppProperties.getInstance().setTestChain(t);
                setSuccessful(true);
            }finally{
                if (in != null) {
                    in.close();
                }
            }
        }
        catch (JSONException jsex){
            logger.warn("JSON:",jsex);
            setSuccessful(false);
        }
        catch(IOException ioex){
            logger.warn("IO:",ioex);
            setSuccessful(false);
        }

        
    }
    synchronized public Object getResult(){
        if(getStatus() == TaskStatus.FINISHED){
            return isSuccessful();
        }
        return null;
    }
}
