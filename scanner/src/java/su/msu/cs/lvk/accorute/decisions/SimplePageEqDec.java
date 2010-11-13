package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.11.2010
 * Time: 1:19:57
 * To change this template use File | Settings | File Templates.
 */
public class SimplePageEqDec implements HtmlPageEqualityDecision{
    public boolean pagesEqual(HtmlPage a, HtmlPage b) {
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
