package su.msu.cs.lvk.accorute.storage;

import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.EntityID;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 15.04.2010
 * Time: 15:35:54
 * To change this template use File | Settings | File Templates.
 */
public interface ConversationService{
    void addConversationToContext(EntityID ctxID, Conversation conversation);
    void clearContextConversations(EntityID ctxID);
    Conversation getFirstConversationInContext(EntityID ctxID);
    Conversation getLastConversationInContext(EntityID ctxID);
    Collection<Conversation> getContextConversations(EntityID ctxID);
}