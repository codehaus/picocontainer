/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.PicoInitializationException;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class CyclicDependencyException extends PicoInitializationException {
    private final List dependencies;

    /**
     * @since 1.1
     */
    public CyclicDependencyException(Class type) {
        this.dependencies = new LinkedList();
        appendDependency(type);
    }
    
    /**
     * @since 1.1
     */
    public void appendDependency(Class type) {
        dependencies.add(type);
    }

    public Class[] getDependencies() {
        return (Class[]) dependencies.toArray(new Class[dependencies.size()]);
    }

    public String getMessage() {
        return "Cyclic dependency: " + dependencies.toString();
    }
}
