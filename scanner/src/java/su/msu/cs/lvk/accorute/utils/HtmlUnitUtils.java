package su.msu.cs.lvk.accorute.utils;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.SimpleScriptable;
import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import net.sourceforge.htmlunit.corejs.javascript.Context;
import net.sourceforge.htmlunit.corejs.javascript.VMBridge;
import org.w3c.dom.NamedNodeMap;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.tasks.ResponseFetcher;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.11.2010
 * Time: 14:29:31
 * To change this template use File | Settings | File Templates.
 */
public class HtmlUnitUtils {

    protected static Logger logger = Logger.getLogger(HtmlUnitUtils.class.getName());
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
    public static HtmlPage clonePage(final HtmlPage other,final WebWindow window, final EntityID ctx){
        try{
            WebClient wc = window.getWebClient();
            Method cl = other.getClass().getDeclaredMethod("clone",null);
            cl.setAccessible(true);
            HtmlPage p = (HtmlPage)cl.invoke(other,null);
            Method setPg = DomNode.class.getDeclaredMethod("setPage", SgmlPage.class);
            setPg.setAccessible(true);
            setPg.invoke((DomNode)p.getDocumentElement(),(SgmlPage)p);
            p.setEnclosingWindow(window);
            window.setEnclosedPage(p);
            Field webClient_Field = SgmlPage.class.getDeclaredField("webClient_");
            webClient_Field.setAccessible(true);
            webClient_Field.set(p, wc);
            for(HtmlElement element : p.getElementsByTagName("iframe")){
                HtmlInlineFrame iframe = (HtmlInlineFrame) element;
                Class[] paramClasses = {BaseFrame.class};
                Constructor ctor = FrameWindow.class.getDeclaredConstructor(paramClasses);
                ctor.setAccessible(true);
                Object[] params = {iframe};
                WebWindow win = (WebWindow)ctor.newInstance(params);
                wc.registerWebWindow(win);
                HtmlPage clone = clonePage((HtmlPage) iframe.getEnclosedWindow().getEnclosedPage(), win, ctx);
                Field enclosedWindow_Field = BaseFrame.class.getDeclaredField("enclosedWindow_");
                enclosedWindow_Field.setAccessible(true);
                enclosedWindow_Field.set(iframe, win);
            }
            if(other.getFocusedElement()!=null){
                String focusPath = other.getFocusedElement().getCanonicalXPath();
                p.setFocusedElement(p.<HtmlElement>getFirstByXPath(focusPath));
            }
            window.getWebClient().getJavaScriptEngine().getContextFactory().enterContext();
            ((SimpleScriptable) p.getDocumentElement().getScriptObject()).getWindow().initialize(window);
            return p;
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}

