package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.tasks.*;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.RoleCompare;

import org.w3c.dom.*;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.*;
import javax.xml.parsers.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 20.04.2010
 * Time: 19:03:11
 * To change this template use File | Settings | File Templates.
 */
public class Main{
    private static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args){
        if(args.length != 1){
            System.err.println(
                    "Usage: <program-name> <your_config.xml>, \n" +
                    "where <your_config.xml> is the filename relative to the \n" +
                    "VM working directory. \n" +
                    "If you wish to use absoulute paths, prefix them with 'file:'\n" +
                    "(C) Noseevich George, 2011\n"
            );
            return;
        }
        try {
            ApplicationContext ctx = new FileSystemXmlApplicationContext(args[0]);
        } catch (BeanDefinitionStoreException ex) {
            System.err.println("Error loading evaluation contexts: " + ex.getMessage());
            return;
        }
        JSONConfigurator configTask = new JSONConfigurator(
                WebAppProperties.getInstance().getTaskManager(),
                WebAppProperties.getInstance().getCaptureFileName()
        );
        TaskManager taskman = WebAppProperties.getInstance().getTaskManager();
        taskman.addTask(configTask);
        new Thread(taskman).start();
        taskman.waitForEmptyQueue();
        final UseCaseGraph graph = WebAppProperties.getInstance().getUcGraph();
        Iterator<UseCase> it = graph.dependencyRespectingIterator();
        List<Task> tasks = new ArrayList<Task>();
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
            EntityID ctxID1 = WebAppProperties.getInstance().getContextService().getContextsByUserID(u1.getUserID()).get(0).getContextID();
            WebAppUser u2 = WebAppProperties.getInstance().getUserService().getUsersByRole(r.getRoleName()).get(1);
            EntityID ctxID2 = WebAppProperties.getInstance().getContextService().getContextsByUserID(u2.getUserID()).get(0).getContextID();
            users1.put(r.getRoleName(),ctxID1);
            users2.put(r.getRoleName(),ctxID2);
            if(!r.getRoleName().equalsIgnoreCase("public")){
                taskman.addTask(WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(ctxID1,taskman));
                taskman.addTask(WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(ctxID2,taskman));
            }
        }
        taskman.waitForEmptyQueue();
        boolean started = false;
        Document rootDocument;
        Element rootElement;
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            rootDocument = docBuilder.newDocument();
            rootElement = rootDocument.createElement("WebApp");
            rootDocument.appendChild(rootElement);
        }  catch (Exception e) {
            logger.fatal(e);
            return;
        }
        String ucName = "start";
        int ucNum = 0;
        while(it.hasNext()){
            final Element curState = rootDocument.createElement("usecase");
            rootElement.appendChild(curState);
            if(started){
                UseCase uc = it.next();
                curState.setAttribute("name",uc.getHttpAct().getName());
                ucName = uc.getHttpAct().getName();
                curState.setAttribute("role",uc.getUserRole().getRoleName());
                uc.getHttpAct().appendToElement(curState);
                EntityID ctxID = users1.get(uc.getUserRole().getRoleName());
                HttpAction action = uc.getHttpAct();
                Task t = new HttpActionPerformerWithPrecedingActions(taskman,ctxID,action);
                taskman.addTask(t);
                taskman.waitForEmptyQueue();
            }
            started = true;
            //2. Build sitemaps
            for(Role r: WebAppProperties.getInstance().getRoles()){
                EntityID ctxID1 = users1.get(r.getRoleName());
                EntityID ctxID2 = users2.get(r.getRoleName());
                taskman.addTask(new SitemapCrawler(taskman,startAct,ctxID1));
                taskman.addTask(new SitemapCrawler(taskman,startAct,ctxID2));
            }
            taskman.waitForEmptyQueue();
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
            //3. Perform spike detection
            for(final Role attackRole: WebAppProperties.getInstance().getRoles()){
                for(final Role victimRole: WebAppProperties.getInstance().getRoles()){
                    EntityID attacker = users2.get(attackRole.getRoleName());
                    EntityID victim = users1.get(victimRole.getRoleName());
                    if(RoleCompare.less(victimRole,attackRole) >= 0){
                        final Task t = new SimpleDetectSpikes(taskman,attacker,victim);
                        t.registerCallback(
                                new Callback0(){
                                    public void CallMeBack() {
                                        Set<HttpAction> acts = (Set<HttpAction>)t.getResult();
                                        Element spikeSet = curState.getOwnerDocument().createElement("spikeset");
                                        curState.appendChild(spikeSet);
                                        spikeSet.setAttribute("from",attackRole.getRoleName());
                                        spikeSet.setAttribute("to",victimRole.getRoleName());
                                        Iterator<HttpAction> it = acts.iterator();
                                        while(it.hasNext()){
                                            HttpAction act = it.next();
                                            act.appendToElement(spikeSet);
                                        }
                                    }
                                }
                        );
                        taskman.addTask(t);
                    }
                }                                                                                                                           
            }
            taskman.waitForEmptyQueue();
            ucNum++;
        }
        WebAppProperties.getInstance().getTaskManager().terminate();
        WebAppProperties.getInstance().getTaskManager().waitForFinish();
        System.out.print(WebAppProperties.getInstance().getUcGraph());
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
            return;
        }
    }
}
