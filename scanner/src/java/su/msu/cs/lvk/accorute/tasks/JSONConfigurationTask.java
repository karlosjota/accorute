package su.msu.cs.lvk.accorute.tasks;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.SimpleRBACRole;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.SerialTask;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.io.*;
import java.net.MalformedURLException;
import java.util.List;

import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.04.2010
 * Time: 9:13:23
 * To change this template use File | Settings | File Templates.
 */
public class JSONConfigurationTask extends SerialTask{
    private static Logger logger = Logger.getLogger(JSONConfigurationTask.class.getName());
    private static class UnsupportedDOMEventTypeException extends Exception{
        public UnsupportedDOMEventTypeException(){
            super();
        }
        public UnsupportedDOMEventTypeException(String err){
            super("Unsupported evant type: " + err);
        }
    }
    private static class JSONEventParser{
        public static Action parse(JSONObject event) throws JSONException, MalformedURLException, UnsupportedDOMEventTypeException {
            String type = event.getString("type");
            Action act;
            if(type.equals("pageLoaded")){
                String URL = event.getJSONObject("document").getString("location");
                List<ActionParameter> params;
                try {
                    params = WebAppProperties.getInstance().getRcd().decomposeURL(URL);
                } catch (java.net.MalformedURLException e) {
                    throw e;
                }
                act = new Action("",params);
            }else if(type.equals("formSubmitted")){
                //TODO: implement
                throw new JSONException("Not an event record!");
            }else if(type.equals("linkClicked")){
                //TODO: cookies!!!
                String URL = event.getJSONObject("document").getString("location");
                List<ActionParameter> params;
                try {
                    params = WebAppProperties.getInstance().getRcd().decomposeURL(URL);
                } catch (java.net.MalformedURLException e) {
                    throw e;
                }
                act = new Action("",params);
            }else{
                throw new UnsupportedDOMEventTypeException(type);
            }
            return act;
        }
    }


    public final String filename;
    public final TestChain t = new TestChain();
    public JSONConfigurationTask(TaskManager t, String filename){
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
                    //this will be changed
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
                    WebAppUser u = WebAppProperties.getInstance().getUserService().getUsersByCredential("username", uname).get(0);
                    logger.trace("Parsing session " + i + " of user " + uname + ". The session contains " + evts.length() + " events");
                    for(int j=1; j < evts.length(); j++){
                        JSONObject evt = evts.getJSONObject(j);
                        String evtType = evt.getString("type");
                        logger.trace("Parsing evt " + j + ", type: " +evtType);

                        if (evtType.equals("UCCreated") && j != evts.length() - 1 ){
                            JSONObject firstAction = evts.getJSONObject(j+1);
                            try{
                                Action act = JSONEventParser.parse(firstAction);
                                act.setName(new Integer(counter++).toString() );
                                WebAppProperties.getInstance().getActionService().addOrUpdateAction(act);
                                WebAppProperties.getInstance().addStateChangingAction(act);
                                t.add(act, u);
                                j++;
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
