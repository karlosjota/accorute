package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.WebClient;
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
            HtmlPage p = w.getPage("http://127.0.0.1/accorute_tests/JS_menu_2/demo1/index.html");
            HtmlPage p3 = HtmlUnitUtils.clonePage(p,w1.getCurrentWindow());
            p3.executeJavaScript("menu;");
        }catch(IOException e){
            System.out.println("FUCK!");
        }
    }
}
