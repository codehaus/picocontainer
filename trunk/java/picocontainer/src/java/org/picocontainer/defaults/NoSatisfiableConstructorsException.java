package org.picocontainer.defaults;

import org.picocontainer.PicoIntrospectionException;

import java.util.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NoSatisfiableConstructorsException extends PicoIntrospectionException {

    private final Class componentImplementation;
    private final Set failedDependencies;

    public NoSatisfiableConstructorsException(Class componentImplementation, Set failedDependencies) {
        super(componentImplementation.getName() + " doesn't have any satisfiable constructors. Usatisfiable dependencies: " + failedDependencies.toString());
        this.componentImplementation = componentImplementation;
        this.failedDependencies = failedDependencies;
    }

    public Class getUnsatisfiableComponentImplementation() {
        return componentImplementation;
    }

    public Set getUnsatisfiableDependencies() {
        return failedDependencies;
    }
}
