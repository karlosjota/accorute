package su.msu.cs.lvk.accorute.http.model;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 11.11.2010
 * Time: 17:05:36
 * To change this template use File | Settings | File Templates.
 */
public class DOMAction {
    public String getXpathElString() {
        return xpathElString;
    }

    public DOMActionType getType() {
        return type;
    }
    final private String xpathElString;
    final private DOMActionType type;

    public DOMAction(String elemXpath, DOMActionType t){
        xpathElString = elemXpath;
        type = t;
    }

    @Override
    public String toString() {
        return "{" +
                "'" + xpathElString + '\'' +
                ", " + type +
                '}';
    }
}
