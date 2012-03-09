package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import org.apache.http.HttpHost;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.decisions.FormFiller;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.11.2010
 * Time: 23:22:11
 * To change this template use File | Settings | File Templates.
 */
public class FormBasedAuthTask extends Task {
    private final EntityID ctxID;
    private final URL url;
    private final int formIndex;
    private final String submitXPath;
    private HtmlPage resultPage = null;
    final private WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
    private Collection<String> userControllableFormFields = new ArrayList<String>();
    private final ArrayList<Conversation> convs = new ArrayList<Conversation>();


    public FormBasedAuthTask(EntityID ctxID, TaskManager t, URL url, int formIndex, String submitXPath){
        super(t);
        this.ctxID = ctxID;
        this.url = url;
        this.formIndex = formIndex;
        this.submitXPath = submitXPath;
        initConn();
    }
    public FormBasedAuthTask(EntityID ctxID, TaskManager t, URL url, int formIndex){
        super(t);
        this.ctxID = ctxID;
        this.url = url;
        this.formIndex = formIndex;
        this.submitXPath = null;
        initConn();
    }
    private WebConnection initConn(){
        WebConnection falseWebC= new FalsifyingWebConnection(webClient){
            final EntityID uid = WebAppProperties.getInstance().getContextService().getContextByID(ctxID).getUserID();
            public WebResponse getResponse(WebRequest request) throws IOException {
                //TODO:not so easy!!!
                List<ActionParameter> param = WebAppProperties.getInstance().getRcd().decompose(
                        request,
                        userControllableFormFields
                );
                HttpAction act = new HttpAction("tmp", param);
                logger.trace("Will request HttpAction " + act);
                ResponseFetcher tsk = new ResponseFetcher(taskManager, act, ctxID);
                waitForTask(tsk);
                logger.trace("got responce from fetcher");
                if(tsk.isSuccessful()){
                    Conversation conv = (Conversation) tsk.getResult();
                    convs.add(conv);
                    WebAppProperties.getInstance().getDynCredUpd().updateCredentials(uid,conv);
                    return conv.getResponse().genWebResponse(conv.getRequest().getURL(),0, request);
                }
                throw new IOException();
            }
        };
        webClient.setWebConnection(falseWebC);
        return falseWebC;
    }
    @Override
    public Object getResult() {
        return resultPage;
    }

    @Override
    protected void start() {
        setSuccessful(false);
        webClient.setJavaScriptEnabled(true);
        webClient.setThrowExceptionOnFailingStatusCode(false);
        webClient.setThrowExceptionOnScriptError(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.setConfirmHandler(
                new  ConfirmHandler(){
                    public boolean handleConfirm(Page page, String message) {
                        return true;
                    }
                }
        );
        webClient.setAlertHandler(
                new AlertHandler() {
                    public void handleAlert(Page page, String message) {
                        return;
                    }
                }
        );
        webClient.setPromptHandler(
                new PromptHandler() {
                    public String handlePrompt(Page page, String message) {
                        return "test";
                    }
                }
        );
        try{
            logger.trace("Login task spawned");
            Page lPage = webClient.getPage(url);
            if(!(lPage instanceof HtmlPage)){
                logger.error("Not a html page?!");
                return;
            }
            HtmlPage loginPage = (HtmlPage) lPage;
            WebAppProperties.getInstance().getDynCredUpd().updateCredentials(
                    WebAppProperties.getInstance().getContextService().getContextByID(ctxID).getUserID(),
                    (HtmlPage)lPage
            );
            List<HtmlForm> forms = loginPage.getForms();
            if(forms.size() <= formIndex){
                logger.error("Form index out of range!");
                return;
            }
            HtmlForm loginForm = forms.get(formIndex);
            FormFiller ffiller = WebAppProperties.getInstance().getFormFillerFactory().generate(loginForm, ctxID);

            HtmlElement submitButton = null;
            //find the submit button and click it
            if(submitXPath != null){
                submitButton = loginForm.getFirstByXPath(submitXPath);
                if(submitButton == null){
                    logger.error("Submit button not present");
                    return;
                }
            }else{
                try{
                    submitButton = ffiller.next();//fill in the submit
                }catch (NoSuchElementException ex){
                    logger.error("The login form has no submit buttons ?!");
                }
            }
            Page p = submitButton.click();
            if(! (p instanceof HtmlPage)){
                logger.error("Auth task received not an html page");
                setSuccessful(false);
                return;
            }
            webClient.waitForBackgroundJavaScript(12000);
            HtmlPage newPage = (HtmlPage) p;
            resultPage = newPage;
            logger.trace("Login task finished successfully");
            setSuccessful(true);


        } catch (IOException e) {
            logger.error("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
}