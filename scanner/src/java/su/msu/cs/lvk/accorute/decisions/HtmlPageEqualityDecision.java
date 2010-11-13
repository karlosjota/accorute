package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 10.11.2010
 * Time: 23:09:31
 * To change this template use File | Settings | File Templates.
 */
public interface HtmlPageEqualityDecision {
    public boolean pagesEqual(HtmlPage a, HtmlPage b);
}
