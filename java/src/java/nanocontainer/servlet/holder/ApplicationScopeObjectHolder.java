package nanocontainer.servlet.holder;

import nanocontainer.servlet.ObjectHolder;
import javax.servlet.ServletContext;


/**
 * Holds an object in the ServletContext (application scope)
 */

public class ApplicationScopeObjectHolder implements ObjectHolder {

    private ServletContext context;
    private String key;

    public ApplicationScopeObjectHolder(ServletContext context, String key) {
        this.context = context;
        this.key = key;
    }

    public void put(Object item) {
        context.setAttribute(key, item);
    }

    public Object get() {
        return context.getAttribute(key);
    }

}

