package su.msu.cs.lvk.accorute.http.model;

import org.apache.log4j.Logger;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 10.04.2010
 * Time: 23:37:29
 * To change this template use File | Settings | File Templates.
 */
public class ActionParameter extends NamedValue {
    private static Logger logger = Logger.getLogger(NamedValue.class.getName());
    private ActionParameterLocation location;
    private ActionParameterMeaning meaning;
    private ActionParameterDatatype datatype;

    @Override
    public String toString() {
        return super.toString();
    }

    public boolean isDefined() {
        return defined;
    }

    public void setDefined(boolean defined) {
        this.defined = defined;
    }

    private boolean defined = false;

    public ActionParameter(String pname, String pvalue,
                           ActionParameterLocation loc,
                           ActionParameterMeaning mean,
                           ActionParameterDatatype type
                           ){
        super(pname,pvalue);
        location = loc;
        meaning = mean;
        datatype = type;
    }
    public ActionParameterLocation getLocation() {
        return location;
    }

    public ActionParameterMeaning getMeaning() {
        return meaning;
    }

    public ActionParameterDatatype getDatatype() {
        return datatype;
    }

}
