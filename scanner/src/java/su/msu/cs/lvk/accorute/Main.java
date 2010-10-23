package su.msu.cs.lvk.accorute;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.tasks.JSONConfigurator;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 20.04.2010
 * Time: 19:03:11
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    private static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args){
        ApplicationContext ctx;
        try {
            ctx = new ClassPathXmlApplicationContext("accorute-config.xml");
        } catch (BeanDefinitionStoreException ex) {
            System.err.println("Error loading evaluation contexts: " + ex.getMessage());
            return;
        }
        /*WebAppUser user = new WebAppUser();
        Action action = new Action("default action", new ArrayList<ActionParameter>());
        logger.trace(WebAppProperties.getInstance());

        WebAppProperties.getInstance().getUserService().addOrModifyUser(user);
        logger.trace(user.getUserID());

        WebAppProperties.getInstance().getActionService().addOrUpdateAction(action);
        logger.trace(action.getActionID());

        UserContext uctx = new UserContext();
        uctx.setUserID(user.getUserID());
        WebAppProperties.getInstance().getContextService().addOrUpdateContext(uctx);
        logger.trace(uctx.getContextID() + " " + uctx.getUserID());

        ArrayList<Cookie> list = new ArrayList<Cookie>();
        list.add(new Cookie(".ya.ru","fuck","off"));
        list.add(new Cookie("money.ya.ru","sonuva","bitch"));
        logger.trace(list.get(1).isDomainAttributeSpecified());
        CookieDescriptor cdesc = new CookieDescriptor(list,new CookieOrigin("www.ya.ru",80,"/",false),"Cookie");
        cdesc.setCtxID(uctx.getContextID());
        WebAppProperties.getInstance().getCookieService().setCookies(cdesc);
        try{
            Collection<ContextCookie> cooks =  WebAppProperties.getInstance().getCookieService().getCookiesForUrlInContext(
                uctx.getContextID(),
                new URL("http","ya.ru",80,"/"));
            for(ContextCookie cook: cooks){
                logger.debug("Domain: " + cook.getDomain() + " Path: " + cook.getPath() + " " + cook.toString());
            }
        }catch(MalformedURLException muex){}   */
        logger.trace(WebAppProperties.getInstance().getRoles().isEmpty());

        JSONConfigurator configTask = new JSONConfigurator(WebAppProperties.getInstance().getTaskManager(),
                "src/resources/django-test1.txt");
        WebAppProperties.getInstance().getTaskManager().addTask(configTask);
        new Thread(WebAppProperties.getInstance().getTaskManager()).start();
        WebAppProperties.getInstance().getTaskManager().terminate();
        while(configTask.getStatus() != Task.TaskStatus.FINISHED){
            ;//Spin lock =)
        }
        if(!configTask.isSuccessful()){
            logger.trace("smth went wrong!");
        }
        TestChain testChain = WebAppProperties.getInstance().getTestChain();
        for(int i=0; i < testChain.size(); i++){
            logger.trace("UC: " + testChain.get(i).action + " by role " +testChain.get(i).user.getUserRole().getRoleName());
            logger.trace("\n==========\n" + WebAppProperties.getInstance().getRcd().compose(
                    testChain.get(i).action.getActionParameters(),
                    testChain.get(i).user).toString()+ "\n==========\n"
            );
        }
    }
}
