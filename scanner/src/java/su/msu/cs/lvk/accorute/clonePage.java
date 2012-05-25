package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.SimpleScriptable;
import com.gargoylesoftware.htmlunit.javascript.host.EventListenersContainer;
import com.gargoylesoftware.htmlunit.javascript.host.Location;
import com.gargoylesoftware.htmlunit.javascript.host.Node;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLDocument;
import net.sourceforge.htmlunit.corejs.javascript.Function;
import net.sourceforge.htmlunit.corejs.javascript.NativeArray;
import net.sourceforge.htmlunit.corejs.javascript.NativeObject;
import net.sourceforge.htmlunit.corejs.javascript.Undefined;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.NodeList;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 14.11.2010
 * Time: 21:58:33
 * To change this template use File | Settings | File Templates.
 */
public class clonePage {
    static class DomAndAttrChangeLogger implements DomChangeListener, HtmlAttributeChangeListener{
        final String prefix;
        DomAndAttrChangeLogger(String prefix) {
            this.prefix = prefix;
        }
        public void nodeAdded(DomChangeEvent event) {
            System.out.println("[" + prefix + "] nodeAdded " + event.getChangedNode().getCanonicalXPath());
        }

        public void nodeDeleted(DomChangeEvent event) {
            System.out.println("[" + prefix + "] nodeDeleted " + event.getChangedNode().getCanonicalXPath());
        }

        public void attributeAdded(HtmlAttributeChangeEvent event) {
            System.out.println("[" + prefix + "] attributeAdded " + event.getHtmlElement().getCanonicalXPath() + " " + event.getName() + " = " + event.getValue());
        }

        public void attributeRemoved(HtmlAttributeChangeEvent event) {
            System.out.println("[" + prefix + "] attributeRemoved " + event.getHtmlElement().getCanonicalXPath() + " " + event.getName() + " = " + event.getValue());
        }

        public void attributeReplaced(HtmlAttributeChangeEvent event) {
            System.out.println("[" + prefix + "] attributeReplaced " + event.getHtmlElement().getCanonicalXPath() + " " + event.getName() + " = " + event.getValue());
        }
    }
    public static void main(String [] args){
        final String JQ_DEFAULT_HANDLER = "\n  function (a) {\n      return typeof L == \"undefined\" || !!a && L.event.triggered === a.type ? b : L.event.dispatch.apply(i.elem, arguments);\n  }\n";
        WebClient w = new WebClient(BrowserVersion.FIREFOX_3_6);
        WebClient w1 = new WebClient(BrowserVersion.FIREFOX_3_6);
        final List collectedAlerts = new ArrayList();
        w1.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
        w.setProxyConfig(new ProxyConfig("localhost", 8008));
        w1.setProxyConfig(new ProxyConfig("localhost", 8008));
        try{
            //HtmlPage origPage = w.getPage("file:///home/ngo/dev/AcCoRuTe/accorute_tests/3.html");
            HtmlPage origPage = w.getPage("http://www.twitter.com/#!/login");
            //HtmlPage origPage = w.getPage("http://bash.im");
            //HtmlPage origPage = w.getPage("http://carrotcreative.com");
            w.waitForBackgroundJavaScript(1000);
            w.setUseInsecureSSL(true);
            HtmlForm loginForm = origPage.getForms().get(0);
            loginForm.getInputByName("session[username_or_email]").setValueAttribute("mytwittest3");
            loginForm.getInputByName("session[password]").setValueAttribute("mytwittest");
            origPage = loginForm.getElementsByTagName("button").get(0).click();
            /*
            DomAndAttrChangeLogger original = new DomAndAttrChangeLogger("ORIGINAL");
            origPage.addDomChangeListener(original);
            origPage.addHtmlAttributeChangeListener(original);
            */
            String connectXpath="/html/body/div/div/div[2]/div/div/ul/li[2]/a";
            HtmlElement el = origPage.getFirstByXPath(connectXpath);
            el.click();
            w.waitForBackgroundJavaScriptStartingBefore(10000);
            HtmlPage clonedPage1 = HtmlUnitUtils.clonePage(origPage,w1.openWindow(null, "tmpWindow"));
            DomAndAttrChangeLogger clone1 = new DomAndAttrChangeLogger("CLONE 1");
            clonedPage1.addDomChangeListener(clone1);
            clonedPage1.addHtmlAttributeChangeListener(clone1);
            HtmlPage clonedPage2 = HtmlUnitUtils.clonePage(clonedPage1,w1.openWindow(null, "tmpWindow2"));
            DomAndAttrChangeLogger clone2 = new DomAndAttrChangeLogger("CLONE 2");
            clonedPage2.addDomChangeListener(clone2);
            clonedPage2.addHtmlAttributeChangeListener(clone2);
            HtmlPage clonedPage3 = HtmlUnitUtils.clonePage(clonedPage1,w1.openWindow(null, "tmpWindow3"));
            DomAndAttrChangeLogger clone3 = new DomAndAttrChangeLogger("CLONE 3");
            clonedPage3.addDomChangeListener(clone3);
            clonedPage3.addHtmlAttributeChangeListener(clone3);
            ((HtmlElement)origPage.getFirstByXPath("/html/body/div/div/div[2]/div/div/ul[3]/li/a")).click();
            System.out.println("======================");
            ((HtmlElement)clonedPage1.getFirstByXPath("/html/body/div/div/div[2]/div/div/ul[3]/li/a")).click();
            System.out.println("======================");
            ((HtmlElement)clonedPage2.getFirstByXPath("/html/body/div/div/div[2]/div/div/ul[3]/li/a")).click();
            System.out.println("======================");
            ((HtmlElement)clonedPage3.getFirstByXPath("/html/body/div/div/div[2]/div/div/ul[3]/li/a")).click();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
