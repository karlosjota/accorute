package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 14.11.2010
 * Time: 21:58:33
 * To change this template use File | Settings | File Templates.
 */
public class clonePage {
    public static void main(String [] args){
        WebClient w = new WebClient();
        WebClient w1 = new WebClient();
        try{
            HtmlPage p = w.getPage("http://twitter.com");
            System.out.println(p.getDocumentElement().getScriptObject().hashCode());
            Date now = new Date();
            System.out.println(now);
            HtmlPage p3 = HtmlUnitUtils.clonePage(p,w1.getCurrentWindow(), new EntityID());
            Date later = new Date();
            System.out.println(later);
            System.out.println(p.getDocumentElement().getScriptObject().hashCode());
            System.out.println(p3.getDocumentElement().getScriptObject().hashCode());
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
