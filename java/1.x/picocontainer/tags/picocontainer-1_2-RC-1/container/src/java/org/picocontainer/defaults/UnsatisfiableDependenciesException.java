/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoIntrospectionException;

import java.util.Set;

/**
 * Exception thrown when some of the component's dependencies are not satisfiable.
 * 
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @version $Revision$
 */
public class UnsatisfiableDependenciesException extends PicoIntrospectionException {

    private final ComponentAdapter instantiatingComponentAdapter;
    private final Set unsatisfiableDependencies;
    private final Class unsatisfiedDependencyType;

    public UnsatisfiableDependenciesException(ComponentAdapter instantiatingComponentAdapter, Set unsatisfiableDependencies) {
        super(instantiatingComponentAdapter.getComponentImplementation().getName() + " has unsatisfiable dependencies: " + unsatisfiableDependencies);
        this.instantiatingComponentAdapter = instantiatingComponentAdapter;
        this.unsatisfiableDependencies = unsatisfiableDependencies;
        this.unsatisfiedDependencyType = null;
    }

    public UnsatisfiableDependenciesException(ComponentAdapter instantiatingComponentAdapter, Class unsatisfiedDependencyType, Set unsatisfiableDependencies) {
        super(instantiatingComponentAdapter.getComponentImplementation().getName() + " has unsatisfied dependency: " + unsatisfiedDependencyType
                +" among unsatisfiable dependencies: "+unsatisfiableDependencies);
        this.instantiatingComponentAdapter = instantiatingComponentAdapter;
        this.unsatisfiableDependencies = unsatisfiableDependencies;
        this.unsatisfiedDependencyType = unsatisfiedDependencyType;
    }
    
    public ComponentAdapter getUnsatisfiableComponentAdapter() {
        return instantiatingComponentAdapter;
    }

    public Set getUnsatisfiableDependencies() {
        return unsatisfiableDependencies;
    }

    public Class getUnsatisfiedDependencyType() {
        return unsatisfiedDependencyType;
    }

    
}
