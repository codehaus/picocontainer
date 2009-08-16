package org.picocontainer.web.sample.ajaxemail;

import java.util.List;
import java.util.Collection;

import org.picocontainer.web.sample.ajaxemail.persistence.Persister;

/**
 * Abstract Mailbox
 */
public abstract class Mailbox {

    private final Persister persister;
    private final User user;
    private final QueryStore queryStore;

    public Mailbox(Persister persister, User user, QueryStore queryStore) {
        this.persister = persister;
        this.user = user;
        this.queryStore = queryStore;
    }

    protected Message addMessage(Message newMsg) {
        persister.makePersistent(newMsg);
        return newMsg;
    }

    /**
     * Read a message (flip its read flag if not already)
     * @param msgId the message to read
     * @return the message
     */
    public Message read(long msgId) {
        Message message = getMessage(msgId);
        if (!message.isRead()) {
            persister.beginTransaction();
            message.markRead();
            persister.commitTransaction();
        }
        return message;
    }

    @SuppressWarnings("unchecked")
	private Message getMessage(long msgId) {
        Collection<Message> coll = (Collection<Message>) getSingleMessageQuery().execute(msgId);
        if (coll != null && coll.size() == 1) {
            Message message = coll.iterator().next();
            checkUser(message);
            return message;
        }
        throw new AjaxEmailException("no such message ID");
    }

    protected abstract void checkUser(Message message) ;

    protected void throwNotForThisUser() {
        throw new AjaxEmailException("email ID not for the user logged in");
    }

    private Query getSingleMessageQuery() {
        String key = "SM_" + fromOrTo();
        Query query = queryStore.get(key);
        if (query == null) {
            query = persister.newQuery(Message.class, "id == message_id");
            query.declareImports("import java.lang.Long");
            query.declareParameters("Long message_id");
            queryStore.put(key, query);
        }
        return query;
    }

    /**
     * Delete a message
     * @param msgId the message to delete
     */
    public void delete(long msgId) {
        Message message = getMessage(msgId);
        persister.deletePersistent(message);
    }

    /**
     * List the messages for the user
     * @return the messages
     */
    @SuppressWarnings("unchecked")
	public Message[] messages() {
        Query query = getMultipleMessageQuery();
        List<Message> messageCollection = (List<Message>) query.execute(user.getName());
        return toArrayWithoutMessageBody(messageCollection);
    }

    private Message[] toArrayWithoutMessageBody(List<Message> messageCollection) {
        Message[] msgAry = messageCollection.toArray(new Message[messageCollection.size()]);
        for (Message message : msgAry) {
            message.clearMessageBody();
        }
        return msgAry;
    }

    private Query getMultipleMessageQuery() {
        String key = "MM_" + fromOrTo();
        Query query = queryStore.get(key);
        if (query == null) {
            query = persister.newQuery(Message.class, fromOrTo() + " == user_name");
            query.declareImports("import java.lang.String");
            query.declareParameters("String user_name");
            queryStore.put(key, query);
        }
        return query;
    }

    protected abstract String fromOrTo();

    protected String getUserName() {
        return user.getName();
    }

    public String toString() {
        return getUserName();
    }


}
