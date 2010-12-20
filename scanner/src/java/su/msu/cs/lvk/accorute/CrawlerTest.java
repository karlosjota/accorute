package su.msu.cs.lvk.accorute;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.SimpleRBACRole;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.JoinTask;
import su.msu.cs.lvk.accorute.taskmanager.SerialAdapter;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.tasks.SimpleDetectSpikes;
import su.msu.cs.lvk.accorute.tasks.SitemapCrawler;

import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 10.11.2010
 * Time: 1:28:26
 * To change this template use File | Settings | File Templates.
 */
public class CrawlerTest {
    private static Logger logger;
    public static void main(String[] args){
        ApplicationContext ctx;
        try {
            ctx = new FileSystemXmlApplicationContext("src/resources/accorute-config-django-test-1.xml");
        } catch (BeanDefinitionStoreException ex) {
            System.err.println("Error loading evaluation contexts: " + ex.getMessage());
            return;
        }
        logger = Logger.getLogger(Main.class.getName());
        Role role = SimpleRBACRole.createRootRole("user");
        WebAppUser u1 = new WebAppUser();
        u1.getStaticCredentials().put("username","user1");
        u1.getStaticCredentials().put("password","user1");
        u1.setRole(role);
        WebAppProperties.getInstance().getUserService().addOrModifyUser(u1);
        UserContext u1Ctx = new UserContext();
        u1Ctx.setUserID(u1.getUserID());
        WebAppProperties.getInstance().getContextService().addOrUpdateContext(u1Ctx);

        WebAppUser u2 = new WebAppUser();
        u2.getStaticCredentials().put("username","user2");
        u2.getStaticCredentials().put("password","user2");
        u2.setRole(role);
        WebAppProperties.getInstance().getUserService().addOrModifyUser(u2);
        UserContext u2Ctx = new UserContext();
        u2Ctx.setUserID(u2.getUserID());
        WebAppProperties.getInstance().getContextService().addOrUpdateContext(u2Ctx);

        HttpAction act;
        try{
            //"http://127.0.0.1/accorute_tests/JS_menu_2/demo1/index.html"
            //http://127.0.0.1/accorute_tests/plainHTML/test4/1.html
            //http://127.0.0.1:8080/accounts/login/?next=/test1/
            act = new HttpAction("initial",WebAppProperties.getInstance().getRcd().decomposeURL("http://127.0.0.1:8000/test1/"));
        }catch(Throwable r){
            return;
        }
        TaskManager taskman = WebAppProperties.getInstance().getTaskManager();
        Task authTask1 = WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(u1Ctx.getContextID(),taskman);
        Task authTask2 = WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(u2Ctx.getContextID(),taskman);
        SitemapCrawler crawler1 = new SitemapCrawler(taskman,act,u1Ctx.getContextID());
        SitemapCrawler crawler2 = new SitemapCrawler(taskman,act,u2Ctx.getContextID());
        SerialAdapter combo1 = new SerialAdapter(taskman,false,false,authTask1,crawler1);
        SerialAdapter combo2 = new SerialAdapter(taskman,false,false,authTask2,crawler2);
        SimpleDetectSpikes detector1 = new SimpleDetectSpikes(taskman,u1Ctx.getContextID(),u2Ctx.getContextID());
        SimpleDetectSpikes detector2 = new SimpleDetectSpikes(taskman,u2Ctx.getContextID(),u1Ctx.getContextID());
        taskman.addTask(combo1);
        taskman.addTask(combo2);
        taskman.addTask(new JoinTask(taskman));
        taskman.addTask(detector1);
        taskman.addTask(detector2);
        new Thread(WebAppProperties.getInstance().getTaskManager()).start();
        WebAppProperties.getInstance().getTaskManager().terminate();
        WebAppProperties.getInstance().getTaskManager().waitForFinish();
        Sitemap map1 = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(u1Ctx.getContextID());
        Sitemap map2 = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(u2Ctx.getContextID());
        Set<HttpAction>  spike12 = (Set<HttpAction> ) detector1.getResult();
        Set<HttpAction>  spike21 = (Set<HttpAction> ) detector2.getResult();
        logger.fatal("Map1 is " + map1);
        logger.fatal("Map2 is " + map2);
        
        logger.fatal("u1-> u2 " + spike12);
        logger.fatal("u2-> u1 " + spike21);
    }
}
