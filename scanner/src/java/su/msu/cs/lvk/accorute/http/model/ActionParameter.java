package su.msu.cs.lvk.accorute.http.model;

import org.apache.log4j.Logger;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterRole;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 10.04.2010
 * Time: 23:37:29
 * To change this template use File | Settings | File Templates.
 */
public class ActionParameter extends NamedValue {
    private static Logger logger = Logger.getLogger(NamedValue.class.getName());
    private final ActionParameterLocation location;
    private final ActionParameterMeaning meaning;
    private final ActionParameterDatatype datatype;
    private final ActionParameterRole role;

    @Override
    public String toString() {
        return super.toString();
    }

    public boolean isDefined() {
        return defined;
    }
    public ActionParameterRole getRole() {
        return role;
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
        role = ActionParameterRole.UNKNOWN;
    }

    public ActionParameter(String pname, String pvalue,
                           ActionParameterLocation loc,
                           ActionParameterMeaning mean,
                           ActionParameterDatatype type,
                           ActionParameterRole paramRole
                           ){
        super(pname,pvalue);
        location = loc;
        meaning = mean;
        datatype = type;
        role = paramRole;
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
