package su.msu.cs.lvk.accorute.tasks;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.SimpleRBACRole;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterDatatype;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterMeaning;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterRole;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
/*
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
*/

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 07.05.12
 * Time: 21:15
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationLoader extends Task {
    
    public static final String version = "1";
    final File file;

    public ConfigurationLoader(TaskManager t, File file) {
        super(t);
        this.file = file;
    }

    @Override
    public Object getResult() {
        return null; 
    }
    
    private ActionParameter readActionParameter(Element paramElement){
        if(!paramElement.getTagName().equals("action_parameter"))
            throw new RuntimeException("Action parameter must be enclosed in action_parameter tag");
        
        String name = paramElement.getAttribute("name");
        String value = paramElement.getAttribute("value");
        ActionParameterLocation location = ActionParameterLocation.valueOf(paramElement.getAttribute("location"));
        ActionParameterMeaning meaning = ActionParameterMeaning.valueOf(paramElement.getAttribute("meaning"));
        ActionParameterDatatype type = ActionParameterDatatype.valueOf(paramElement.getAttribute("type"));
        ActionParameterRole role = ActionParameterRole.valueOf(paramElement.getAttribute("role"));
        return new ActionParameter(name, value,location,meaning,type,role);
    }
    
    private HttpAction readHttpAction(Element actionElement){
        if(!actionElement.getTagName().equals("action"))
            throw new RuntimeException("HttpAction must be enclosed in equals");
        String name = actionElement.getAttribute("name");
        List<ActionParameter> pList = new ArrayList<ActionParameter>();
        for(int i = 0; i < actionElement.getElementsByTagName("action_parameter").getLength(); i++){
            ActionParameter param = readActionParameter((Element) actionElement.getElementsByTagName("action_parameter").item(i));
            pList.add(param);
        }
        return new HttpAction(name, pList);
    }

    @Override
    protected void start() {
        try {
            setSuccessful(false);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            /*InputStream inputStream = new FileInputStream(file);
            XMLStreamReader inputReader = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            XMLStreamBuffer buf = MutableXMLStreamBuffer.createNewBufferFromXMLStreamReader(inputReader);
            Source source = new XMLStreamBufferSource(buf);
            DOMResult result = new DOMResult();
            transformer.transform(source,result);*/
            Node node = doc;
            if(node.getChildNodes().getLength() != 1)
                throw new RuntimeException("More than one root element!");
            Element rootElement = (Element) node.getChildNodes().item(0);
            if(!rootElement.getTagName().equals("configuration"))
                throw new RuntimeException("Root element not configuration");
            String ver = rootElement.getAttribute("version");
            if(!ver.equals(version))
                throw new RuntimeException("Unsupported version!");
            //I.scope
            NodeList scopeNodes = rootElement.getElementsByTagName("scope");
            if(scopeNodes.getLength() != 1)
                throw  new RuntimeException("There must be one and only one scope element!");
            Element scope = (Element) scopeNodes.item(0);
            NodeList includeList = scope.getElementsByTagName("include");   
            NodeList excludeList = scope.getElementsByTagName("exclude");
            NodeList mainPageList = scope.getElementsByTagName("main_page");
            if(includeList.getLength() != 1 || excludeList.getLength() != 1 || mainPageList.getLength() != 1)
                throw  new RuntimeException("There must be one and only one include, exclude and main_page element within scope!");
            Pattern incPattern = Pattern.compile(((Element)includeList.item(0)).getAttribute("pattern"));
            Pattern excPattern = Pattern.compile(((Element) excludeList.item(0)).getAttribute("pattern"));
            WebAppProperties.getInstance().setUrlIncludeScope(incPattern);
            WebAppProperties.getInstance().setUrlExcludeScope(excPattern);
            URL mainPage = new URL(((Element) mainPageList.item(0)).getAttribute("url"));
            WebAppProperties.getInstance().setMainPage(mainPage);
            //Auth
            NodeList authNodes = rootElement.getElementsByTagName("authentication");
            if(authNodes.getLength() != 1)
                throw  new RuntimeException("There must be one and only one authentication element!");
            Element authentication = (Element) authNodes.item(0);
            String type = authentication.getAttribute("type");
            if(type.equals("form")){
                URL url = new URL(authentication.getAttribute("url"));
                int form_index = Integer.valueOf(authentication.getAttribute("form_index"));
                String sumbit_xpath =  authentication.getAttribute("submit_xpath");
                WebAppProperties.getInstance().setAuthTaskFactory(new FormBasedAuthTaskFactory(url, form_index, sumbit_xpath));
            }else{
                throw new RuntimeException("Unsupported auth type: " + type);
            }
            //II. entity_id_list
            NodeList entId = rootElement.getElementsByTagName("entity_id_list");
            if(entId.getLength() != 1)
                throw new RuntimeException("There must be one and only one entity_id_list");
            NodeList entityIDs = ((Element)entId.item(0)).getElementsByTagName("item");
            List<Pattern > names = new LinkedList<Pattern>();
            List<Pattern > values = new LinkedList<Pattern>();
            for(int i = 0 ; i < entityIDs.getLength(); i++){
                Element item = (Element) entityIDs.item(i);
                String name = item.getAttribute("name");
                String value = item.getAttribute("value");
                names.add(Pattern.compile(name));
                values.add(Pattern.compile(value));
            }
            WebAppProperties.getInstance().getIdParamNameRegexList().clear();
            WebAppProperties.getInstance().getIdParamValueRegexList().clear();
            WebAppProperties.getInstance().getIdParamValueRegexList().addAll(values);
            WebAppProperties.getInstance().getIdParamNameRegexList().addAll(names);
            //III.dyn_token_list
            NodeList dToken = rootElement.getElementsByTagName("dyn_token_list");
            if(dToken.getLength() != 1)
                throw new RuntimeException("There must be one and only one dyn_token_list");
            NodeList dynTokens = ((Element)dToken.item(0)).getElementsByTagName("action_parameter");
            List<ActionParameter> params = new LinkedList<ActionParameter>();
            for(int i = 0; i < dynTokens.getLength(); i++){
                Element paramElem = (Element) dynTokens.item(i);
                ActionParameter param = readActionParameter(paramElem);
                params.add(param);
            }
            WebAppProperties.getInstance().getDynamicTokens().clear();
            WebAppProperties.getInstance().getDynamicTokens().addAll(params);
            //IV. Roles and users

            NodeList rolesList = rootElement.getElementsByTagName("roles");
            if(rolesList.getLength() != 1)
                throw new RuntimeException("There must be one and only one roles");
            Element roles = (Element) rolesList.item(0);
            HashMap<String, Role> roleMap = new HashMap<String, Role>();
            HashMap<String, String> roleParentMap = new HashMap<String, String>();
            List<WebAppUser> users = new LinkedList<WebAppUser>();
            for(int i = 0; i < roles.getElementsByTagName("role").getLength(); i++){
                Element role = (Element) roles.getElementsByTagName("role").item(i);
                String roleName = role.getAttribute("name");
                String parent = role.getAttribute("parent");
                if(parent.length() == 0){
                    Role r = SimpleRBACRole.createRootRole(roleName);
                    roleMap.put(roleName, r);
                }else{
                    if(roleMap.containsKey(parent)){
                        Role r = roleMap.get(parent).addChildRole(roleName);
                        roleMap.put(roleName, r);
                    }else{
                        roleParentMap.put(roleName, parent);
                    }
                }
                for(int j = 0; j < role.getElementsByTagName("user").getLength(); j++){
                    Element user = (Element) role.getElementsByTagName("user").item(j);
                    String log = user.getAttribute("log");
                    String pass = user.getAttribute("password");
                    WebAppUser u = new WebAppUser();
                    u.getStaticCredentials().put("log",log);
                    u.getStaticCredentials().put("password",pass);
                    u.getStaticCredentials().put("__role__", roleName);
                    users.add(u);
                }
            }
            while(roleParentMap.size() != 0){
                boolean changed = false;
                for(Map.Entry<String,String> entry : roleParentMap.entrySet()){
                    String name   = entry.getKey();
                    String parent = entry.getValue();
                    if(roleMap.containsKey(parent)){
                        changed = true;
                        roleMap.put(name, roleMap.get(parent).addChildRole(name));
                    }
                }
                if(!changed){
                    throw new RuntimeException("Unresolved roles " + roleParentMap.keySet());
                }
                for (String name : roleMap.keySet()){
                    roleParentMap.remove(name);
                }
            }
            WebAppProperties.getInstance().getRoles().clear();
            WebAppProperties.getInstance().getRoles().addAll(roleMap.values());
            WebAppProperties.getInstance().getUserService().clear();
            for(WebAppUser u : users){
                String roleName = u.getStaticCredentials().remove("__role__");
                if(! roleMap.containsKey(roleName))
                    throw new RuntimeException("Detected user of unknown role " + roleName);
                u.setRole(roleMap.get(roleName));
                WebAppProperties.getInstance().getUserService().addOrModifyUser(u);
            }
            // V. useCase graph
            if(rootElement.getElementsByTagName("use_case_graph").getLength() != 1)
                throw new RuntimeException("There must be one and only one use_case_graph");
            Element ucGraph = (Element) rootElement.getElementsByTagName("use_case_graph").item(0); 
            if(ucGraph.getElementsByTagName("use_cases").getLength() != 1)  
                throw new RuntimeException("There must be one and only one use_cases within use_case_graph");
            Element ucList = (Element) ucGraph.getElementsByTagName("use_cases").item(0);
            Map<String, UseCase> useCaseMap = new HashMap<String, UseCase>();
            UseCaseGraph theGraph = new UseCaseGraph();
            WebAppProperties.getInstance().getStateChangingHttpActions().clear();
            for(int i = 0; i < ucList.getElementsByTagName("uc").getLength(); i++){
                Element ucElem = (Element) ucList.getElementsByTagName("uc").item(i);
                String role = ucElem.getAttribute("role");
                if(!roleMap.containsKey(role))
                    throw new RuntimeException("Unexpected role " + role);
                String code = ucElem.getAttribute("code"); 
                if(ucElem.getElementsByTagName("action").getLength()!=1)
                    throw new RuntimeException("UseCase must have only one child node!");
                HttpAction act = readHttpAction((Element) ucElem.getElementsByTagName("action").item(0));
                WebAppProperties.getInstance().getActionService().addOrUpdateAction(act);
                UseCase uc = new UseCase(roleMap.get(role), act);
                useCaseMap.put(code, uc);
                theGraph.addUC(uc);
                WebAppProperties.getInstance().addStateChangingAction(act);
            }
            if(ucGraph.getElementsByTagName("dependencies").getLength() != 1)    
                throw new RuntimeException("There must be one and only one dependencies within use_case_graph");
            Element depList = (Element) ucGraph.getElementsByTagName("dependencies").item(0);
            for(int i = 0; i < depList.getElementsByTagName("dependency").getLength(); i++){
                Element dependency = (Element) depList.getElementsByTagName("dependency").item(i);
                String fromCode = dependency.getAttribute("from");
                String toCode = dependency.getAttribute("to");
                if(!useCaseMap.containsKey(fromCode) || !useCaseMap.containsKey(toCode)){
                    throw  new RuntimeException("Unexpected UseCase code found");
                }
                UseCase from = useCaseMap.get(fromCode);
                UseCase to = useCaseMap.get(toCode);
                theGraph.addDependency(from, to);
            }
            if(ucGraph.getElementsByTagName("cancellations").getLength() != 1)
                throw new RuntimeException("There must be one and only one cancellations within use_case_graph");
            Element cancList = (Element) ucGraph.getElementsByTagName("cancellations").item(0);
            for(int i = 0; i < cancList.getElementsByTagName("cancellation").getLength(); i++){
                Element cancellation = (Element) cancList.getElementsByTagName("cancellation").item(i);
                String fromCode = cancellation.getAttribute("from");
                String toCode = cancellation.getAttribute("to");
                if(!useCaseMap.containsKey(fromCode) || !useCaseMap.containsKey(toCode)){
                    throw  new RuntimeException("Unexpected UseCase code found");
                }
                UseCase from = useCaseMap.get(fromCode);
                UseCase to = useCaseMap.get(toCode);
                theGraph.addCancellation(from, to);
            }
            WebAppProperties.getInstance().getUcGraph().fromGraph(theGraph);
            //VI. state-changing actions  
            if(rootElement.getElementsByTagName("state_changing_actions").getLength() != 1)
                throw new RuntimeException("There must be one and only one state_changing_actions");
            Element stChAct = (Element) rootElement.getElementsByTagName("state_changing_actions").item(0);
            for(int i = 0; i < stChAct.getElementsByTagName("action").getLength(); i++){
                HttpAction act = readHttpAction((Element) stChAct.getElementsByTagName("action").item(i));
                WebAppProperties.getInstance().addStateChangingAction(act);
            }
            setSuccessful(true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }/* catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }*/ catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
