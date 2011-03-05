package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 3/3/11
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLDiffPageEqualityDecision implements HtmlPageEqualityDecision{
    public boolean pagesEqual(HtmlPage a, HtmlPage b) {
        //TOD: this is now almost useless
        //  TODO: a.isEqualNode() - not yet implemented...
        //TODO: this task is not trivial in general case!
        Iterable<HtmlElement> ait = a.getHtmlElementDescendants();
        Iterable<HtmlElement> bit = b.getHtmlElementDescendants();
        for(HtmlElement ael: ait){
            if(!bit.iterator().hasNext())
                return false;//quantity is not equal...
            HtmlElement bel = bit.iterator().next();
            if(
                    ael.getNodeName() != bel.getNodeName() ||
                    ael.getId() != bel.getId()             ||
                    ael.getNodeValue() != bel.getNodeValue()
            ){
                return false;
            }
        }
        return true;
    }
}

