/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.04.2010
 * Time: 18:46:57
 * To change this template use File | Settings | File Templates.
 */
package su.msu.cs.lvk.accorute;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.AbstractHttpClient;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.decisions.*;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.model.HttpAction;
import su.msu.cs.lvk.accorute.http.model.TestChain;
import su.msu.cs.lvk.accorute.http.model.UseCaseGraph;
import su.msu.cs.lvk.accorute.storage.*;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.tasks.ContextedTaskFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class WebAppProperties {
    private static WebAppProperties ourInstance = new WebAppProperties();
    private DynamicCredentialsUpdater dynCredUpd;

    public DynamicCredentialsUpdater getDynCredUpd() {
        return dynCredUpd;
    }

    public void setDynCredUpd(DynamicCredentialsUpdater dynCredUpd) {
        this.dynCredUpd = dynCredUpd;
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public void setProxy(HttpHost proxy) {
        this.proxy = proxy;
    }

    private HttpHost proxy;
    public AbstractHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(AbstractHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private AbstractHttpClient httpClient;


    public Pattern getScope() {
        return scope;
    }

    public void setScope(Pattern scope) {
        this.scope = scope;
    }

    private Pattern scope;

    private List<String> dynamicTokenNames = new ArrayList<String>();
    private List<ActionParameterLocation> dynamicTokenLocations = new ArrayList<ActionParameterLocation>();

    public ContextedTaskFactory getAuthTaskFactory() {
        return authTaskFactory;
    }

    public void setAuthTaskFactory(ContextedTaskFactory authTaskFactory) {
        this.authTaskFactory = authTaskFactory;
    }

    private ContextedTaskFactory authTaskFactory;

    private URL startPage = null;
    private TestChain testChain;
    private final Collection<HttpAction> stateChangingHttpActions = new ArrayList<HttpAction>();
    private ParameterValueDecision pvd;
    private RequestComposerDecomposer rcd;

    public AccessGrantedDecision getAgd() {
        return agd;
    }

    public void setAgd(AccessGrantedDecision agd) {
        this.agd = agd;
    }

    private AccessGrantedDecision agd;

    public FormFillDecision getFfd() {
        return ffd;
    }

    public void setFfd(FormFillDecision ffd) {
        this.ffd = ffd;
    }

    private FormFillDecision ffd;

    public ActionEqualityDecision getAcEqDec() {
        return acEqDec;
    }

    public void setAcEqDec(ActionEqualityDecision acEqDec) {
        this.acEqDec = acEqDec;
    }

    private ActionEqualityDecision acEqDec;

    public ResponseClassificator getRespClassificator() {
        return respClassificator;
    }

    public void setRespClassificator(ResponseClassificator respClassificator) {
        this.respClassificator = respClassificator;
    }

    private ResponseClassificator respClassificator;

    public ActionChangesStateDecision getChStateDec() {
        return chStateDec;
    }

    public void setChStateDec(ActionChangesStateDecision chStateDec) {
        this.chStateDec = chStateDec;
    }

    private ActionChangesStateDecision chStateDec;

    public HtmlPageEqualityDecision getPageEqDec() {
        return pageEqDec;
    }

    public void setPageEqDec(HtmlPageEqualityDecision pageEqDec) {
        this.pageEqDec = pageEqDec;
    }

    private HtmlPageEqualityDecision pageEqDec;

    public MessageEqualityDecision getrEqD() {
        return rEqD;
    }

    public void setrEqD(MessageEqualityDecision rEqD) {
        this.rEqD = rEqD;
    }

    private MessageEqualityDecision rEqD;
    private CookieService cookieService;
    private ActionService actionService;
    private ConversationService conversationService;
    private SitemapService sitemapService;
    private UserService userService;
    private ContextService contextService;
    private TaskManager taskManager;
    private final UseCaseGraph ucGraph = new UseCaseGraph();

    public URL getMainPage() {
        return mainPage;
    }

    public void setMainPage(URL mainPage) {
        this.mainPage = mainPage;
    }

    private URL mainPage;

    public String getCaptureFileName() {
        return captureFileName;
    }

    public void setCaptureFileName(String captureFileName) {
        this.captureFileName = captureFileName;
    }

    private String captureFileName;

    public UseCaseGraph getUcGraph() {
        return ucGraph;
    }

    public void addDynamicToken(String name, ActionParameterLocation loc) {
        dynamicTokenNames.add(name);
        dynamicTokenLocations.add(loc);
    }

    public ActionParameterMeaning getDynTokenMeaning(String name){
       if(dynamicTokenNames.indexOf(name)<0)
           return null;
       return ActionParameterMeaning.ONETIMETOKEN;
    }
    public ActionParameterLocation getDynTokenLoc(String name){
        int p = dynamicTokenNames.indexOf(name);
        if(p<0)
            return null;
        return dynamicTokenLocations.get(p);
    }

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

    public Collection<HttpAction> getStateChangingHttpActions() {
        return stateChangingHttpActions;
    }

    public void addStateChangingAction(HttpAction act){
        stateChangingHttpActions.add(act);
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
