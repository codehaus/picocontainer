/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.PicoInitializationException;

import java.util.Arrays;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CyclicDependencyException extends PicoInitializationException {
    private final Class[] dependencies;

    public CyclicDependencyException(Class[] dependencies) {
        this.dependencies = dependencies;
    }

    public Class[] getDependencies() {
        return dependencies;
    }

    public String getMessage() {
        return "Cyclic dependency: " + Arrays.asList(dependencies).toString();
    }
}
