package su.msu.cs.lvk.accorute.storage.dao.RAM;

import org.apache.commons.collections.map.MultiValueMap;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.storage.ConversationService;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 20.04.2010
 * Time: 17:09:15
 * To change this template use File | Settings | File Templates.
 */
public class CollectionConversationService implements ConversationService{
    private final MultiValueMap database = new MultiValueMap();
    private Long nextId = 1l;

    public void addConversationToContext(EntityID ctxID, Conversation conversation){
        if(conversation.getId() != EntityID.NOT_INITIALIZED || ctxID == EntityID.NOT_INITIALIZED){
            throw new IllegalArgumentException();
        }
        EntityID id = new EntityID(nextId++);
        conversation.setId(id);
        database.put(ctxID,conversation);
    }

    public void clearContextConversations(EntityID ctxID) {
        database.remove(ctxID);
    }

    public Conversation getFirstConversationInContext(EntityID ctxID){
        Collection<Conversation> db = database.getCollection(ctxID);
        Conversation first = null;
        for(Conversation conv : db){
            if(first == null || conv.getTime().before(first.getTime()) ){
                first = conv;
            }
        }
        return first;
    }
    public Conversation getLastConversationInContext(EntityID ctxID){
        Collection<Conversation> db = database.getCollection(ctxID);
        Conversation last = null;
        for(Conversation conv : db){
            if(last == null || conv.getTime().after(last.getTime()) ){
                last = conv;
            }
        }
        return last;
    }

    public Collection<Conversation> getContextConversations(EntityID ctxID) {
        return database.getCollection(ctxID);
    }
}
