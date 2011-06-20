package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.utils.LevenshteinDistance;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.11.2010
 * Time: 1:19:57
 * To change this template use File | Settings | File Templates.
 */
public class LevensteinPageEqualityDecision implements HtmlPageEqualityDecision{
    public boolean pagesEqual(HtmlPage a, HtmlPage b) {
        String s1 = a.asXml()+" ";
        String s2 = b.asXml()+" ";
        int distance = LevenshteinDistance.getLevenshteinDistance(s1,s2);
        int avglen = (s1.length() + s2.length())/2;
        return ((double) distance / (double)avglen) < 0.0000000005 ;
    }
}