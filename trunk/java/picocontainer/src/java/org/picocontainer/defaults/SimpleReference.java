package org.picocontainer.defaults;

import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SimpleReference implements ObjectReference, Serializable {
    private Object instance;

    public Object get() {
        return instance;
    }

    public void set(Object item) {
        this.instance = item;
    }
}
