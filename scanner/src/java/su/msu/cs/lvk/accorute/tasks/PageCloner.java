package su.msu.cs.lvk.accorute.tasks;

import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.HtmlUnitUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 17.04.12
 * Time: 9:51
 * To change this template use File | Settings | File Templates.
 */
public class PageCloner extends Task {
    final HtmlPage original_;
    final WebWindow whereToClone_;
    HtmlPage resultPage = null;
    public PageCloner(TaskManager t, HtmlPage original, WebWindow whereToClone) {
        super(t);

        this.whereToClone_ = whereToClone;
        this.original_ = original;
    }

    @Override
    public Object getResult() {
        return resultPage;
    }
    
    public String toString(){
        return original_.getUrl().toString();
    }

    @Override
    protected void start() {
        setSuccessful(false);
        GregorianCalendar start = new GregorianCalendar();
        try {
            resultPage = HtmlUnitUtils.clonePage(original_, whereToClone_);
            setSuccessful(true);
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally{
            GregorianCalendar stop = new GregorianCalendar();
            logger.info("Clone page took " + (stop.getTimeInMillis() - start.getTimeInMillis()) / 1000.0 + " seconds");
        }
    }
}
