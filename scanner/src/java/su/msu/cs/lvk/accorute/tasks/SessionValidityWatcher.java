package su.msu.cs.lvk.accorute.tasks;

import org.apache.log4j.Logger;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.decisions.ResponseClassificator;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback0;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 21.11.2010
 * Time: 22:22:57
 * To change this template use File | Settings | File Templates.
 */
public class SessionValidityWatcher {
    public enum RespCheckStatus {
        NOT_EXPIRED,
        RETRY,
        EXPIRED
    }
    private enum SessionWatcherState{
        NORMAL,
        JOINING,
        TESTING
    }
    private  SessionWatcherState curState = SessionWatcherState.NORMAL;
    private EntityID ctx;
    private int concurrentFetches = 0;
    private int yetUntestedFetches = 0;
    private int yetUnjoinedFetches = 0;
    private boolean finishedReauth;
    private  Logger logger = Logger.getLogger(this.getClass().getName());
    static private final Map<EntityID, SessionValidityWatcher> instances = new HashMap<EntityID, SessionValidityWatcher>();
    private SessionValidityWatcher(EntityID ctxID){
        ctx = ctxID;
    }
    static public SessionValidityWatcher getInstanceForContext(EntityID ctxID){
        if(instances.containsKey(ctxID)){
            return instances.get(ctxID);
        }
        SessionValidityWatcher watcher = new SessionValidityWatcher(ctxID);
        instances.put(ctxID, watcher);
        return watcher;
    }

    synchronized private void syncReAuth(TaskManager tm){
        logger.trace("will reauth");
        Task tsk = WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(ctx, tm);
        finishedReauth = false;
        final SessionValidityWatcher _this = this;
        tsk.registerCallback(
            new Callback0(){
                public void CallMeBack(){
                    synchronized(_this){
                        finishedReauth = true;
                        logger.trace("will notify reauth complete");
                        _this.notifyAll();
                    }
                }
            }
        );
        tm.addWaitedTask(tsk);
        while(!finishedReauth){
            try{
                wait();
            }catch(InterruptedException ex){}
        }
        logger.trace("reauth complete");
    }

    /**
     * If the responce is an invalid session, this will perform reauth.
     * @param conv
     * @return NOT_EXPIRED, if the response is not an expired session;
     * EXPIRED , if the responce is expired, but not first of these ( high possibility benign )
     * EXPIRED_FIRST, if the responce is first of expired  ( high possibility caused session expiration )
     */
    synchronized public RespCheckStatus analyzeResponce(Conversation conv, TaskManager tm){
        ResponseClassificator.ResponseType type = WebAppProperties.getInstance().getRespClassificator().getResponseType(conv);
        if(curState == SessionWatcherState.TESTING){
            yetUntestedFetches--;
            notifyAll();
            logger.trace(yetUntestedFetches + "left to test");
            RespCheckStatus res;
            if(type == ResponseClassificator.ResponseType.EXPIRED){
                syncReAuth(tm);
                res = RespCheckStatus.EXPIRED;
            }
            res = RespCheckStatus.NOT_EXPIRED;
            if(yetUntestedFetches == 0){
                curState = SessionWatcherState.NORMAL;
                notifyAll();
            }
            logger.trace("I'm returning from analyzeResponce after a retry");
            return res;
        }
        if(curState == SessionWatcherState.JOINING ){
            yetUnjoinedFetches--;
            notifyAll();
            logger.trace(yetUntestedFetches + "left to join");
            while(yetUnjoinedFetches > 0){
                try{
                    wait();
                }catch(InterruptedException ex){}
            }
            logger.trace("I'm returning from analyzeResponce for a retry");
            return RespCheckStatus.RETRY;
        }
        // NORMAL
        concurrentFetches--;
        if(type != ResponseClassificator.ResponseType.EXPIRED){
            logger.trace("I'm returning from analyzeResponce cause I'm not expired and state is normal");
            return RespCheckStatus.NOT_EXPIRED;
        }
        logger.trace("Expiration detected!");
        curState = SessionWatcherState.JOINING;
        syncReAuth(tm);
        //Expired session detected!!!
        if(concurrentFetches == 0){
            //I'm all alone and all is clear
            curState = SessionWatcherState.NORMAL;
            logger.trace("I'm returning from analyzeResponce cause I'm alone");
            notifyAll();
            return RespCheckStatus.EXPIRED;
        }
        yetUnjoinedFetches = concurrentFetches;
        logger.trace(yetUnjoinedFetches + "left to join");
        yetUntestedFetches = concurrentFetches;
        while(yetUnjoinedFetches > 0){
            try{
                wait();
            }catch(InterruptedException ex){}
        }
        logger.trace("I'm returning from analyzeResponce for a retry");
        return RespCheckStatus.RETRY;
    }


    /**
     * Waits for a valid session. Returns immediately, iff the session is currently valid
     */
    synchronized public void prepareForRequest(boolean retry){
        logger.trace("prepareForRequest");
        while(! retry && curState != SessionWatcherState.NORMAL){
            try{
                wait();
            } catch(InterruptedException ex){}
        }
        concurrentFetches++;
        logger.trace("DoIt!");
    }
}
