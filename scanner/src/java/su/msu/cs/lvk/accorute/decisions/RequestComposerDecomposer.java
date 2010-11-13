package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.WebRequest;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.Request;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.04.2010
 * Time: 12:33:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class RequestComposerDecomposer {
    abstract public List<ActionParameter> decompose(Request r);
    abstract public List<ActionParameter> decompose(WebRequest r);
    abstract public Request compose(List<ActionParameter> params, WebAppUser user);
    abstract public List<ActionParameter> decomposeURL(URL u);
    public List<ActionParameter> decomposeURL(String u) throws MalformedURLException {
        return decomposeURL(new URL(u));
    }
    abstract public List<ActionParameter> decomposeCookies(String cookies) throws MalformedCookieException;
}
