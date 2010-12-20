package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.http.model.ActionParameter;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.EntityID;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.12.2010
 * Time: 23:10:21
 * To change this template use File | Settings | File Templates.
 */
public interface DynamicCredentialsUpdater {
    public void updateCredentials(EntityID userID, List<ActionParameter> params);  
    public void updateCredentials(EntityID userID, HtmlPage p);
    public void updateCredentials(EntityID userID, Conversation conv);
}
