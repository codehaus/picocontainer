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
        super(componentImplementation.getName() + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: " + failedDependencies);
        this.componentImplementation = componentImplementation;
        this.failedDependencies = failedDependencies;
    }

    public Class getUnsatisfiableComponentImplementation() {
        return componentImplementation;
    }

    public Set getUnsatisfiableDependencies() {
        return failedDependencies;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoSatisfiableConstructorsException)) return false;

        final NoSatisfiableConstructorsException noSatisfiableConstructorsException = (NoSatisfiableConstructorsException) o;

        if (!componentImplementation.equals(noSatisfiableConstructorsException.componentImplementation)) return false;
        if (!failedDependencies.equals(noSatisfiableConstructorsException.failedDependencies)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = componentImplementation.hashCode();
        result = 29 * result + failedDependencies.hashCode();
        return result;
    }
}
