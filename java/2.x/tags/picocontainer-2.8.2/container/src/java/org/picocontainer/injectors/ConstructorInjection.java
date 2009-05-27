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

import java.util.Properties;

import org.picocontainer.Characteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

/**
 * A {@link org.picocontainer.InjectionFactory} for constructor injection.
 * The factory creates {@link ConstructorInjector}.
 *
 * If there is more than one constructor for the component, the one with the
 * most satisfiable parameters will be used.  By default, the choice of
 * constructor for the component in question will be remembered between usages.
 * 
 * @author Paul Hammant 
 * @author Jon Tirs&eacute;n
 */
@SuppressWarnings("serial")
public class ConstructorInjection extends AbstractInjectionFactory  {

    private final boolean rememberChosenConstructor;

    /**
     *
     * @param rememberChosenConstructor whether 'which constructor?' should be remembered
     *                                  from use to use for the associated injector.
     */
    public ConstructorInjection(boolean rememberChosenConstructor) {
        this.rememberChosenConstructor = rememberChosenConstructor;
    }

    /**
     * Will remember which constructor to use between usages on the associated
     * Injector.
     */
    public ConstructorInjection() {
        this(true);
    }

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties properties, Object componentKey,
                                                   Class<T> componentImplementation, Parameter... parameters) throws PicoCompositionException {
        boolean useNames = AbstractBehaviorFactory.arePropertiesPresent(properties, Characteristics.USE_NAMES, true);
        return componentMonitor.newInjector(new ConstructorInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, useNames, rememberChosenConstructor));
    }
}
