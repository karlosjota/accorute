package su.msu.cs.lvk.accorute.utils;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.dom.NamedNodeMap;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.11.2010
 * Time: 14:29:31
 * To change this template use File | Settings | File Templates.
 */
public class HtmlUnitUtils {
    public static Collection<String> getUserControllableFormFields(HtmlForm form){
        Set<String> controllableFieldNames = new HashSet<String>();
        //1. input
        for(HtmlElement i: form.getHtmlElementsByTagName("input")){
            HtmlInput input = (HtmlInput) i;
            final String type = input.getTypeAttribute();
            //1.1 type=text
            if(type.equalsIgnoreCase("text")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }
            //1.2 type=password
            else if(type.equalsIgnoreCase("password")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }
            //1.3 type=radio
            else if(type.equalsIgnoreCase("radio")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }
            //1.4 type=checkbox
            else if(type.equalsIgnoreCase("checkbox")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }
            /*
            else if(type.equalsIgnoreCase("hidden")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }*/
        }

        //selects
        for(HtmlElement i: form.getHtmlElementsByTagName("select")){
            HtmlSelect sel = (HtmlSelect) i;
            String name = sel.getNameAttribute();
            controllableFieldNames.add(name);
        }
        //textarea
        for(HtmlElement el: form.getHtmlElementsByTagName("textarea")){
            HtmlTextArea area = (HtmlTextArea) el;
            String name = area.getNameAttribute();
            controllableFieldNames.add(name);
        }
        return controllableFieldNames;
    }
    public static HtmlPage clonePage(HtmlPage other, WebWindow w){
        try{
            Method cl = other.getClass().getDeclaredMethod("clone",null);
            cl.setAccessible(true);
            HtmlPage p = (HtmlPage)cl.invoke(other,null);
            return p;
        }catch(Exception ex){
            HtmlPage p = new HtmlPage(other.getUrl(), other.getWebResponse(), w);
            p.setEnclosingWindow(w);
            w.setEnclosedPage(p);
            DomNode childNode = other.getFirstChild();
            while(childNode != null){
                DomNode newContents = childNode.cloneNode(true);
                p.appendChild(newContents);
                childNode = childNode.getNextSibling();
            }
            List<HtmlElement> scripts = p.getElementsByTagName("script");
            p.getDocumentElement();
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
            List<HtmlElement> cssLinks = p.getElementsByTagName("link");
            for(HtmlElement l: cssLinks){
                HtmlLink link = (HtmlLink)l;
                DomNode n = link.getParentNode();
                HtmlLink newLink = (HtmlLink) p.createElement("link");
                NamedNodeMap map =  link.getAttributes();
                for(int i=0;i<map.getLength();i++){
                    newLink.setAttribute(map.item(i).getNodeName(),map.item(i).getNodeValue());
                }
                n.removeChild(link);
                n.appendChild(newLink);

            }
            /*Map<Object, Object> repl = new HashMap<Object, Object>();
            repl.put(other.getEnclosingWindow(), w);
            Window newWin = (Window)  SerialClone.clone(((Window)other.getEnclosingWindow().getScriptObject()),repl);
            w.setScriptObject(newWin);*/
            if(other.getFocusedElement()!=null){
                String focusPath = other.getFocusedElement().getCanonicalXPath();
                p.setFocusedElement(p.<HtmlElement>getFirstByXPath(focusPath));
            }
            return p;
        }
    }
}

