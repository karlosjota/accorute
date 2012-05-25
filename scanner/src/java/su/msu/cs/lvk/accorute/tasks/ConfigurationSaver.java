package su.msu.cs.lvk.accorute.tasks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 07.05.12
 * Time: 2:04
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationSaver extends Task {
    public static final String version = "1";
    private final File file;
    public ConfigurationSaver(TaskManager t, File file) {
        super(t);
        this.file = file;
    }
    @Override
    public Object getResult() {
        return null;
    }
    private Element dumpActionParameter(Document doc, ActionParameter param){
        Element paramElement = doc.createElement("action_parameter");
        paramElement.setAttribute("name", param.getName());
        paramElement.setAttribute("value", param.getValue());
        paramElement.setAttribute("location", param.getLocation().toString());
        paramElement.setAttribute("meaning", param.getMeaning().toString());
        paramElement.setAttribute("type", param.getDatatype().toString());
        paramElement.setAttribute("role", param.getRole().toString());
        return paramElement;        
    }
    private Element dumpHttpAction(Document doc, HttpAction action){
        Element actionElement = doc.createElement("action");
        actionElement.setAttribute("name", action.getName());
        for(ActionParameter p : action.getActionParameters()){
            Element pElement = dumpActionParameter(doc,p);
            actionElement.appendChild(pElement);
        }
        return actionElement;
    }
    @Override
    protected void start() {
        setSuccessful(false);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("configuration");
            doc.appendChild(rootElement);
            rootElement.setAttribute("version", version);
            //I. scope
            Element scopeElement = doc.createElement("scope");
            String excludeScope = WebAppProperties.getInstance().getUrlExcludeScope().toString();
            Element excScope = doc.createElement("exclude");
            excScope.setAttribute("pattern", excludeScope);
            String includeScope = WebAppProperties.getInstance().getUrlIncludeScope().toString();
            Element incScope = doc.createElement("include");
            incScope.setAttribute("pattern", includeScope);
            scopeElement.appendChild(incScope);
            scopeElement.appendChild(excScope);
            Element mainPage = doc.createElement("main_page");
            mainPage.setAttribute("url", WebAppProperties.getInstance().getMainPage().toString());
            scopeElement.appendChild(mainPage);
            //Auth
            Element authentication  = doc.createElement("authentication");
            if( WebAppProperties.getInstance().getAuthTaskFactory() instanceof FormBasedAuthTaskFactory){
                FormBasedAuthTaskFactory factory = (FormBasedAuthTaskFactory) WebAppProperties.getInstance().getAuthTaskFactory();
                authentication.setAttribute("type", "form");
                authentication.setAttribute("url", factory.getUrl().toString());
                authentication.setAttribute("submit_xpath", factory.getSubmitXPath());
                authentication.setAttribute("form_index", Integer.toString(factory.getFormIndex()));
            }
            rootElement.appendChild(authentication);
            rootElement.appendChild(scopeElement);
            //II. EntityID params
            Element entityID = doc.createElement("entity_id_list");
            int size = WebAppProperties.getInstance().getIdParamNameRegexList().size();
            for(int i = 0; i < size ; i++){
                Pattern name = WebAppProperties.getInstance().getIdParamNameRegexList().get(i);
                Pattern value = WebAppProperties.getInstance().getIdParamValueRegexList().get(i);
                Element item =  doc.createElement("item");
                item.setAttribute("name", name.toString());
                item.setAttribute("value", value.toString());
                entityID.appendChild(item);
            }
            rootElement.appendChild(entityID);
            // III.Dynamic Tokens
            Element dynTokens = doc.createElement("dyn_token_list");
            for(ActionParameter dynToken : WebAppProperties.getInstance().getDynamicTokens()){
                Element param = dumpActionParameter(doc, dynToken);
                dynTokens.appendChild(param);
            }
            rootElement.appendChild(dynTokens);
            //IV. Roles and users
            Element rolesElement = doc.createElement("roles");
            for(Role r: WebAppProperties.getInstance().getRoles()){
                Element roleElement = doc.createElement("role");
                roleElement.setAttribute("name", r.getRoleName());
                Role parent = r.getParentRole();
                if(parent != null)
                    roleElement.setAttribute("parent", parent.getRoleName());
                for(WebAppUser u: WebAppProperties.getInstance().getUserService().getUsersByRole(r.getRoleName())){
                    Element userElement = doc.createElement("user");
                    for(Map.Entry<String,String> cred : u.getStaticCredentials().entrySet()){
                        userElement.setAttribute(cred.getKey(), cred.getValue());
                    }
                    roleElement.appendChild(userElement);
                }
                rolesElement.appendChild(roleElement);
            }
            rootElement.appendChild(rolesElement);
            //V. UseCase graph
            UseCaseGraph graph = WebAppProperties.getInstance().getUcGraph();
            Element ucGraphElement = doc.createElement("use_case_graph");
            Element ucList =  doc.createElement("use_cases");
            Set<HttpAction> ucStChActions = new HashSet<HttpAction>();
            for(UseCase uc : graph.getUseCases()){
                Element ucElement = doc.createElement("uc");
                ucElement.setAttribute("role", uc.getUserRole().getRoleName());
                ucElement.setAttribute("code", Integer.toString(uc.hashCode()));
                ucStChActions.add(uc.getHttpAct());
                Element actionElement = dumpHttpAction(doc,uc.getHttpAct());
                ucElement.appendChild(actionElement);
                ucList.appendChild(ucElement);
            }
            ucGraphElement.appendChild(ucList);
            Element depList =  doc.createElement("dependencies");
            Element cancList =  doc.createElement("cancellations");
            for(UseCase uc : graph.getUseCases()){
                for(UseCase req: graph.getPrerequisites(uc) ){
                    Element depElement = doc.createElement("dependency");
                    depElement.setAttribute("from",Integer.toString(req.hashCode()));
                    depElement.setAttribute("to",Integer.toString(uc.hashCode()));
                    depList.appendChild(depElement);
                }
                for(UseCase canc: graph.getCancelledBy(uc)){
                    Element cancElement = doc.createElement("cancellation");
                    cancElement.setAttribute("from", Integer.toString(uc.hashCode()));
                    cancElement.setAttribute("to", Integer.toString(canc.hashCode()));
                    cancList.appendChild(cancElement);
                }
            }
            ucGraphElement.appendChild(depList);
            ucGraphElement.appendChild(cancList);
            rootElement.appendChild(ucGraphElement);
            //VI. state-changing actions
            Element stChAct = doc.createElement("state_changing_actions");
            for(HttpAction act : WebAppProperties.getInstance().getStateChangingHttpActions()){
                if(ucStChActions.contains(act))
                    continue;
                Element actElem = dumpHttpAction(doc, act);
                stChAct.appendChild(actElem);
            }
            rootElement.appendChild(stChAct);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            setSuccessful(true);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
