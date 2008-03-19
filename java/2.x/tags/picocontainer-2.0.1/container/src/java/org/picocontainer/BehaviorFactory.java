/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

import java.util.Properties;

/**
 * Extends ComponentFactory to provide factory methods for Behaviors
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public interface BehaviorFactory extends ComponentFactory {

    ComponentFactory wrap(ComponentFactory delegate);

    <T> ComponentAdapter<T> addComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy,
            Properties componentProperties, ComponentAdapter<T> adapter);

}
