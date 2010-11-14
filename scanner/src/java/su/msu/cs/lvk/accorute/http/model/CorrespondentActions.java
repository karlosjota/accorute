package su.msu.cs.lvk.accorute.http.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 14.11.2010
 * Time: 21:11:41
 * To change this template use File | Settings | File Templates.
 */
public class CorrespondentActions {
    private final HTTPAction httpAction;
    private final ArrayList<DOMAction> domActions;

    public ArrayList<DOMAction> getDomActions() {
        return domActions;
    }

    public CorrespondentActions(HTTPAction h, ArrayList<DOMAction> d){
        httpAction = h;
        domActions = d;
    }

    public HTTPAction getHttpAction() {
        return httpAction;
    }
}
