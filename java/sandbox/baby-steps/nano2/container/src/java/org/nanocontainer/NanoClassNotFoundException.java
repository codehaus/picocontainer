package org.nanocontainer;

import org.picocontainer.PicoException;

public class NanoClassNotFoundException extends PicoException {

    public NanoClassNotFoundException(final String className, final ClassNotFoundException cnfe) {
        super("Class '" + className + "' not found", cnfe);  
    }
}
