package nanocontainer.servlet.holder;

import nanocontainer.servlet.ObjectHolder;

import javax.servlet.http.HttpSession;

/**
 * Holds an object in the HttpSession
 */ 
public class SessionScopeObjectHolder implements ObjectHolder {

    private HttpSession session;
    private String key;

    public SessionScopeObjectHolder(HttpSession session, String key) {
        this.session = session;
        this.key = key;
    }

    public void put(Object item) {
        session.setAttribute(key, item);
    }

    public Object get() {
        return session.getAttribute(key);
    }

}
