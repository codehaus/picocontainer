package org.picocontainer.defaults;

import org.picocontainer.PicoIntrospectionException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NoSatisfiableConstructorsException extends PicoIntrospectionException {
    private final Class componentImplementation;

    public NoSatisfiableConstructorsException(Class missingComponentImplementation) {
        if(missingComponentImplementation == null) {
            throw new NullPointerException("missingComponentImplementation can't be null");
        }
        this.componentImplementation = missingComponentImplementation;
    }

    public String getMessage() {
        return componentImplementation.getName() + " doesn't have any satisfiable constructors";
    }

    public Class getMissingComponentImplementation() {
        return componentImplementation;
    }
}
