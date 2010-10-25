package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.Action;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.Response;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback1;

import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 22.10.2010
 * Time: 14:52:55
 * To change this template use File | Settings | File Templates.
 */
public class ResponseParser extends Task {
    private final Conversation conversation;
    private final Callback1<Action> callback;
    private final WebClient webClient = new WebClient();

    public ResponseParser(TaskManager t, Conversation c,  Callback1<Action> cb) {
        super(t);
        conversation = c;
        callback = cb;
        //create WebConnectionWrapper that calls back the callback on each request
        new FalsifyingWebConnection(webClient) {
            public WebResponse getResponse(WebRequest request) throws IOException {
                //TODO: meaningful action name
                Action act = new Action("no name",WebAppProperties.getInstance().getRcd().decompose(request));
                callback.CallMeBack(act);//offload this action bach to the farm
                return createWebResponse(request,"","text/plain");
            }
        };
    }

    @Override
    public Object getResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void start() {
        URL origUrl = conversation.getRequest().getURL();
        //1. create WebResponse from conversation : WebResponse(WebResponseData responseData, WebRequest request, long loadTime)
        WebResponse resp = conversation.getResponse().genWebResponse(origUrl);
        //2. create htmlpage : HtmlPage(URL originatingUrl, WebResponse webResponse, WebWindow webWindow)
        HtmlPage thisPage = new HtmlPage(origUrl, resp, webClient.getCurrentWindow());
        //3. do parsing of the page
        //Here will follow a huuuge copypaste from petand's httptools 
    }
}
