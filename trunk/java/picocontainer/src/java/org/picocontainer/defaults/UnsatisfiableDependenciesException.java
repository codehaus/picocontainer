package org.picocontainer.defaults;

import org.picocontainer.PicoIntrospectionException;

import java.util.Set;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class UnsatisfiableDependenciesException extends PicoIntrospectionException {

    private final InstantiatingComponentAdapter instantiatingComponentAdapter;
    private final Set failedDependencies;

    public UnsatisfiableDependenciesException(InstantiatingComponentAdapter instantiatingComponentAdapter, Set failedDependencies) {
        super(instantiatingComponentAdapter.getComponentImplementation().getName() + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: " + failedDependencies);
        this.instantiatingComponentAdapter = instantiatingComponentAdapter;
        this.failedDependencies = failedDependencies;
    }

    public InstantiatingComponentAdapter getUnsatisfiableComponentAdapter() {
        return instantiatingComponentAdapter;
    }

    public Set getUnsatisfiableDependencies() {
        return failedDependencies;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnsatisfiableDependenciesException)) return false;

        final UnsatisfiableDependenciesException noSatisfiableConstructorsException = (UnsatisfiableDependenciesException) o;

        if (!instantiatingComponentAdapter.equals(noSatisfiableConstructorsException.instantiatingComponentAdapter)) return false;
        if (!failedDependencies.equals(noSatisfiableConstructorsException.failedDependencies)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = instantiatingComponentAdapter.hashCode();
        result = 29 * result + failedDependencies.hashCode();
        return result;
    }
}
