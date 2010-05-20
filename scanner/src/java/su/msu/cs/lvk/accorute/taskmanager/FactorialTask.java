package su.msu.cs.lvk.accorute.taskmanager;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 03.04.2010
 * Time: 22:03:11
 * To change this template use File | Settings | File Templates.
 */
public class FactorialTask extends Task{
    final int num;
    int result = 0;
    FactorialTask(TaskManager t, int numb){
        super(t);
        num = numb;
    }

    public void start(){
        if(num == 0){
            synchronized(this){
                result = 1;
                return;
            }
        }
        FactorialTask tsk = new FactorialTask(taskManager, num - 1);
        waitForTask(tsk);
        synchronized(this){
            result = num * (Integer) tsk.getResult();
            return;
        }

    }
    synchronized public Object getResult(){
        if(getStatus() == TaskStatus.FINISHED){
            return result;
        }
        return null;
    }
}
