package org.picocontainer.defaults;

import org.picocontainer.PicoIntrospectionException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NoSatisfiableConstructorsException extends PicoIntrospectionException {
    private final Class componentImplementation;

    public NoSatisfiableConstructorsException(Class componentImplementation) {
        super(componentImplementation.getName() + " doesn't have any satisfiable constructors");
        this.componentImplementation = componentImplementation;
    }

    public Class getUnsatisfiableComponentImplementation() {
        return componentImplementation;
    }
}
