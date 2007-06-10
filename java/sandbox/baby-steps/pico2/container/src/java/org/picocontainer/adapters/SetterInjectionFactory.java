/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.PicoRegistrationException;

import java.io.Serializable;


/**
 * A {@link org.picocontainer.ComponentFactory} for JavaBeans.
 * The factory creates {@link SetterInjectionAdapter}.
 *
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class SetterInjectionFactory implements InjectionFactory, Serializable {

    /**
     * Create a {@link SetterInjectionAdapter}.
     *
     * @param componentMonitor
     * @param lifecycleStrategy
     * @param componentCharacteristic
     * @param componentKey                The addComponent's key
     * @param componentImplementation     The class of the bean.
     * @param parameters                  Any parameters for the setters. If null the addAdapter solves the
     *                                    dependencies for all setters internally. Otherwise the number parameters must match
     *                                    the number of the setter. @return Returns a new {@link org.picocontainer.adapters.SetterInjectionAdapter}. @throws PicoIntrospectionException if dependencies cannot be solved
     * @throws org.picocontainer.PicoRegistrationException
     *          if the implementation is an interface or an
     *          abstract class.
     */
    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoIntrospectionException, PicoRegistrationException {
        return new SetterInjectionAdapter(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy);
    }
}
