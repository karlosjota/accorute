package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.io.IOException;

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
            HtmlPage p = w.getPage("http://127.0.0.1/accorute_tests/plainHTML/test4/3.html");
            HtmlPage p2 = p.cloneNode(true);
            HtmlPage p3 = HtmlUnitUtils.clonePage(p,w1.getCurrentWindow());
            p.getElementById("a").click();
            System.out.println(p.asXml());
            System.out.println("========");
            System.out.println(p2.asXml());
            System.out.println("========");
            System.out.println(p3.asXml());
            System.out.println("========");
            p3.getElementById("a").click();
            System.out.println(p3.asXml());
        }catch(IOException e){
            System.out.println("FUCK!");
        }
    }
}
