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
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.Characterizations;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.LifecycleStrategy;

/**
 * @author Aslak Helles&oslash;y
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision$
 */
public class CachingBehaviorFactory extends AbstractBehaviorFactory {


    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristics componentCharacteristics, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException
    {
        if (Characterizations.NOCACHE.setAsProcessedIfSoCharacterized(componentCharacteristics)) {
            ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor,
                                                                             lifecycleStrategy,
                                                                             componentCharacteristics,
                                                                             componentKey,
                                                                             componentImplementation,
                                                                             parameters);
            return componentAdapter;
        }
        Characterizations.CACHE.setAsProcessedIfSoCharacterized(componentCharacteristics);
        return new CachingBehavior(super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                componentCharacteristics, componentKey, componentImplementation, parameters));

    }
}
