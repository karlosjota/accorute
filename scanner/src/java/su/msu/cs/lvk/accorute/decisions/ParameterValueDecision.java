package su.msu.cs.lvk.accorute.decisions;

import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.UserContext;

import java.net.URL;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.04.2010
 * Time: 12:29:00
 * To change this template use File | Settings | File Templates.
 */
public interface ParameterValueDecision {
    public  void resolve(List<ActionParameter> params, UserContext ctx, URL url);

}
