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
import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Characteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.util.Properties;

/**
 * This behavior factory provides <strong>synchronized</strong> wrappers to control access to a particular component.
 *  It is recommended that you use {@link org.picocontainer.behaviors.Locking} instead since it results in better performance
 *  and does the same job.
 * @author Aslak Helles&oslash;y
 */
@SuppressWarnings("serial")
public class Synchronizing extends AbstractBehaviorFactory {

	
    /** {@inheritDoc} **/
	public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class<T> componentImplementation, Parameter... parameters) {
       if (removePropertiesIfPresent(componentProperties, Characteristics.NO_SYNCHRONIZE)) {
    	   return super.createComponentAdapter(
    	            componentMonitor,
    	            lifecycleStrategy,
    	            componentProperties,
    	            componentKey,
    	            componentImplementation,
    	            parameters);
       }
    	
    	removePropertiesIfPresent(componentProperties, Characteristics.SYNCHRONIZE);
        return new Synchronized<T>(super.createComponentAdapter(
            componentMonitor,
            lifecycleStrategy,
            componentProperties,
            componentKey,
            componentImplementation,
            parameters));
    }

    /** {@inheritDoc} **/
    public <T> ComponentAdapter<T> addComponentAdapter(ComponentMonitor componentMonitor,
                                                LifecycleStrategy lifecycleStrategy,
                                                Properties componentProperties,
                                                ComponentAdapter<T> adapter) {
        if (removePropertiesIfPresent(componentProperties, Characteristics.NO_SYNCHRONIZE)) {
        	return super.addComponentAdapter(componentMonitor,
                    lifecycleStrategy,
                    componentProperties,
                    adapter);
        }
    	
    	removePropertiesIfPresent(componentProperties, Characteristics.SYNCHRONIZE);
        return new Synchronized<T>(super.addComponentAdapter(componentMonitor,
                                         lifecycleStrategy,
                                         componentProperties,
                                         adapter));
    }
}
