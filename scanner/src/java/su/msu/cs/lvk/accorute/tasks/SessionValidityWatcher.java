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
    private boolean singleRetryFinished = true;
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
        logger.trace(ctx + ":will reauth");
        Task tsk = WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(ctx, tm);
        finishedReauth = false;
        final SessionValidityWatcher _this = this;
        tsk.registerCallback(
            new Callback0(){
                public void CallMeBack(){
                    synchronized(_this){
                        finishedReauth = true;
                        logger.trace(ctx + ":will notify reauth complete");
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
        logger.trace(ctx + ":reauth complete");
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
            logger.trace(ctx + ":"+yetUntestedFetches + " left to test");
            RespCheckStatus res;
            if(type == ResponseClassificator.ResponseType.EXPIRED
                    || type == ResponseClassificator.ResponseType.PROHIBITED
                    || type == ResponseClassificator.ResponseType.ERROR
            ){
                syncReAuth(tm);
            }
            res = RespCheckStatus.NOT_EXPIRED;
            if(yetUntestedFetches == 0){
                curState = SessionWatcherState.NORMAL;
                concurrentFetches = 0;
                notifyAll();
            }
            logger.trace(ctx + ":I'm returning from analyzeResponce after a retry");
            singleRetryFinished = true;
            notifyAll();
            return res;
        }
        if(curState == SessionWatcherState.JOINING ){
             while(!finishedReauth){
                try{
                    wait();
                }catch(InterruptedException ex){}
            }
            yetUnjoinedFetches--;
            if(yetUnjoinedFetches == 0){
                curState = SessionWatcherState.TESTING;
                singleRetryFinished = true;
                notifyAll();
            }
            notifyAll();
            logger.trace(ctx + ":" + yetUnjoinedFetches + " left to join");
            while(yetUnjoinedFetches > 0){
                try{
                    wait();
                }catch(InterruptedException ex){}
            }
            logger.trace(ctx + ":I'm returning from analyzeResponse for a retry");
            return RespCheckStatus.RETRY;
        }
        // NORMAL
        concurrentFetches--;
        if(type != ResponseClassificator.ResponseType.EXPIRED){
            logger.trace(ctx + ":I'm returning from analyzeResponse cause I'm not expired and state is normal");
            return RespCheckStatus.NOT_EXPIRED;
        }
        logger.trace(ctx + ":Expiration detected! Concurrent fetches " + concurrentFetches  );
        finishedReauth = false;
        //Expired session detected!!!
        if(concurrentFetches == 0){
            //I'm all alone and all is clear
            syncReAuth(tm);
            curState = SessionWatcherState.NORMAL;
            logger.trace(ctx + ":I'm returning from analyzeResponce cause I'm alone");
            notifyAll();
            return RespCheckStatus.EXPIRED;
        }
        curState = SessionWatcherState.JOINING;
        yetUnjoinedFetches = concurrentFetches;
        logger.trace(ctx + ":"+yetUnjoinedFetches + " left to join");
        yetUntestedFetches = concurrentFetches + 1;
        syncReAuth(tm);
        while(yetUnjoinedFetches > 0){
            try{
                wait();
            }catch(InterruptedException ex){}
        }
        logger.trace(ctx + ":I'm returning from analyzeResponce for a retry");
        return RespCheckStatus.RETRY;
    }


    /**
     * Waits for a valid session. Returns immediately, iff the session is currently valid
     */
    synchronized public void prepareForRequest(boolean retry){
        logger.trace(ctx + ":prepareForRequest , retry = " + retry);
        while(!retry && curState != SessionWatcherState.NORMAL || retry && !singleRetryFinished){
            try{
                wait();
            } catch(InterruptedException ex){}
        }
        if(!retry)
            concurrentFetches++;
        else
            singleRetryFinished = false;
        logger.trace(ctx + ":DoIt! Concurrent " + concurrentFetches);
    }
}
