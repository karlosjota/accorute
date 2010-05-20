/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.04.2010
 * Time: 18:46:57
 * To change this template use File | Settings | File Templates.
 */
package su.msu.cs.lvk.accorute;

import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.SimpleRBACRole;
import su.msu.cs.lvk.accorute.decisions.ParameterValueDecision;
import su.msu.cs.lvk.accorute.decisions.RequestComposerDecomposer;
import su.msu.cs.lvk.accorute.http.model.Action;
import su.msu.cs.lvk.accorute.http.model.TestChain;
import su.msu.cs.lvk.accorute.storage.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class WebAppProperties {
    private static WebAppProperties ourInstance = new WebAppProperties();

    private final List<Pattern> scope = new ArrayList<Pattern>();

    private URL startPage = null;
    private TestChain testChain;
    private final Collection<Action> stateChangingActions = new ArrayList<Action>();
    private ParameterValueDecision pvd;
    private RequestComposerDecomposer rcd;
    private CookieService cookieService;
    private ActionService actionService;
    private ConversationService conversationService;
    private SitemapService sitemapService;
    private UserService userService;
    private ContextService contextService;
    private TaskManager taskManager;

    public URL getStartPage() {
        return startPage;
    }

    public void setStartPage(URL startPage) {
        this.startPage = startPage;
    }

    public TestChain getTestChain() {
        return testChain;
    }

    public void setTestChain(TestChain testChain) {
        this.testChain = testChain;
    }

    public ParameterValueDecision getPvd() {
        return pvd;
    }

    public void setPvd(ParameterValueDecision pvd) {
        this.pvd = pvd;
    }

    public RequestComposerDecomposer getRcd() {
        return rcd;
    }

    public void setRcd(RequestComposerDecomposer rcd) {
        this.rcd = rcd;
    }

    public CookieService getCookieService() {
        return cookieService;
    }

    public void setCookieService(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    public ConversationService getConversationService() {
        return conversationService;
    }

    public void setConversationService(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    public SitemapService getSitemapService() {
        return sitemapService;
    }

    public void setSitemapService(SitemapService sitemapService) {
        this.sitemapService = sitemapService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ContextService getContextService() {
        return contextService;
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }
    public void addStateChangingAction(Action act){
        stateChangingActions.add(act);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    private List<Role> roles;//TODO: make a dao service for that or handle it in some other neat way....

    
    public static WebAppProperties getInstance() {
        return ourInstance;
    }

    private WebAppProperties() {
    }
}
