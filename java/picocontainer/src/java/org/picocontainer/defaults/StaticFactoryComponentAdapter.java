/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simmons & Jörg Schaible                              *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;


/**
 * Component adapter that wrapps a static factory with the help of {@link StaticFactory}.
 * @author J&ouml;rg Schaible
 * @author Leo Simmons
 * @since 1.1
 */
public class StaticFactoryComponentAdapter
        extends AbstractComponentAdapter {
    private StaticFactory staticFactory;

    /**
     * Construct a ComponentAdapter accessing a static factory creating the component.
     * 
     * @param type The type of the created component.
     * @param staticFactory Wrapper instance for the static factory.
     */
    public StaticFactoryComponentAdapter(Class type, StaticFactory staticFactory) {

        this(type, type, staticFactory);
    }

    /**
     * Construct a ComponentAdapter accessing a static factory creating the component 
     * using a special key for component registration.
     * 
     * @param type The type of the created component.
     * @param staticFactory Wrapper instance for the static factory.
     */
    public StaticFactoryComponentAdapter(Object componentKey, Class type, StaticFactory staticFactory) {
        super(componentKey, type);
        this.staticFactory = staticFactory;
    }

    /**
     * @return Returns the component created by the static factory.
     * @see org.picocontainer.ComponentAdapter#getComponentInstance()
     */
    public Object getComponentInstance()
            throws PicoInitializationException, PicoIntrospectionException {
        return staticFactory.get();
    }

    /**
     * {@inheritDoc}
     */
    public void verify() throws UnsatisfiableDependenciesException {
    }
}