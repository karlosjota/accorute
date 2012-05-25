package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpRequestFilter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.SimpleRBACRole;
import su.msu.cs.lvk.accorute.gui.*;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.tasks.*;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.Callback4;

import javax.swing.*;
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
        HttpRequestFilter filter = new HttpRequestFilter() {
            public void filter(HttpRequest httpRequest) {
                System.out.println("Request: " + httpRequest.getMethod().toString() + " " + httpRequest.getUri());
                EntityID proxyContext = WebAppProperties.getInstance().getProxyContext();
                Request req = new Request(httpRequest);
                WebAppProperties.getInstance().getConversationService().addConversationToContext(proxyContext,new Conversation(req));
            }
        };
        final HttpProxyServer server = new DefaultHttpProxyServer(8088, filter);
        server.start();

        final LogWatchComponent appender = new LogWatchComponent();
        Logger.getRootLogger().addAppender(appender);

        final TaskManager taskman = WebAppProperties.getInstance().getTaskManager();
        new Thread(taskman).start();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TaskManagerForm form = new TaskManagerForm(taskman);
                form.addTaskResultViewerFactory(new HtmlPageParserResultViewerFactory());
                form.addTaskResultViewerFactory(new ResponseFetcherResultViewerFactory());
                form.addtaskOverviewFactory(new GenericTaskVisualizerFactory());
                form.addLogAppender(appender);
            }
        });
        /*
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
        List<Role> roles = new ArrayList<Role>();
        Role role = SimpleRBACRole.createRootRole("user");
        WebAppProperties.getInstance().getRoles().add(role);
        WebAppUser u1 = new WebAppUser();
        u1.getStaticCredentials().put("log",username);
        u1.getStaticCredentials().put("password",password);
        u1.setRole(role);
        WebAppProperties.getInstance().getUserService().addOrModifyUser(u1);
        final UserContext u1Ctx = new UserContext();
        u1Ctx.setUserID(u1.getUserID());
        WebAppProperties.getInstance().getContextService().addOrUpdateContext(u1Ctx);
        HtmlPage parseRoot = null;
        if(needAuth){
            Task authTask = WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(u1Ctx.getContextID(), taskman);
            taskman.addTask(authTask);
            taskman.waitForEmptyQueue();
            parseRoot = (HtmlPage) authTask.getResult();
        }else{
            //Fetch the page
            HttpAction startAct = new HttpAction("initial",WebAppProperties.getInstance().getRcd().decomposeURL(
                    pageToParse
            ));
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
            parseRoot = pages.get(0);
        }
        final List<CorrespondentActions> outRequests = new ArrayList<CorrespondentActions>();
        final List<HtmlPage> pages = new ArrayList<HtmlPage>();
        HtmlPageParser pageParser = new HtmlPageParser(
                taskman,
                parseRoot,
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
                        pages.add(page);
                    }
                }
        );
        taskman.addTask(pageParser);
        taskman.waitForEmptyQueue();
        //To change body of implemented methods use File | Settings | File Templates.
        int i_google = 0;
    
        for(int i = 0 ; i < outRequests.size() ; i++){
            CorrespondentActions corActs = outRequests.get(i);
            ArrayList<HttpAction> httpActs = corActs.getHttpActions();
            ArrayList<DomAction> domActs = corActs.getDomActions();
            logger.info("=======ACTION======");
            logger.info(httpActs.get(0).toString());
            logger.info(domActs);
            List<ActionParameter> prms = httpActs.get(0).getActionParameters();
            for(ActionParameter param : prms){
                if(param.getName().equals("host")){
                    if(param.getValue().contains("google-analytics"))
                        i_google = i; 
                    break;
                }
            }
            if(i_google != -1)
                break;

        }
        */
        /*
        HtmlElementActionPerformer performer = new HtmlElementActionPerformer(
                taskman,
                pages.get(i_google),
                outRequests.get(i_google).getDomActions(),
                u1Ctx.getContextID(),
                new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>(){
                    public void CallMeBack(ArrayList<Conversation> param1, ArrayList<HttpAction> param2, HtmlPage param3) {
                        logger.info("=========CONVERSATIONS=======");
                        for(Conversation conv: param1){
                            logger.info(conv);
                        }
                        logger.info("==========HTTP ACTIONS=======");
                        for(HttpAction action: param2){
                            logger.info(action);
                        }
                    }
                }
        );
        taskman.addTask(performer);
        taskman.waitForFinish();
        */
    }
}
