package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SimpleScriptable;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 14.11.2010
 * Time: 21:58:33
 * To change this template use File | Settings | File Templates.
 */
public class clonePage {
    public static void main(String [] args){
        WebClient w = new WebClient(BrowserVersion.FIREFOX_3_6);
        WebClient w1 = new WebClient(BrowserVersion.FIREFOX_3_6);
        final List collectedAlerts = new ArrayList();
        w1.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
        w.setProxyConfig(new ProxyConfig("localhost", 8008));
        w1.setProxyConfig(new ProxyConfig("localhost", 8008));
        try{
            HtmlPage p = w.getPage("file:///home/ngo/dev/AcCoRuTe/accorute_tests/3.html");
            SimpleScriptable window = (SimpleScriptable) p.getEnclosingWindow().getScriptObject();
            SimpleScriptable w2 = window.clone();
            System.out.println(p.getDocumentElement().getScriptObject().hashCode());
            HtmlPage p3 = HtmlUnitUtils.clonePage(p,w1.getCurrentWindow(), new EntityID());
            System.out.println(p.getDocumentElement().getScriptObject().hashCode());
            System.out.println(p3.getDocumentElement().getScriptObject().hashCode());
            //p.getAnchors().get(0).click();
            System.out.println(p3.getElementsByTagName("div").size());
            System.out.println(p.getElementsByTagName("div").size());
            p3.getAnchors().get(0).click();
            System.out.println(p3.getElementsByTagName("div").size());
            System.out.println(p.getElementsByTagName("div").size());
            p.getAnchors().get(0).click();
            System.out.println(p3.getElementsByTagName("div").size());
            System.out.println(p.getElementsByTagName("div").size());
            p3.getAnchors().get(0).click();
            System.out.println(p3.getElementsByTagName("div").size());
            System.out.println(p.getElementsByTagName("div").size());
            System.out.println(collectedAlerts);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
