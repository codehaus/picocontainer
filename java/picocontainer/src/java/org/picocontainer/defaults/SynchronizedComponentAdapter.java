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


import org.picocontainer.*;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.defaults.DecoratingComponentAdapter;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SynchronizedComponentAdapter extends DecoratingComponentAdapter {
    public SynchronizedComponentAdapter(ComponentAdapter delegate) {
        super(delegate);
    }

    public synchronized Object getComponentKey() {
        return super.getComponentKey();
    }

    public synchronized Class getComponentImplementation() {
        return super.getComponentImplementation();
    }

    public synchronized Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
        return super.getComponentInstance();
    }

    public synchronized void verify() throws UnsatisfiableDependenciesException {
        super.verify();
    }
}
