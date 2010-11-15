package su.msu.cs.lvk.accorute.http.model;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 14.11.2010
 * Time: 21:11:41
 * To change this template use File | Settings | File Templates.
 */
public class CorrespondentActions {
    private final ArrayList<HttpAction> httpActions;
    private final ArrayList<DomAction> domActions;

    public ArrayList<DomAction> getDomActions() {
        return domActions;
    }

    public CorrespondentActions(ArrayList<HttpAction> h, ArrayList<DomAction> d){
        httpActions = h;
        domActions = d;
    }

    public ArrayList<HttpAction> getHttpActions() {
        return httpActions;
    }
}
