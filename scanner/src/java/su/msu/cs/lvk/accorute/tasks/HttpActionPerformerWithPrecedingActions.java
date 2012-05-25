package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback3;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.12.2010
 * Time: 0:21:06
 * To change this template use File | Settings | File Templates.
 */
public class HttpActionPerformerWithPrecedingActions extends Task{
    EntityID ctxID;
    HttpAction act;
    public HttpActionPerformerWithPrecedingActions(TaskManager t,  EntityID ctxID, HttpAction act) {
        this(t, ctxID,act, false);
    }
    public HttpActionPerformerWithPrecedingActions(TaskManager t,  EntityID ctxID, HttpAction act,boolean ser) {
        super(t, ser);
        this.ctxID = ctxID;
        this.act = act;
    }

    @Override
    public Object getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    private boolean hasExactMatchingIdentifiers(HttpAction a, List<HttpAction> templates){
        List<ActionParameter> aParam = a.getActionParameters();
        for(HttpAction b: templates){
            List<ActionParameter> bParam = b.getActionParameters();
            ListIterator<ActionParameter> it = aParam.listIterator();
            boolean exact_match = true;
            while(it.hasNext()){
                ActionParameter param = it.next();
                String pName = param.getName();
                List<Pattern> idNamePatterns = WebAppProperties.getInstance().getIdParamNameRegexList();
                List<Pattern> idValuePatterns = WebAppProperties.getInstance().getIdParamValueRegexList();
                boolean foundMatch = false;
                for(int i = 0; i < idNamePatterns.size(); i++){
                    Matcher nameMatcher = idNamePatterns.get(i).matcher(pName);
                    Matcher valMatcher = idValuePatterns.get(i).matcher(param.getValue());
                    if(nameMatcher.matches()&&valMatcher.matches() ){
                        foundMatch = true;
                        break;
                    }
                }
                if(!foundMatch)
                    continue;
                boolean found = false;
                boolean matched = false;
                for(ActionParameter p: bParam){
                    if(p.getName().equals(pName)){
                        found = true;
                        matched = p.getValue().equals(param.getValue());
                        break;
                    }
                }
                if(!found || !matched){
                    exact_match = false;
                    break;
                }
            }
            if(exact_match)
                return true;
        }
        return false;
    }
    private void fixActionIdentifiers(HttpAction a, HttpAction b){
        List<ActionParameter> aParam = a.getActionParameters();
        List<ActionParameter> bParam = b.getActionParameters();
        ListIterator<ActionParameter> it = aParam.listIterator();
        List<ActionParameter> toAdd = new ArrayList<ActionParameter>();
        while(it.hasNext()){
            ActionParameter param = it.next();
            String pName = param.getName();
            List<Pattern> idNamePatterns = WebAppProperties.getInstance().getIdParamNameRegexList();
            List<Pattern> idValuePatterns = WebAppProperties.getInstance().getIdParamValueRegexList();
            boolean foundMatch = false;
            int matchIndex = 0;
            for(int i = 0; i < idNamePatterns.size(); i++){
                Matcher nameMatcher = idNamePatterns.get(i).matcher(pName);
                Matcher valMatcher = idValuePatterns.get(i).matcher(param.getValue());
                if(nameMatcher.matches()&&valMatcher.matches() ){
                    foundMatch = true;
                    matchIndex = i;
                    break;
                }
            }
            if(!foundMatch)
                continue;
            ActionParameter template = null;
            for(ActionParameter p: bParam){
                if(p.getName().equals(pName) && idValuePatterns.get(matchIndex).matcher(p.getValue()).matches()){
                    template = p;
                }
            }
            if(template == null)
                continue;
            it.remove();
            toAdd.add(template);
        }
        for(ActionParameter p: toAdd){
            aParam.add(p);
        }
        a.setActionParameters(aParam);
    }
    @Override
    protected void start() {
        Sitemap smap = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(ctxID);
        Sitemap.SitemapEdge e = smap.getEdgePreceedingNeededAction(act);
        if(e == null){
            logger.error("Action " + act + " not found in the sitemap!!!");
            setSuccessful(false);
            return;
        }
        Set<HttpAction> preActs = smap.getInbound(e.getV1());
        if(preActs.size() == 0){
            logger.error("Inbound actions are absent!");
            setSuccessful(false);
            return;
        }
        HttpAction preAct = preActs.iterator().next();
        Task preTask = new HtmlElementActionPerformer(
                    taskManager,
                    preAct,
                    ctxID,
                    new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                        public void CallMeBack(ArrayList<Conversation> c , ArrayList<HttpAction> a, HtmlPage p){
                            // do nothing
                        }
                    }
        );
        waitForTask(preTask);


        if(!preTask.isSuccessful()){
            logger.error("Pre task not successful!");
            setSuccessful(false);
            return;
        }
        if(!hasExactMatchingIdentifiers(act,e.getLabel().getHttpActions())){
            fixActionIdentifiers(act,e.getLabel().getHttpActions().get(0));
        }
        Task task = new HtmlElementActionPerformer(
                    taskManager,
                    act,
                    ctxID,
                    new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                        public void CallMeBack(ArrayList<Conversation> c , ArrayList<HttpAction> a, HtmlPage p){
                            // do nothing
                        }
                    }
        );
        waitForTask(task);
        if(!task.isSuccessful()){
            logger.error("task not successful!");
            setSuccessful(false);
        }else{
            setSuccessful(true);
        }
    }
}
