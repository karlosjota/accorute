package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.utils.LevenshteinDistance;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.11.2010
 * Time: 1:19:57
 * To change this template use File | Settings | File Templates.
 */
public class LevensteinPageEqualityDecision implements HtmlPageEqualityDecision{
    public boolean pagesEqual(HtmlPage a, HtmlPage b) {
        try {
            if(new URI(a.getUrl().toString()).equals(new URI(b.getUrl().toString())))
                return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        /*
        String s1 = a.asXml()+" ";
        String s2 = b.asXml()+" ";
        int distance = LevenshteinDistance.getLevenshteinDistance(s1,s2);
        int avglen = (s1.length() + s2.length())/2;
        return ((double) distance / (double)avglen) < 0.0000000005 ;
        */
        String s1 = a.asText()+" ";
        String s2 = b.asText()+" ";
        int distance = LevenshteinDistance.getLevenshteinDistance(s1,s2);
        int avglen = (s1.length() + s2.length())/2;
        return ((double) distance / (double)avglen) < 0.00001 ;
    }
}