package su.msu.cs.lvk.accorute.http.model;

import com.gargoylesoftware.htmlunit.javascript.host.xml.XMLDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.04.2010
 * Time: 12:19:25
 * To change this template use File | Settings | File Templates.
 */
public class HttpAction implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return actionParameters.toString();
    }

    public String toStringNl() {
        StringWriter wr = new StringWriter();
        for(ActionParameter p: actionParameters){
            wr.write(p.getName());
            wr.write(":");
            wr.write(p.getValue());
            wr.write("\\n");
        }
        return wr.toString();
    }
    public String toStringRealNl() {
        StringWriter wr = new StringWriter();
        for(ActionParameter p: actionParameters){
            wr.write(p.getName());
            wr.write(":");
            wr.write(p.getValue());
            wr.write("\n");
        }
        return wr.toString();
    }

    public void appendToElement(Element el) {
        Document doc = el.getOwnerDocument();
        Element a = doc.createElement("action");
        el.appendChild(a);
        a.setAttribute("name",name);
        a.setAttribute("id",actionID.getId().toString());
        Element paramSet = doc.createElement("params");
        a.appendChild(paramSet);
        for(int i=0; i < actionParameters.size(); i++){
            Element param = doc.createElement("param");
            ActionParameter ap = actionParameters.get(i);
            param.setAttribute("name",ap.getName());
            param.setAttribute("value",ap.getValue());
            param.setAttribute("type",ap.getDatatype().toString());
            param.setAttribute("location",ap.getLocation().toString());
            param.setAttribute("meaning",ap.getMeaning().toString());
            param.setAttribute("role",ap.getRole().toString());
            paramSet.appendChild(param);
        }
    }

    public EntityID getActionID() {
        return actionID;
    }

    public void setActionID(EntityID actionID) {
        this.actionID = actionID;
    }

    public List<ActionParameter> getActionParameters() {
        return actionParameters;
    }

    public void setActionParameters(List<ActionParameter> actionParameters) {
        this.actionParameters = actionParameters;
    }

    private String name;
    private List<ActionParameter> actionParameters;
    private transient EntityID actionID = EntityID.NOT_INITIALIZED;

    public HttpAction(String name,List<ActionParameter> params ){
        this.name = name;
        this.actionParameters = params;
    }

}
