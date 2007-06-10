/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.InjectionFactory;
import org.picocontainer.PicoInitializationException;

import java.io.Serializable;


/**
 * A {@link org.picocontainer.ComponentFactory} for Guice style annotated methods.
 * The factory creates {@link AnnotationInjector}.
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public class AnnotationInjectionFactory implements InjectionFactory, Serializable {

    /**
     * Create a {@link SetterInjector}.
     *
     * @param componentMonitor
     *@param lifecycleStrategy
     * @param componentCharacteristic
     * @param componentKey            The addComponent's key
     * @param componentImplementation The class of the bean.
     * @param parameters              Any parameters for the setters. If null the adapter solves the
*                                dependencies for all setters internally. Otherwise the number parameters must match
*                                the number of the setter. @return Returns a new {@link SetterInjector}. @throws org.picocontainer.PicoIntrospectionException if dependencies cannot be solved
     * @throws org.picocontainer.PicoInitializationException
     *                                    if the implementation is an interface or an
     *                                    abstract class.
     */
    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoIntrospectionException, PicoInitializationException
    {
        return new AnnotationInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy);
    }
}