package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 18.11.2010
 * Time: 15:48:42
 * To change this template use File | Settings | File Templates.
 */
public class SimpleFormFill implements FormFillDecision{

    public void FillForm(HtmlForm f, EntityID ctxID) {
        for(HtmlElement i: f.getHtmlElementsByTagName("input")){
            HtmlInput input = (HtmlInput) i;
            String name = input.getNameAttribute();
            EntityID uid = WebAppProperties.getInstance().getContextService().getContextByID(ctxID).getUserID();
            WebAppUser user = WebAppProperties.getInstance().getUserService().getUserByID(uid);
            boolean updated = false;
            if(user.getStaticCredentials().containsKey(name)){
                input.setValueAttribute(user.getStaticCredentials().get(name));
                updated = true;
            }
            if(!updated){
                if(input.getTypeAttribute().equalsIgnoreCase("password")
                        && user.getStaticCredentials().containsKey("password")
                ){
                    input.setValueAttribute(user.getStaticCredentials().get("password"));
                }
            }
        }
        //TODO: do not forget about textarea!!!
    }
}
