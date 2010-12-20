package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.tasks.*;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.RoleCompare;

import java.util.*;

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
        ApplicationContext ctx;
        try {
            ctx = new FileSystemXmlApplicationContext("src/resources/accorute-config-django-test-1.xml");
        } catch (BeanDefinitionStoreException ex) {
            System.err.println("Error loading evaluation contexts: " + ex.getMessage());
            return;
        }
        JSONConfigurator configTask = new JSONConfigurator(WebAppProperties.getInstance().getTaskManager(),
                "src/resources/django-test1-light.txt");
        TaskManager taskman = WebAppProperties.getInstance().getTaskManager();
        taskman.addTask(configTask);
        new Thread(taskman).start();
        taskman.waitForEmptyQueue();
        final UseCaseGraph graph = WebAppProperties.getInstance().getUcGraph();
        Iterator<UseCase> it = graph.dependencyRespectingIterator();
        List<Task> tasks = new ArrayList<Task>();
        HttpAction startAct;
        try{

            //"http://127.0.0.1/accorute_tests/JS_menu_2/demo1/index.html"
            //http://127.0.0.1/accorute_tests/plainHTML/test4/1.html
            //http://127.0.0.1:8080/accounts/login/?next=/test1/
            startAct = new HttpAction("initial",WebAppProperties.getInstance().getRcd().decomposeURL("http://127.0.0.1:8000/test1/"));
        }catch(Throwable r){
            return;
        }
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
        while(it.hasNext()){
            if(started){
                UseCase uc = it.next();
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
            //3. Perform spike detection
            for(Role r1: WebAppProperties.getInstance().getRoles()){
                for(Role r2: WebAppProperties.getInstance().getRoles()){
                    EntityID ctxID1 = users1.get(r2.getRoleName());
                    EntityID ctxID2 = users2.get(r1.getRoleName());
                    if(RoleCompare.less(r2,r1) >= 0)
                        taskman.addTask(new SimpleDetectSpikes(taskman,ctxID1,ctxID2));
                }                                                                                                                           
            }
            taskman.waitForEmptyQueue();
        }
        WebAppProperties.getInstance().getTaskManager().terminate();
        WebAppProperties.getInstance().getTaskManager().waitForFinish();
        if(!configTask.isSuccessful()){
            logger.trace("smth went wrong!");
        }      
        System.out.print(WebAppProperties.getInstance().getUcGraph());
        /*TestChain testChain = WebAppProperties.getInstance().getTestChain();
        for(int i=0; i < testChain.size(); i++){
            logger.trace("UC: " + testChain.get(i).action + " by role " +testChain.get(i).user.getUserRole().getRoleName());
            logger.trace("\n==========\n" + WebAppProperties.getInstance().getRcd().compose(
                    testChain.get(i).action.getActionParameters(),
                    testChain.get(i).user).toString()+ "\n==========\n"
            );
        } */
    }
}
