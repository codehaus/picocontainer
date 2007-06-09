package org.nanocontainer.nanowar.webwork;

import org.picocontainer.defaults.ObjectReference;
import webwork.action.ActionContext;

/**
 * References an object that lives as an attribute of the
 * webwork action context
 *
 * @author Konstantin Pribluda
 */
public final class ActionContextScopeObjectReference implements ObjectReference {

    private final String key;

    public ActionContextScopeObjectReference(String key) {
        this.key = key;
    }

    public void set(Object item) {
        ActionContext.getContext().put(key, item);
    }

    public Object get() {
        return ActionContext.getContext().get(key);
    }

}
