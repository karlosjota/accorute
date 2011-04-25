package su.msu.cs.lvk.accorute;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.SimpleRBACRole;
import su.msu.cs.lvk.accorute.decisions.FormFiller;
import su.msu.cs.lvk.accorute.decisions.MultiStateFormFillFactory;
import su.msu.cs.lvk.accorute.http.model.UserContext;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 4/25/11
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormFillerTest {
    public static void main(String[] args){
        ApplicationContext ctx;
        try {
            ctx = new FileSystemXmlApplicationContext(args[0]);
        } catch (BeanDefinitionStoreException ex) {
            System.err.println("Error loading evaluation contexts: " + ex.getMessage());
            return;
        }
        Role role = SimpleRBACRole.createRootRole("user");
        WebAppUser u1 = new WebAppUser();
        u1.getStaticCredentials().put("username","user1");
        u1.getStaticCredentials().put("password","user1");
        u1.setRole(role);
        WebAppProperties.getInstance().getUserService().addOrModifyUser(u1);
        UserContext u1Ctx = new UserContext();
        u1Ctx.setUserID(u1.getUserID());
        WebAppProperties.getInstance().getContextService().addOrUpdateContext(u1Ctx);

        WebClient w = new WebClient();
        try{
            HtmlPage p = w.getPage(args[1]);
            HtmlForm f = p.getFormByName("editFrm");
            MultiStateFormFillFactory factory = new MultiStateFormFillFactory(
                    "default text",
                    "default text",
                    true,
                    true,
                    true
            );
            FormFiller filler = factory.generate(f, u1Ctx.getContextID());
            System.out.println(HtmlUnitUtils.getUserControllableFormFields(f));
            while(filler.hasNext()){
                HtmlElement sub = filler.next();
                System.out.println("==============================");
                for( HtmlElement el : f.getElementsByTagName("input")){
                    HtmlInput inp = (HtmlInput) el;
                    System.out.println(
                            inp.getNameAttribute() + " : "
                            + inp.getValueAttribute() + " ; "
                            + inp.getCheckedAttribute()
                    );
                }
                System.out.println("submit with " + sub.getAttribute("name"));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
