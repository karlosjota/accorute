package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.HttpAction;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 4/27/11
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class StaticAnalysisAcChStDec implements ActionChangesStateDecision{
    public StaticAnalysisAcChStDec(String fname) throws IOException{
        FileInputStream fstream = new FileInputStream(fname);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String url;
        while( (url = br.readLine()) != null){
            String paramNames = br.readLine();
            HashSet<String> params = new HashSet<String>();
            if(paramNames == null)
                break;
            for(String s: paramNames.split("\\s")){
                params.add(s);
            }
            if(!stateChanging.containsKey(url)){
                stateChanging.put(url,new ArrayList<HashSet<String>>());
            }
            stateChanging.get(url).add(params);
        }
        in.close();
    }

    final private Map<String,ArrayList<HashSet<String>> > stateChanging = new HashMap<String, ArrayList<HashSet<String>>> ();
    public boolean changesState(HttpAction action) {
        List<ActionParameter> params = action.getActionParameters();
        String path = null;
        HashMap<String, ActionParameter> availParams = new HashMap<String, ActionParameter>();
        for(ActionParameter p : params){
            availParams.put(p.getName(),p);
            if(p.getName().equals("path")){
                path = p.getValue();
            }
        }
        if(path == null)
            return false;
        ArrayList<HashSet<String>> neededParamsVariants = null;
        for(String url: stateChanging.keySet()){
            if(path.endsWith(url)){
                neededParamsVariants = stateChanging.get(url);
            }
        }
        if(neededParamsVariants == null)
            return false;
        for(HashSet<String> neededParams: neededParamsVariants){
            boolean flag = true;
            for(String reqParam: neededParams){
                if(!availParams.keySet().contains(reqParam)){
                    flag = false;
                    break;
                }
            }
            if(!flag)
                continue;
            return true;
            /*for(Map.Entry<String, ActionParameter> entry:availParams.entrySet()){
                if(!neededParams.contains(entry.getKey()) && (
                       entry.getValue().getLocation() == ActionParameterLocation.QUERY  ||
                       entry.getValue().getLocation() == ActionParameterLocation.BODY
                    )  )
                {
                    flag = false;
                    break;
                }
            }
            if(!flag)
                continue;
            return true;   */
        }
        return false;
    }
}
