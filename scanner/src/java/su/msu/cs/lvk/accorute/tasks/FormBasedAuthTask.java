package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.HttpWebConnection;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.HttpHost;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public FormBasedAuthTask(EntityID ctxID, TaskManager t, URL url, int formIndex, String submitXPath){
        super(t);
        this.ctxID = ctxID;
        this.url = url;
        this.formIndex = formIndex;
        this.submitXPath = submitXPath;
    }
    public FormBasedAuthTask(EntityID ctxID, TaskManager t, URL url, int formIndex){
        super(t);
        this.ctxID = ctxID;
        this.url = url;
        this.formIndex = formIndex;
        this.submitXPath = null;
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    protected void start() {
        setSuccessful(false);

        WebClient client = new WebClient();
        client.setJavaScriptEnabled(true);
        client.setThrowExceptionOnFailingStatusCode(false);
        client.setThrowExceptionOnScriptError(false);
        HttpHost pr = WebAppProperties.getInstance().getProxy();
        client.getProxyConfig().setProxyHost(pr.getHostName());
        client.getProxyConfig().setProxyPort(pr.getPort());
        try{
            logger.trace("Login task spawned");
            Page lPage = client.getPage(url);
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
            WebAppProperties.getInstance().getFfd().FillForm(loginForm,ctxID);
            //find the submit button and click it
            HtmlInput submitButton;
            if(submitXPath != null){
                submitButton = loginForm.getFirstByXPath(submitXPath);
                if(submitButton == null){
                    logger.error("Submit button not present");
                    return;
                }
            } else{
                submitButton = loginForm.getFirstByXPath(".//input[@type='submit']");
                if(submitButton == null){
                    submitButton = loginForm.getFirstByXPath(".//button[@type='submit']");
                }
                if(submitButton == null){
                    submitButton = loginForm.getFirstByXPath(".//input[@type='image']");
                }
                if(submitButton == null){
                    logger.error("Submit button not present");
                    return;
                }
            }
            Page p = submitButton.click();
            if(! (p instanceof HtmlPage)){
                logger.error("Auth task received not an html page");
                setSuccessful(false);
                return;
            }
            HtmlPage newPage = (HtmlPage) p;
            WebAppProperties.getInstance().getDynCredUpd().updateCredentials(
                    WebAppProperties.getInstance().getContextService().getContextByID(ctxID).getUserID(),
                    (HtmlPage)newPage
            );

            logger.trace("Login task finished successfully");
            setSuccessful(true);
        } catch (IOException e) {
            logger.error("Fatal transport error: " + e.getMessage());
            return;
        }
    }
}