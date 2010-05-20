package su.msu.cs.lvk.accorute.taskmanager;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 05.04.2010
 * Time: 1:49:49
 * To change this template use File | Settings | File Templates.
 */
public class JoinTask extends SerialTask{
    final String info;
    public void start(){
        System.out.println(info);
    }
    JoinTask(TaskManager t, String str){
        super(t);
        info = str;
    }
    public Object getResult(){
        return null;
    }
}
