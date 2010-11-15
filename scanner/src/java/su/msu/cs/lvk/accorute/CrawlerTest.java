package su.msu.cs.lvk.accorute;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.tasks.SitemapCrawler;

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
            ctx = new FileSystemXmlApplicationContext("src/resources/accorute-config-crawler-test.xml");
        } catch (BeanDefinitionStoreException ex) {
            System.err.println("Error loading evaluation contexts: " + ex.getMessage());
            return;
        }
        logger = Logger.getLogger(Main.class.getName());
        WebAppUser u = new WebAppUser();
        WebAppProperties.getInstance().getUserService().addOrModifyUser(u);
        UserContext dummyCtx = new UserContext();
        dummyCtx.setUserID(u.getUserID());
        WebAppProperties.getInstance().getContextService().addOrUpdateContext(dummyCtx);
        HttpAction act;
        try{
            //"http://127.0.0.1/accorute_tests/JS_menu_2/demo1/index.html"
            act = new HttpAction("initial",WebAppProperties.getInstance().getRcd().decomposeURL("http://127.0.0.1/accorute_tests/plainHTML/test4/1.html"));
        }catch(Throwable r){
            return;
        }
        SitemapCrawler crawler = new SitemapCrawler(WebAppProperties.getInstance().getTaskManager(),act,dummyCtx.getContextID());
        WebAppProperties.getInstance().getTaskManager().addTask(crawler);
        new Thread(WebAppProperties.getInstance().getTaskManager()).start();
        while(crawler.getStatus() != Task.TaskStatus.FINISHED){
            ;//Spin lock =)
        }
        WebAppProperties.getInstance().getTaskManager().terminate();
        Sitemap map = WebAppProperties.getInstance().getSitemapService().getSitemapForContext(dummyCtx.getContextID());
        logger.fatal("Map is " + map);
    }
}
