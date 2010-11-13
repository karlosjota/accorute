package su.msu.cs.lvk.accorute.utils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
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
    public static HtmlPage clonePage(HtmlPage other, WebWindow w){
        HtmlPage p = new HtmlPage(other.getUrl(), other.getWebResponse(), w);
        DomNode n = other.cloneNode(true);
        Iterable<DomNode> children = n.getChildren();
        for(DomNode node: children){
            p.appendChild(node);            
        }
        return p;
    }
}
