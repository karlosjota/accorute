package su.msu.cs.lvk.accorute.http.model;

import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 14.11.2010
 * Time: 21:11:41
 * To change this template use File | Settings | File Templates.
 */
public class CorrespondentActions {
    private final ArrayList<HttpAction> httpActions;
    private final ArrayList<DomAction> domActions;

    public ArrayList<DomAction> getDomActions() {
        return domActions;
    }

    public CorrespondentActions(ArrayList<HttpAction> h, ArrayList<DomAction> d){
        httpActions = h;
        domActions = d;
    }

    public ArrayList<HttpAction> getHttpActions() {
        return httpActions;
    }
    public String getAsDotLabel() {
        StringWriter output = new StringWriter();
        if(httpActions.size()==1){
            output.write(httpActions.get(0).toStringNl());
        }else{
            output.write("{");
            for(HttpAction a: httpActions){
                output.write(a.toStringNl() + "|");
            }
            output.getBuffer().deleteCharAt(output.getBuffer().length()-1);
            output.write("}");
        }
        output.write("\\n");
        if(domActions.size()==1){
            output.write(domActions.get(0).toString());
        }else{
            output.write("{");
            for(DomAction da: domActions){
                output.write(da + "|");
            }
            output.getBuffer().deleteCharAt(output.getBuffer().length()-1);
            output.write("}");
        }
        return output.toString();
    }
    public String getAsDotRecord(){
        StringWriter output = new StringWriter();
        output.write("{");
        if(httpActions.size()==1){
            output.write(httpActions.get(0).toString());
        }else{
            for(HttpAction a: httpActions){
                output.write(a + "|");
            }
            output.getBuffer().deleteCharAt(output.getBuffer().length()-1);
        }
        output.write("|");
        if(domActions.size()==1){
            output.write(domActions.get(0).toString());
        }else{
            for(DomAction da: domActions){
                output.write(da + "|");
            }
            output.getBuffer().deleteCharAt(output.getBuffer().length()-1);
        }
        output.write("}");
        return output.toString();
    }
}
