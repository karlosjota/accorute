package su.msu.cs.lvk.accorute;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpRequestFilter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import su.msu.cs.lvk.accorute.gui.*;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.Request;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 09.05.12
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
public class GuiMain {
    public static void main(String[] args){
        if(args.length != 1){
            System.err.println("Usage: pageParserTest context.xml");
            return;
        }
        //1. Load evaluation context
        ApplicationContext ctx;
        try {
            ctx = new FileSystemXmlApplicationContext(args[0]);
        } catch (BeanDefinitionStoreException ex) {
            System.err.println("Error loading evaluation contexts: " + ex.getMessage());
            return;
        }
        //2. Start intercepting proxy
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
        //3. Initialize GUI logging
        final LogWatchComponent appender = new LogWatchComponent();
        Logger.getRootLogger().addAppender(appender);
        //4. create and start the TaskManager
        final TaskManager taskman = WebAppProperties.getInstance().getTaskManager();
        new Thread(taskman).start();
        //5. Create main window
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TaskManagerForm form = new TaskManagerForm(taskman);
                form.addTaskResultViewerFactory(new HtmlPageParserResultViewerFactory());
                form.addTaskResultViewerFactory(new ResponseFetcherResultViewerFactory());
                form.addTaskResultViewerFactory(new SitemapCrawlerResultViewerFactory());
                form.addtaskOverviewFactory(new GenericTaskVisualizerFactory());
                form.addLogAppender(appender);
            }
        });
    }
}
