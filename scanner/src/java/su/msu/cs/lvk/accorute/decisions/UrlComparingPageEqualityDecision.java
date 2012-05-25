package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 14.05.12
 * Time: 1:17
 * To change this template use File | Settings | File Templates.
 */
public class UrlComparingPageEqualityDecision implements HtmlPageEqualityDecision{
    public boolean pagesEqual(HtmlPage a, HtmlPage b) {
        try {
            return new URI(a.getUrl().toString()).equals(new URI(b.getUrl().toString()));
        } catch (URISyntaxException e) {
        }
        return false;
    }
}
