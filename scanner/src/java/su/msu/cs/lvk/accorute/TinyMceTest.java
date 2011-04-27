package su.msu.cs.lvk.accorute;

/**
 *
 * @author trey
 */
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 4/27/11
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class TinyMceTest {
    public static void main( String args[]) throws Exception{
     WebClient wb = new WebClient(BrowserVersion.FIREFOX_3_6, "localhost", 8008);
        wb.getCache().setMaxSize(0);

        HtmlPage p = (HtmlPage) wb.getPage("http://10.0.0.13:8080/easy_jsp_forum/index.jsp");
        HtmlForm f = p.getForms().get(0);
        f.getInputByName("username").setValueAttribute("admin1");
        f.getInputByName("password").setValueAttribute("admin1");
        HtmlPage newP = f.getInputByName("login").click();
        HtmlPage newP2 = newP.getAnchorByHref("index.jsp?page=forum&boxid=1925").click();
        HtmlElement inp = newP2.getElementsByTagName("input").get(0);
        HtmlPage newP3 = inp.click();
        System.out.println(newP3.asXml());

    }
}
