/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.gems.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.util.Properties;


/**
 * Hides implementation.
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @see HotSwappable
 */
public class HotSwapping extends AbstractBehaviorFactory {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1263007839990337386L;

	@Override
	public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor componentMonitor, final LifecycleStrategy lifecycleStrategy, final Properties componentProperties, final Object componentKey, final Class<T> componentImplementation, final Parameter... parameters)
            throws PicoCompositionException {
        ComponentAdapter<T> componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                         componentProperties, componentKey, componentImplementation, parameters);
        return new HotSwappable<T>(componentAdapter);
    }

    @Override
	public <T> ComponentAdapter<T> addComponentAdapter(final ComponentMonitor componentMonitor,
                                                final LifecycleStrategy lifecycleStrategy,
                                                final Properties componentProperties,
                                                final ComponentAdapter<T> adapter) {
        return new HotSwappable<T>(super.addComponentAdapter(componentMonitor,
                                                                 lifecycleStrategy,
                                                                 componentProperties,
                                                                 adapter));
    }
}
