package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.SimpleRBACRole;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.tasks.HtmlElementActionPerformer;
import su.msu.cs.lvk.accorute.tasks.HtmlPageParser;
import su.msu.cs.lvk.accorute.tasks.ResponseFetcher;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.Callback4;
import sun.swing.CachedPainter;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 18.12.11
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class PageParserTest {
    private static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args){
        if(args.length != 2 && args.length != 4){
            System.err.println("Usage: pageParserTest context.xml http://page_to_parse [user] [password]");
            return;
        }
        ApplicationContext ctx;
        try {
            ctx = new FileSystemXmlApplicationContext(args[0]);
        } catch (BeanDefinitionStoreException ex) {
            System.err.println("Error loading evaluation contexts: " + ex.getMessage());
            return;
        }
        URL pageToParse;
        try{
            pageToParse = new URL(args[1]);
        }catch (MalformedURLException muex){
            logger.fatal("Malformed URL: " + args[1]);
            return;
        }
        String username = "user";
        String password = "password";
        boolean needAuth = false;
        if(args.length == 4){
            needAuth = true;
            username = args[2];
            password = args[3];
        }
        Role role = SimpleRBACRole.createRootRole("user");
        WebAppUser u1 = new WebAppUser();
        u1.getStaticCredentials().put("username",username);
        u1.getStaticCredentials().put("password",password);
        u1.setRole(role);
        WebAppProperties.getInstance().getUserService().addOrModifyUser(u1);
        final UserContext u1Ctx = new UserContext();
        u1Ctx.setUserID(u1.getUserID());
        WebAppProperties.getInstance().getContextService().addOrUpdateContext(u1Ctx);
        final TaskManager taskman = WebAppProperties.getInstance().getTaskManager();
        new Thread(taskman).start();
        if(needAuth){
            taskman.addTask(WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(u1Ctx.getContextID(),taskman));
            taskman.waitForEmptyQueue();
        }
        //Fetch the page
        HttpAction startAct = new HttpAction("initial",WebAppProperties.getInstance().getRcd().decomposeURL(
                pageToParse
        ));
        final List<CorrespondentActions> outRequests = new ArrayList<CorrespondentActions>();
        final ArrayList<HtmlPage> pages = new ArrayList<HtmlPage>();
        final HtmlElementActionPerformer tsk = new HtmlElementActionPerformer(
                taskman,
                startAct,
                u1Ctx.getContextID(),
                new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                    public void CallMeBack(ArrayList<Conversation> c , ArrayList<HttpAction> a, HtmlPage p){
                        pages.add(p);
                    }
                }
        );
        taskman.addTask(tsk);
        taskman.waitForEmptyQueue();
        HtmlPageParser pageParser = new HtmlPageParser(
                taskman,
                pages.get(0),
                u1Ctx.getContextID(),
                new Callback4<HtmlPage, ArrayList<DomAction>, HttpAction, Boolean>(){
                    public void CallMeBack(HtmlPage page, ArrayList<DomAction> domActions, HttpAction act, Boolean cancellable){
                        ArrayList acts = new ArrayList<HttpAction>();
                        acts.add(act);
                        CorrespondentActions actions = new CorrespondentActions(
                                acts,
                                domActions
                        );
                        outRequests.add(actions);
                    }
                }
        );
        taskman.addTask(pageParser);
        taskman.terminate();
        taskman.waitForFinish();
        //To change body of implemented methods use File | Settings | File Templates.
        for(CorrespondentActions corActs: outRequests){
            ArrayList<HttpAction> httpActs = corActs.getHttpActions();
            ArrayList<DomAction> domActs = corActs.getDomActions();
            logger.info("=======ACTION======");
            logger.info(httpActs.get(0).toString());
            logger.info(domActs);
        }

    }
}
