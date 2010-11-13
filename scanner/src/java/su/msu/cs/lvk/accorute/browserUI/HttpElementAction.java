package su.msu.cs.lvk.accorute.browserUI;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 11.11.2010
 * Time: 17:05:36
 * To change this template use File | Settings | File Templates.
 */
public class HttpElementAction {
    public String getXpathElString() {
        return xpathElString;
    }

    public HttpElementActionType getType() {
        return type;
    }
    final private String xpathElString;
    final private HttpElementActionType type;

    public HttpElementAction(String elemXpath, HttpElementActionType t){
        xpathElString = elemXpath;
        type = t;
    }
}
