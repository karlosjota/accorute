package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import su.msu.cs.lvk.accorute.http.model.EntityID;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 3/15/11
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FormFillerFactory {
    FormFiller generate(HtmlForm f, EntityID ctx);
}
