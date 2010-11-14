package su.msu.cs.lvk.accorute.utils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.WebWindowImpl;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.SimpleScriptable;
import com.gargoylesoftware.htmlunit.javascript.host.Window;
import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
        DomNode newContents = other.getFirstChild().cloneNode(true);
        p.appendChild(newContents);
        w.setEnclosedPage(p);
        p.setEnclosingWindow(w);
        List<HtmlElement> scripts = p.getElementsByTagName("script");
        for(HtmlElement sc: scripts){
            HtmlScript script = (HtmlScript)sc;
            DomNode n = script.getParentNode();
            HtmlScript newScript = (HtmlScript) p.createElement("script");
            NamedNodeMap map =  script.getAttributes();
            for(int i=0;i<map.getLength();i++){
                newScript.setAttribute(map.item(i).getNodeName(),map.item(i).getNodeValue());
            }
            newScript.setTextContent(script.getTextContent());
            newScript.setNodeValue(script.getNodeValue());
            n.removeChild(script);
            n.appendChild(newScript);
        }
        if(other.getFocusedElement()!=null){
            String focusPath = other.getFocusedElement().getCanonicalXPath();
            p.setFocusedElement(p.<HtmlElement>getFirstByXPath(focusPath));
        }
        return p;
    }
}

