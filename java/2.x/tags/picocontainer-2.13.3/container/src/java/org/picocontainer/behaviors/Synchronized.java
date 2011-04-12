/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;


import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehavior;

import java.lang.reflect.Type;

/**
 * Component Adapter that uses java synchronized around getComponentInstance().
 * @author Aslak Helles&oslash;y
 * @author Manish Shah
 */
@SuppressWarnings("serial")
public class Synchronized<T> extends AbstractBehavior<T> {


	public Synchronized(ComponentAdapter<T> delegate) {
        super(delegate);
    }

    public synchronized T getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
        return super.getComponentInstance(container, into);
    }

    public String getDescriptor() {
        return "Synchronized"; 
    }

}
