package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.HttpWebConnection;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.HttpHost;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.CookieDescriptor;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.UserContext;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;
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
            List<HtmlForm> forms = loginPage.getForms();
            if(forms.size() <= formIndex){
                logger.error("Form index out of range!");
                return;
            }
            HtmlForm loginForm = forms.get(formIndex);
            WebAppProperties.getInstance().getFfd().FillForm(loginForm,ctxID);
            //find the submit button and click it
            HtmlInput submitButton = loginForm.getFirstByXPath(submitXPath);
            if(submitButton == null){
                logger.error("Submit button not present");
                return;
            }
            Page p = submitButton.click();
            if(! (p instanceof HtmlPage)){
                logger.error("Auth task received not an html page");
                setSuccessful(false);
                return;
            }
            HtmlPage newPage = (HtmlPage) p;
            UserContext contx = WebAppProperties.getInstance().getContextService().getContextByID(ctxID);
            WebAppProperties.getInstance().getDynCredUpd().updateCredentials(contx.getUserID(),newPage);
            Set<com.gargoylesoftware.htmlunit.util.Cookie> cooks = client.getCookieManager().getCookies();
            //TODO: Use dynamic cred updater for cookie updates here!
            URL u = loginPage.getUrl();
            CookieOrigin origin = new CookieOrigin(u.getHost(), u.getPort(),u.getPath(),false); // TODO: false here
            List<Cookie> cookies = new ArrayList<Cookie>();
            for(com.gargoylesoftware.htmlunit.util.Cookie cook: cooks){
                cookies.add(new BasicClientCookie(cook.getName(), cook.getValue()));
            }
            CookieDescriptor desc = new CookieDescriptor(cookies,origin,ctxID);
            WebAppProperties.getInstance().getCookieService().setCookies(desc);
            //Update dynamic credentials
            EntityID id = WebAppProperties.getInstance().getContextService().getContextByID(ctxID).getUserID();
            WebAppUser user = WebAppProperties.getInstance().getUserService().getUserByID(id);
            for(Cookie c: desc.getCookies()){
                user.getDynamicCredentials().put(c.getName(),c.getValue());
                logger.trace("Added cookie: " + c.getName() + ":" + c.getValue());
            }
            logger.trace("Login task finished successfully");
            setSuccessful(true);
        } catch (IOException e) {
            logger.error("Fatal transport error: " + e.getMessage());
            return;
        }
    }
}