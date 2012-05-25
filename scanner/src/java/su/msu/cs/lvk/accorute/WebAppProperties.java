/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.04.2010
 * Time: 18:46:57
 * To change this template use File | Settings | File Templates.
 */
package su.msu.cs.lvk.accorute;

import org.apache.http.impl.client.AbstractHttpClient;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.decisions.*;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.storage.*;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.tasks.ContextedTaskFactory;
import su.msu.cs.lvk.accorute.utils.NotifyingListDecorator;
import su.msu.cs.lvk.accorute.utils.NotifyingObjectDecorator;

import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class WebAppProperties{
    private static boolean ENABLE_JAVASCRIPT_ANALYSIS = false;

    public boolean isENABLE_JAVASCRIPT_ANALYSIS() {
        return ENABLE_JAVASCRIPT_ANALYSIS;
    }

    private static WebAppProperties ourInstance = new WebAppProperties();
    private DynamicCredentialsUpdater dynCredUpd;

    public DynamicCredentialsUpdater getDynCredUpd() {
        return dynCredUpd;
    }

    public void setDynCredUpd(DynamicCredentialsUpdater dynCredUpd) {
        this.dynCredUpd = dynCredUpd;
    }
    public AbstractHttpClient getHttpClient() {
        return httpClient;
    }
    public void setHttpClient(AbstractHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private AbstractHttpClient httpClient;

    private SuppressDetectionDecision suppressDecision = new NoTestSuppression();

    public SuppressDetectionDecision getSuppressDecision() {
        return suppressDecision;
    }
    public void setSuppressDecision(SuppressDetectionDecision suppressDecision) {
        this.suppressDecision = suppressDecision;
    }
    public Pattern getUrlExcludeScope() {
        return urlExcludeScope.getObject();
    }
    public NotifyingObjectDecorator getUrlExcludeScopeNotifier() {
        return urlExcludeScope;
    }
    public void setUrlExcludeScope(Pattern urlExcludeScope) {
        this.urlExcludeScope.setObject(urlExcludeScope);
    }
    public Pattern getResponceExcludeScope() {
        return responceExcludeScope.getObject();
    }
    public NotifyingObjectDecorator getResponceExcludeScopeNotifier() {
        return responceExcludeScope;
    }
    public void setResponceExcludeScope(Pattern responceExcludeScope) {
        this.responceExcludeScope.setObject(responceExcludeScope);
    }
    public Pattern getUrlIncludeScope() {
        return urlIncludeScope.getObject();
    }
    public NotifyingObjectDecorator getUrlIncludeScopeNotifier() {
        return urlIncludeScope;
    }
    public void setUrlIncludeScope(Pattern urlIncludeScope) {
        this.urlIncludeScope.setObject(urlIncludeScope);
    }

    private NotifyingObjectDecorator<Pattern> urlExcludeScope = new NotifyingObjectDecorator<Pattern>(Pattern.compile("(?!)"));
    private NotifyingObjectDecorator<Pattern> responceExcludeScope = new NotifyingObjectDecorator<Pattern>(Pattern.compile("(?!)"));
    private NotifyingObjectDecorator<Pattern> urlIncludeScope = new NotifyingObjectDecorator<Pattern>(Pattern.compile("(?!)"));

    public void setDynamicTokens(List<ActionParameter> dynamicTokens){
        if(dynamicTokens == null)
            throw new IllegalArgumentException("dynamicTokens must be non-null");
        this.dynamicTokens = new NotifyingListDecorator<ActionParameter>(Collections.synchronizedList(dynamicTokens));
    }
    public NotifyingListDecorator<ActionParameter> getDynamicTokens() {
        return dynamicTokens;
    }

    private NotifyingListDecorator<ActionParameter>dynamicTokens = new NotifyingListDecorator<ActionParameter>(new LinkedList<ActionParameter>());

    public void addDynamicToken(String name, ActionParameterLocation location) {
        addDynamicToken(name, location, ActionParameterMeaning.ONETIMETOKEN);    
    }
    public void addDynamicToken(String name, ActionParameterLocation location, ActionParameterMeaning meaning) {
        addDynamicToken(name, location, meaning, ActionParameterDatatype.STRING);    
    }
    public void addDynamicToken(String name, ActionParameterLocation location, ActionParameterMeaning meaning, ActionParameterDatatype type) {
        addDynamicToken(name,".*",location, meaning, type);
    }
    public void addDynamicToken(String name,String value, ActionParameterLocation location){
        addDynamicToken(name,value,location,  ActionParameterMeaning.ONETIMETOKEN, ActionParameterDatatype.STRING);
    }
    public void addDynamicToken(String name,String value, ActionParameterLocation location, ActionParameterMeaning meaning, ActionParameterDatatype type) {
            dynamicTokens.add(new ActionParameter(name,value,location, meaning, type));
        }
    public ActionParameterMeaning getDynTokenMeaning(String name, String value, ActionParameterLocation location){
       for(ActionParameter param: dynamicTokens){
           Pattern nameRegex = Pattern.compile(param.getName());
           Pattern valueRegex = Pattern.compile(param.getValue());
           if(nameRegex.matcher(name).matches() && valueRegex.matcher(value).matches() && param.getLocation().equals(location)){
               return param.getMeaning();
           }
       }
       return null;
    }
    public boolean isDynToken(String name, String value, ActionParameterLocation location){
           for(ActionParameter param: dynamicTokens){
               Pattern nameRegex = Pattern.compile(param.getName());
               Pattern valueRegex = Pattern.compile(param.getValue());
               if(nameRegex.matcher(name).matches() && valueRegex.matcher(value).matches() && param.getLocation().equals(location)){
                   return true;
               }
           }
           return false;
        }
    public int getDynTokenNum(){
        return dynamicTokens.size();
    }
    public ActionParameter getDynTokenAt(int index){
        return dynamicTokens.get(index);
    }
    public void deleteDynToken(int i){
        if(i>=0 && i<dynamicTokens.size()){
            dynamicTokens.remove(i);
        }
    }
    private NotifyingListDecorator<Pattern> idParamNameRegexList = new NotifyingListDecorator<Pattern>(Collections.synchronizedList(new LinkedList<Pattern>()));
    private NotifyingListDecorator<Pattern> idParamValueRegexList = new NotifyingListDecorator<Pattern>(Collections.synchronizedList(new LinkedList<Pattern>()));
    public NotifyingListDecorator<Pattern> getIdParamNameRegexList() {
        return idParamNameRegexList;
    }
    public void setIdParamNameRegexList(List<Pattern> idParamNameRegexList) {
        if(idParamNameRegexList == null)
            throw new IllegalArgumentException("idParamNameRegexList must be non-null");
        this.idParamNameRegexList = new NotifyingListDecorator<Pattern>(Collections.synchronizedList(idParamNameRegexList));
    }
    public NotifyingListDecorator<Pattern> getIdParamValueRegexList() {
        return idParamValueRegexList;
    }
    public void setIdParamValueRegexList(List<Pattern> idParamValueRegexList) {
        if(idParamValueRegexList == null)
            throw new IllegalArgumentException("idParamValueRegexList must be non-null");
        this.idParamValueRegexList = new NotifyingListDecorator<Pattern>(Collections.synchronizedList(idParamValueRegexList));
    }

    public ContextedTaskFactory getAuthTaskFactory() {
        return authTaskFactory.getObject();
    }

    public void setAuthTaskFactory(ContextedTaskFactory authTaskFactory) {
        this.authTaskFactory.setObject(authTaskFactory);
    }
    public NotifyingObjectDecorator<ContextedTaskFactory> getAuthTaskFactoryNotifier(){
        return authTaskFactory;
    }

    private NotifyingObjectDecorator<ContextedTaskFactory> authTaskFactory = new NotifyingObjectDecorator<ContextedTaskFactory>(null);

    private final NotifyingListDecorator<HttpAction> stateChangingHttpActions = new NotifyingListDecorator<HttpAction>(Collections.synchronizedList(new ArrayList<HttpAction>()));
    private ParameterValueDecision pvd;
    private RequestComposerDecomposer rcd;

    public AccessGrantedDecision getAgd() {
        return agd;
    }

    public void setAgd(AccessGrantedDecision agd) {
        this.agd = agd;
    }

    private AccessGrantedDecision agd;

    public FormFillerFactory getFormFillerFactory() {
        return formFillerFactory;
    }

    public void setFormFillerFactory(FormFillerFactory formFillerFactory) {
        this.formFillerFactory = formFillerFactory;
    }

    private FormFillerFactory formFillerFactory;

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
    private CookieService cookieService;
    private ActionService actionService;
    private ConversationService conversationService;
    private SitemapService sitemapService;
    private UserService userService;
    private ContextService contextService;
    private TaskManager taskManager;
    private final UseCaseGraph ucGraph = new UseCaseGraph();

    public URL getMainPage() {
        return mainPage.getObject();
    }
    public NotifyingObjectDecorator<URL> getMainPageNotifier() {
            return mainPage;
        }

    public void setMainPage(URL mainPage) {
        this.mainPage.setObject(mainPage);
    }

    private final NotifyingObjectDecorator<URL> mainPage = new NotifyingObjectDecorator<URL>(null);

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
    private EntityID proxyContext = EntityID.NOT_INITIALIZED;

    public EntityID getProxyContext() {
        if(proxyContext == EntityID.NOT_INITIALIZED){
            UserContext ctx = new UserContext();
            getContextService().addOrUpdateContext(ctx);
            proxyContext = ctx.getContextID();
        }
        return proxyContext;
    }

    public ContextService getContextService() {
        return contextService;
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }

    public NotifyingListDecorator<HttpAction> getStateChangingHttpActions() {
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


    public NotifyingListDecorator<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = new NotifyingListDecorator<Role>(roles);
    }

    private NotifyingListDecorator<Role> roles = new NotifyingListDecorator<Role>(new ArrayList<Role>());

    
    public static WebAppProperties getInstance() {
        return ourInstance;
    }

    private WebAppProperties() {
    }
    public boolean checkSettings() throws IllegalStateException{
        if(
                acEqDec == null || agd == null || chStateDec == null || dynCredUpd ==null ||
                formFillerFactory == null || pageEqDec == null ||
                pvd == null || rcd == null || respClassificator == null || suppressDecision == null
        )
            throw new IllegalStateException("check decisions");
        if(
                actionService == null || contextService == null || conversationService == null ||
                cookieService == null || sitemapService == null || userService == null
        )
            throw new IllegalStateException("check dao services");
        if(authTaskFactory.getObject() == null)
            throw new IllegalStateException("check auth settings");
        if(httpClient == null)
            throw new IllegalStateException("check httpClient");
        if(mainPage.getObject() == null)
            throw new IllegalStateException("check mainPage");
        if(urlExcludeScope.getObject() == null || urlIncludeScope.getObject() == null)
            throw new IllegalStateException("check url scopes");
        if(responceExcludeScope.getObject() == null)
            throw new IllegalStateException("check responceExcludeScope");
        if(taskManager == null)
            throw new IllegalStateException("check taskManager");
        if(ucGraph == null)
            throw new IllegalStateException("check UseCase graph");
        return true;
    } 
}
