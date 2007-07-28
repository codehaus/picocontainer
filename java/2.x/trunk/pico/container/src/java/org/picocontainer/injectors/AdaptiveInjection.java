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
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characteristics;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.InjectionFactory;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.annotations.Inject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Creates instances Injectors, depending on whether the component is the presence of Annotations and characteristics.
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public class AdaptiveInjection implements InjectionFactory, Serializable {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   Properties componentProperties,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) throws PicoCompositionException {
        ComponentAdapter componentAdapter = null;
        componentAdapter = makeIfFieldAnnotationInjection(componentImplementation,
                               componentMonitor,
                               lifecycleStrategy,
                               componentProperties,
                               componentKey,
                               componentAdapter,
                               parameters);

        if (componentAdapter != null) {
            return componentAdapter;
        }


        componentAdapter = makeIfMethodAnnotationInjection(componentImplementation,
                                                           componentMonitor,
                                                           lifecycleStrategy,
                                                           componentProperties,
                                                           componentKey,
                                                           componentAdapter,
                                                           parameters);

        if (componentAdapter != null) {
            return componentAdapter;
        }

        componentAdapter = makeIfSetterInjection(componentProperties,
                                                 componentMonitor,
                                                 lifecycleStrategy,
                                                 componentKey,
                                                 componentImplementation,
                                                 componentAdapter,
                                                 parameters);

        if (componentAdapter != null) {
            return componentAdapter;
        }


        return makeDefaultInjection(componentProperties,
                                    componentMonitor,
                                    lifecycleStrategy,
                                    componentKey,
                                    componentImplementation,
                                    parameters);
    }

    protected ComponentAdapter makeDefaultInjection(Properties componentProperties,
                                                  ComponentMonitor componentMonitor,
                                                  LifecycleStrategy lifecycleStrategy,
                                                  Object componentKey,
                                                  Class componentImplementation, Parameter... parameters)
    {
        AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.CDI);
        return new ConstructorInjectionFactory().createComponentAdapter(componentMonitor,
                                                                        lifecycleStrategy,
                                                                        componentProperties,
                                                                        componentKey,
                                                                        componentImplementation,
                                                                        parameters);
    }

    protected ComponentAdapter makeIfSetterInjection(Properties componentProperties,
                                                   ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   ComponentAdapter componentAdapter,
                                                   Parameter... parameters)
    {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.SDI)) {
            componentAdapter = new SetterInjectionFactory().createComponentAdapter(componentMonitor,
                                                                                                    lifecycleStrategy,
                                                                                                    componentProperties,
                                                                                                    componentKey,
                                                                                                    componentImplementation,
                                                                                                    parameters);
        }
        return componentAdapter;
    }

    protected ComponentAdapter makeIfMethodAnnotationInjection(Class componentImplementation,
                                                             ComponentMonitor componentMonitor,
                                                             LifecycleStrategy lifecycleStrategy,
                                                             Properties componentProperties,
                                                             Object componentKey,
                                                             ComponentAdapter componentAdapter,
                                                             Parameter... parameters)
    {
        if (isMethodAnnotationInjection(componentImplementation)) {
            componentAdapter =
                new AnnotatatedMethodInjection().createComponentAdapter(componentMonitor,
                                                                              lifecycleStrategy,
                                                                              componentProperties,
                                                                              componentKey,
                                                                              componentImplementation,
                                                                              parameters);
        }
        return componentAdapter;
    }

    protected ComponentAdapter makeIfFieldAnnotationInjection(Class componentImplementation,
                                 ComponentMonitor componentMonitor,
                                 LifecycleStrategy lifecycleStrategy,
                                 Properties componentProperties,
                                 Object componentKey, ComponentAdapter componentAdapter, Parameter... parameters)
    {
        if (isFieldAnnotationInjection(componentImplementation)) {
             componentAdapter =
                new AnnotatatedFieldInjection().createComponentAdapter(componentMonitor,
                                                                             lifecycleStrategy,
                                                                             componentProperties,
                                                                             componentKey,
                                                                             componentImplementation,
                                                                             parameters);
        }
        return componentAdapter;
    }

    protected boolean isMethodAnnotationInjection(Class componentImplementation) {
        Method[] methods = componentImplementation.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotation(Inject.class) != null) {
                return true;
            }
        }
        return false;
    }

    protected boolean isFieldAnnotationInjection(Class componentImplementation) {
        Field[] fields = componentImplementation.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Inject.class) != null) {
                return true;
            }
        }
        return false;
    }

}
