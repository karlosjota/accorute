package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.decisions.MultiStateFormFillFactory;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.RoleCompare;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 09.05.12
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
public class FullAnalysisTask extends Task {
    @Override
    public Object getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void start() {
        long tstart =    System.nanoTime();
        TaskManager taskman = getTaskManager();
        File reportDir = new File("report");
        for(File f : reportDir.listFiles()){
            if(f.getAbsolutePath().endsWith(".dot")|| f.getAbsolutePath().endsWith(".png")|| f.getAbsolutePath().endsWith(".xml"))
                f.delete();
        }
        final Map<EntityID, HtmlPage> currentPages = new HashMap<EntityID, HtmlPage>();
        final UseCaseGraph graph = WebAppProperties.getInstance().getUcGraph();
        Iterator<UseCase> it = graph.dependencyRespectingIterator();
        TaskGroup loginGroup = new TaskGroup(taskman, "Initial login");
        HttpAction startAct;
        startAct = new HttpAction("initial",WebAppProperties.getInstance().getRcd().decomposeURL(
                WebAppProperties.getInstance().getMainPage()
        ));
        for(Role r: WebAppProperties.getInstance().getRoles()){
            WebAppUser u1 = WebAppProperties.getInstance().getUserService().getUsersByRole(r.getRoleName()).get(0);
            UserContext u1Ctx = new UserContext();
            u1Ctx.setUserID(u1.getUserID());
            WebAppProperties.getInstance().getContextService().addOrUpdateContext(u1Ctx);
            WebAppUser u2 = WebAppProperties.getInstance().getUserService().getUsersByRole(r.getRoleName()).get(1);
            UserContext u2Ctx = new UserContext();
            u2Ctx.setUserID(u2.getUserID());
            WebAppProperties.getInstance().getContextService().addOrUpdateContext(u2Ctx);
        }
        Map<String, EntityID> users1 = new HashMap<String, EntityID>();
        Map<String, EntityID> users2 = new HashMap<String, EntityID>();
        for(Role r: WebAppProperties.getInstance().getRoles()){
            WebAppUser u1 = WebAppProperties.getInstance().getUserService().getUsersByRole(r.getRoleName()).get(0);
            final EntityID ctxID1 = WebAppProperties.getInstance().getContextService().getContextsByUserID(u1.getUserID()).get(0).getContextID();
            WebAppUser u2 = WebAppProperties.getInstance().getUserService().getUsersByRole(r.getRoleName()).get(1);
            final EntityID ctxID2 = WebAppProperties.getInstance().getContextService().getContextsByUserID(u2.getUserID()).get(0).getContextID();
            users1.put(r.getRoleName(),ctxID1);
            users2.put(r.getRoleName(),ctxID2);
            if(!r.getRoleName().equalsIgnoreCase("public")){
                final Task t1 = WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(ctxID1, taskman);
                t1.registerCallback(new Callback0() {
                    public void CallMeBack() {
                        synchronized (currentPages) {
                            currentPages.put(ctxID1, (HtmlPage) t1.getResult());
                        }
                    }
                });
                final Task t2 = WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(ctxID2, taskman);
                t2.registerCallback(new Callback0() {
                    public void CallMeBack() {
                        synchronized (currentPages) {
                            currentPages.put(ctxID2, (HtmlPage) t2.getResult());
                        }
                    }
                });
                loginGroup.addTsk(t1);
                loginGroup.addTsk(t2);
            }else{
                final Task t1 = new HtmlElementActionPerformer(taskman,WebAppProperties.getInstance().getMainPage(), ctxID1, new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>() {
                    public void CallMeBack(ArrayList<Conversation> param1, ArrayList<HttpAction> param2, HtmlPage param3) {
                        synchronized (currentPages) {
                            currentPages.put(ctxID1,param3);
                        }
                    }
                });
                final Task t2 = new HtmlElementActionPerformer(taskman,WebAppProperties.getInstance().getMainPage(), ctxID2, new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>() {
                    public void CallMeBack(ArrayList<Conversation> param1, ArrayList<HttpAction> param2, HtmlPage param3) {
                        synchronized (currentPages) {
                            currentPages.put(ctxID2,param3);
                        }
                    }
                });
                loginGroup.addTsk(t1);
                loginGroup.addTsk(t2);
            }
        }
        waitForTask(loginGroup);
        boolean started = false;
        Document rootDocument;
        Element rootElement;
        Element usecases;
        Element summary;
        final Map<String, Element> summaryRolePairs = new HashMap<String, Element>();
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            rootDocument = docBuilder.newDocument();
            rootElement = rootDocument.createElement("WebApp");
            rootDocument.appendChild(rootElement);
            usecases = rootDocument.createElement("usecases");
            rootElement.appendChild(usecases);
            summary = rootDocument.createElement("summary");
            rootElement.appendChild(summary);
        }  catch (Exception e) {
            logger.fatal(e);
            return;
        }
        String ucName = "start";
        int ucNum = 0;
        final List<HttpAction> stateChangingSpikes = new ArrayList<HttpAction>();
        final List<HttpAction> spikes = new ArrayList<HttpAction>();
        do{
            final Element curState = rootDocument.createElement("usecase");
            usecases.appendChild(curState);
            if(started){
                UseCase uc = it.next();
                HttpAction action = uc.getHttpAct();
                EntityID ctxID = users1.get(uc.getUserRole().getRoleName());
                Task t = new HttpActionPerformerWithPrecedingActions(taskman,ctxID,action);
                addWaitedTask(t);
                waitForSpawnedTasks();
                curState.setAttribute("name",uc.getHttpAct().getName());
                ucName = uc.getHttpAct().getName();
                curState.setAttribute("role",uc.getUserRole().getRoleName());
                uc.getHttpAct().appendToElement(curState);
            }
            started = true;
            //2. Build sitemaps
            TaskGroup sitemapGroup = new TaskGroup(taskman, "Build sitemaps");
            for(Role r: WebAppProperties.getInstance().getRoles()){
                EntityID ctxID1 = users1.get(r.getRoleName());
                EntityID ctxID2 = users2.get(r.getRoleName());
                WebAppProperties.getInstance().getConversationService().clearContextConversations(ctxID1);
                WebAppProperties.getInstance().getConversationService().clearContextConversations(ctxID2);
                //waitForTask(new SitemapCrawler(taskman, currentPages.get(ctxID1), ctxID1));
                //waitForTask(new SitemapCrawler(taskman, currentPages.get(ctxID2), ctxID2));
                sitemapGroup.addTsk(new SitemapCrawler(taskman, currentPages.get(ctxID1), ctxID1));
                sitemapGroup.addTsk(new SitemapCrawler(taskman, currentPages.get(ctxID2), ctxID2));
            }
            sitemapGroup.setConcurrent(false);
            waitForTask(sitemapGroup);
            for(final Role role: WebAppProperties.getInstance().getRoles()){
                String rName = role.getRoleName();
                EntityID u1 = users1.get(rName);
                EntityID u2 = users2.get(rName);
                WebAppProperties.getInstance().getSitemapService().getSitemapForContext(u1).writeToFile(
                        "report/"+ucNum+ucName+"_"+rName + "_1.dot",rName + "_1"
                );
                WebAppProperties.getInstance().getSitemapService().getSitemapForContext(u2).writeToFile(
                        "report/"+ucNum+ucName+"_"+rName + "_2.dot",rName + "_2"
                );
            }
            taskManager.cleanTree();
            TaskGroup spikeGroup = new TaskGroup(taskman, "Perform spike detection");
            //3. Perform spike detection
            for(final Role attackRole: WebAppProperties.getInstance().getRoles()){
                for(final Role victimRole: WebAppProperties.getInstance().getRoles()){
                    EntityID attacker = users2.get(attackRole.getRoleName());
                    EntityID victim = users1.get(victimRole.getRoleName());
                    if(RoleCompare.less(victimRole, attackRole) >= 0){
                        //if(attackRole.getRoleName().equalsIgnoreCase("user") && victimRole.getRoleName().equalsIgnoreCase("admin") ){
                        final Task t = new SimpleDetectSpikes(taskman,attacker,victim);
                        final Element summry = summary;
                        t.registerCallback(
                                new Callback0(){
                                    public void CallMeBack() {
                                        Set<HttpAction> acts = (Set<HttpAction>)t.getResult();
                                        //TODO: this should be configurable
                                        for(HttpAction act:acts){
                                            if(WebAppProperties.getInstance().getChStateDec().changesState(act)){
                                                logger.fatal("Found state-changing spike!");
                                                stateChangingSpikes.add(act);
                                            }
                                        }
                                        Element spikeSet = curState.getOwnerDocument().createElement("spikeset");
                                        curState.appendChild(spikeSet);
                                        spikeSet.setAttribute("from",attackRole.getRoleName());
                                        spikeSet.setAttribute("to",victimRole.getRoleName());
                                        String rolePair =  attackRole.getRoleName() + "->" + victimRole.getRoleName();
                                        Element summarySpikes;
                                        if(summaryRolePairs.containsKey(rolePair)){
                                            summarySpikes =  summaryRolePairs.get(rolePair);
                                        }else{
                                            summarySpikes =  curState.getOwnerDocument().createElement("spikeset");
                                            summarySpikes.setAttribute("from",attackRole.getRoleName());
                                            summarySpikes.setAttribute("to",victimRole.getRoleName());
                                            summry.appendChild(summarySpikes);
                                            summaryRolePairs.put(rolePair,summarySpikes);
                                        }
                                        Iterator<HttpAction> it = acts.iterator();
                                        while(it.hasNext()){
                                            HttpAction act = it.next();
                                            boolean fresh = true;
                                            for(HttpAction a: spikes){
                                                if(WebAppProperties.getInstance().getAcEqDec().ActionEqualsIgnoreIdentifiers(a,act)){
                                                    fresh = false;
                                                    break;
                                                }
                                            }
                                            act.appendToElement(spikeSet);
                                            if(fresh){
                                                act.appendToElement(summarySpikes);
                                                spikes.add(act);
                                            }
                                        }
                                    }
                                }
                        );
                        spikeGroup.addTsk(t);
                        //}
                    }
                }
            }
            spikeGroup.setConcurrent(false);
            waitForTask(spikeGroup);
            taskManager.cleanTree();
            ucNum++;
            try{
                //Output the XML
                //set up a transformer
                TransformerFactory transfac = TransformerFactory.newInstance();
                Transformer trans = transfac.newTransformer();
                trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                trans.setOutputProperty(OutputKeys.INDENT, "yes");
                trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                //create string from xml tree
                // get the supporting classes for the transformer
                FileWriter writer = new FileWriter("report/report"+ucNum+".xml");
                StreamResult result = new StreamResult(writer);
                DOMSource source = new DOMSource(curState);
                // transform the xml document into a string
                trans.transform(source, result);
                writer.append("Spike finding invokations: " + SimpleDetectSpikes.numInvokations);
                writer.append("Spike checks: " + SimpleDetectSpikes.numChecks);
                writer.append("Raw spikes detected: " + SimpleDetectSpikes.numRawSpikes);
                writer.append("Unique spikes: " + spikes.size());
                writer.append("Pages fetched: " + ResponseFetcher.numInvokations);
                writer.append("Pages parsed: " + HtmlPageParser.numInvokations);
                writer.append("Forms filled in: " + MultiStateFormFillFactory.numInvokations);
                long tfinish=    System.nanoTime();
                writer.append("Time " + (tfinish-tstart));
                // close the output file
                writer.close();
            }catch(Exception ex){
                logger.fatal(ex);
                return;
            }

        }while(it.hasNext() /*&& stateChangingSpikes.size() == 0*/);
        waitForSpawnedTasks();
        System.out.print(WebAppProperties.getInstance().getUcGraph());
        if( stateChangingSpikes.size() != 0)
            logger.fatal("State-changing spikes were found!");

        WebAppProperties.getInstance().getUcGraph().writeToFile("report/usecases.dot");
        try{
            //Output the XML
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            //create string from xml tree
            // get the supporting classes for the transformer
            FileWriter writer = new FileWriter("report/report.xml");
            StreamResult result = new StreamResult(writer);
            DOMSource    source = new DOMSource(rootDocument);

            // transform the xml document into a string
            trans.transform(source, result);

            // close the output file
            writer.close();
        }catch(Exception ex){
            logger.fatal(ex);
        }
        setSuccessful(true);
    }

    public FullAnalysisTask(TaskManager t) {
        super(t);
    }
}
