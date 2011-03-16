package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import su.msu.cs.lvk.accorute.http.model.EntityID;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 18.11.2010
 * Time: 15:19:26
 * To change this template use File | Settings | File Templates.
 */
public interface FormFiller {
    boolean hasNext();
    HtmlElement next();
}
