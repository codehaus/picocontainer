/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.behaviors.Cached;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.LifecycleStrategy;

import java.util.Properties;

/**
 * Behavior that turns off Caching behavior by default.  
 * <p>Example:</p>
 * <pre>
 * 		import org.picocontainer.*;
 * 		import static org.picocontainer.Characteristics.*;
 * 
 * 		MutablePicoContainer mpc = new PicoBuilder().withBehaviors(new OptInCaching()).build();
 * 		mpc.addComponent(Map.class, HashMap.class) //Multiple Instances, no Caching.
 * 		mpc.as(CACHE).addComponent(Set.class, HashSet.class) //Single Cached Instance.		
 * </pre>
 * @author Aslak Helles&oslash;y
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 */
@SuppressWarnings("serial")
public class OptInCaching extends AbstractBehaviorFactory {

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, 
    			Class<T> componentImplementation, Parameter... parameters)
            throws PicoCompositionException {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.CACHE)) {
            return componentMonitor.newBehavior(new Cached<T>(super.createComponentAdapter(componentMonitor,
                                                                                        lifecycleStrategy,
                                                                                        componentProperties,
                                                                                        componentKey,
                                                                                        componentImplementation,
                                                                                        parameters)));
        }
        AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.NO_CACHE);
        return super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                            componentProperties, componentKey, componentImplementation, parameters);
    }


    public <T> ComponentAdapter<T> addComponentAdapter(ComponentMonitor componentMonitor,
                                                LifecycleStrategy lifecycleStrategy,
                                                Properties componentProperties,
                                                ComponentAdapter<T> adapter) {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.CACHE)) {
            return componentMonitor.newBehavior(new Cached<T>(super.addComponentAdapter(componentMonitor,
                                                                 lifecycleStrategy,
                                                                 componentProperties,
                                                                 adapter)));
        }
        AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.NO_CACHE);
        return super.addComponentAdapter(componentMonitor,
                                         lifecycleStrategy,
                                         componentProperties,
                                         adapter);
    }
}