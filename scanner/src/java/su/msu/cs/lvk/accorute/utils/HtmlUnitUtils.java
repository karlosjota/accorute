package su.msu.cs.lvk.accorute.utils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowImpl;
import com.gargoylesoftware.htmlunit.html.DomDocumentFragment;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.11.2010
 * Time: 14:29:31
 * To change this template use File | Settings | File Templates.
 */
public class HtmlUnitUtils {
    static  final WebClient wc = new WebClient();
    public static HtmlPage clonePage(HtmlPage other){
        //Dirty, dirty hack!
        WebWindowImpl w = new  WebWindowImpl(wc){
            public WebWindow getParentWindow() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            public WebWindow getTopWindow() {
                return this;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            protected boolean isJavaScriptInitializationNeeded() {
                return true;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        return clonePage(other, w);

    }
    public static HtmlPage clonePage(HtmlPage other, WebWindow w){
        HtmlPage p = new HtmlPage(other.getUrl(), other.getWebResponse(), w);
        Iterable<DomNode> children = other.getChildren();
        for(DomNode node: children){
            p.appendChild(node.cloneNode(true));
        }
        return p;
    }
}
