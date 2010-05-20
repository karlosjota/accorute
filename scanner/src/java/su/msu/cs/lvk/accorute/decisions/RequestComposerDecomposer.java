package su.msu.cs.lvk.accorute.decisions;

import org.apache.commons.httpclient.cookie.MalformedCookieException;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.Request;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.04.2010
 * Time: 12:33:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class RequestComposerDecomposer {
    public abstract List<ActionParameter> decompose(Request r);
    public abstract Request compose(List<ActionParameter> params, WebAppUser user);
    public abstract List<ActionParameter> decomposeURL(String u) throws MalformedURLException;
    public abstract List<ActionParameter> decomposeCookies(String cookies) throws MalformedCookieException;
}
