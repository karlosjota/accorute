package su.msu.cs.lvk.accorute.tasks;

import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.11.2010
 * Time: 22:54:13
 * To change this template use File | Settings | File Templates.
 */
public class FormBasedAuthTaskFactory implements ContextedTaskFactory {
    private final URL url;
    private final int formIndex;
    private final String submitXPath;

    public int getFormIndex() {
        return formIndex;
    }

    public String getSubmitXPath() {
        return submitXPath;
    }

    public URL getUrl() {
        return url;
    }

    public FormBasedAuthTaskFactory(URL url, int formIndex,String submitXPath){
        this.url = url;
        this.formIndex = formIndex;
        this.submitXPath = submitXPath;
    }
    public FormBasedAuthTaskFactory(URL url, int formIndex){
        this.url = url;
        this.formIndex = formIndex;
        this.submitXPath = null;
    }
    public Task genContextedTask(EntityID ctx,TaskManager t) {
        if(submitXPath != null && submitXPath.length() >0)
            return new FormBasedAuthTask(ctx,t, url, formIndex,submitXPath);
        else
            return new FormBasedAuthTask(ctx,t, url, formIndex);
    }
}
