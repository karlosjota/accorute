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
            Method cl = other.getClass().getDeclaredMethod("clone",null);
            cl.setAccessible(true);
            HtmlPage p = (HtmlPage)cl.invoke(other,null);
            p.setEnclosingWindow(window);
            window.setEnclosedPage(p);
            Field field = SgmlPage.class.getDeclaredField("webClient_");
            field.setAccessible(true);
            field.set(p, window.getWebClient());
            if(other.getFocusedElement()!=null){
                String focusPath = other.getFocusedElement().getCanonicalXPath();
                p.setFocusedElement(p.<HtmlElement>getFirstByXPath(focusPath));
            }
            Method setPg = DomNode.class.getDeclaredMethod("setPage", SgmlPage.class);
            setPg.setAccessible(true);
            setPg.invoke((DomNode)p,(SgmlPage)p);
            window.getWebClient().getJavaScriptEngine().getContextFactory().enterContext();
            ((SimpleScriptable) p.getDocumentElement().getScriptObject()).getWindow().initialize(window);
            return p;
        }catch(Exception ex){
            logger.warn("Failed to clone page using introspection ", ex);
            try{
                WebClient webClient = window.getWebClient();
                WebConnection oldWebConnection = webClient.getWebConnection();
                WebConnection falseWebC= new FalsifyingWebConnection(webClient){
                    final UserContext contx = WebAppProperties.getInstance().getContextService().getContextByID(ctx);
                    public WebResponse getResponse(WebRequest request) throws IOException {
                        List<ActionParameter> param = WebAppProperties.getInstance().getRcd().decompose(
                                request
                        );
                        HttpAction act = new HttpAction("tmp", param);
                        logger.trace("Intercepted action on page clone: \n" + act);
                        Request req = WebAppProperties.getInstance().getRcd().compose(param, WebAppProperties.getInstance().getContextService().getContextByID(ctx));
                        Collection<Conversation> convs = WebAppProperties.getInstance().getConversationService().getContextConversations(ctx);
                        Conversation res = null;
                        for(Conversation conv: convs){
                            //WebAppProperties.getInstance().getRcd().
                            List<ActionParameter> paramConv = WebAppProperties.getInstance().getRcd().decompose(
                                    conv.getRequest().genWebRequest()
                            );
                            HttpAction actConv = new HttpAction("tmp", paramConv);
                            if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(act, actConv)){
                                res = conv;
                                break;
                            }
                        }
                        if(res == null){
                            throw new IOException("Shit request on page cloning!!!");
                        }
                        return res.getResponse().genWebResponse(res.getRequest().getURL(),0, request);
                    }
                };
                webClient.setWebConnection(falseWebC);
                PageCreator creator = window.getWebClient().getPageCreator();
                HtmlPage p = (HtmlPage) creator.createPage(other.getWebResponse(),window);
                webClient.setWebConnection(oldWebConnection);
                return p;
            }catch(Exception ex2){
                logger.warn("Failed to clone page using request replay ", ex);
                HtmlPage p = new HtmlPage(other.getUrl(), other.getWebResponse(), window);
                p.setEnclosingWindow(window);
                window.setEnclosedPage(p);
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
}

