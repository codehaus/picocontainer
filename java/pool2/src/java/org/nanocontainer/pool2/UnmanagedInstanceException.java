package org.picoextras.pool2;

import org.picocontainer.PicoException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class UnmanagedInstanceException extends PicoException {
    private Object instance;

    public UnmanagedInstanceException(Object instance) {
        this.instance = instance;
    }

    public Object getInstance() {
        return instance;
    }
}
