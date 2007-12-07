/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by the committers                                           *
 *****************************************************************************/
package org.picocontainer.containers;

import java.util.Properties;
import java.lang.annotation.Annotation;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;

/**
 * abstract base class for delegating to mutable containers
 * @author Paul Hammant
 */
public abstract class AbstractDelegatingMutablePicoContainer extends AbstractDelegatingPicoContainer implements MutablePicoContainer {

	
    public AbstractDelegatingMutablePicoContainer(MutablePicoContainer delegate) {
		super(delegate);
	}

	public MutablePicoContainer addComponent(Object componentKey,
                                             Object componentImplementationOrInstance,
                                             Parameter... parameters) throws PicoCompositionException {
        return getDelegate().addComponent(componentKey, componentImplementationOrInstance, parameters);
    }

    public MutablePicoContainer addComponent(Object implOrInstance) throws PicoCompositionException {
        return getDelegate().addComponent(implOrInstance);
    }

    public MutablePicoContainer addConfig(String name, Object val) {
        return getDelegate().addConfig(name, val); 
    }

    public MutablePicoContainer addAdapter(ComponentAdapter<?> componentAdapter) throws PicoCompositionException {
        return getDelegate().addAdapter(componentAdapter);
    }

    public ComponentAdapter removeComponent(Object componentKey) {
        return getDelegate().removeComponent(componentKey);
    }

    public ComponentAdapter removeComponentByInstance(Object componentInstance) {
        return getDelegate().removeComponentByInstance(componentInstance);
    }

    public MutablePicoContainer addChildContainer(PicoContainer child) {
        return getDelegate().addChildContainer(child);
    }

    public boolean removeChildContainer(PicoContainer child) {
        return getDelegate().removeChildContainer(child);
    }

	public MutablePicoContainer change(Properties... properties) {
	    return getDelegate().change(properties);
	}

	public MutablePicoContainer as(Properties... properties) {
	    return getDelegate().as(properties);
	}
	
	public void dispose() {
		getDelegate().dispose();
	}

	public MutablePicoContainer makeChildContainer() {

		return null;
	}

	public void start() {
		getDelegate().start();
	}

	public void stop() {
		getDelegate().stop();
	}

	protected MutablePicoContainer getDelegate() {
		return (MutablePicoContainer) super.getDelegate();
	}
}
