package nanocontainer.servlet.holder;

import nanocontainer.servlet.ObjectHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * Holds an object in the ServletRequest
 */ 
public class RequestScopeObjectHolder implements ObjectHolder {

    private HttpServletRequest request;
    private String key;

    public RequestScopeObjectHolder(HttpServletRequest request, String key) {
        this.request = request;
        this.key = key;
    }

    public void put(Object item) {
        request.setAttribute(key, item);
    }

    public Object get() {
        return request.getAttribute(key);
    }

}
