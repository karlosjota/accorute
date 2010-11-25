package su.msu.cs.lvk.accorute.taskmanager;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 05.04.2010
 * Time: 1:49:49
 * To change this template use File | Settings | File Templates.
 */
public class JoinTask extends Task{
    public void start(){
    }
    public JoinTask(TaskManager t){
        super(t, true);
    }
    public Object getResult(){
        return null;
    }
}
