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
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.InjectionFactory;
import org.picocontainer.annotations.Inject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Creates instances Injectors, depending on whether the component is the presence of Annotations and characteristics.
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public class AdaptiveInjectionFactory implements InjectionFactory, Serializable {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   ComponentCharacteristic componentCharacteristic,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) throws
                                                                            PicoCompositionException {
        if (isFieldAnnotationInjection(componentImplementation)) {
            return new FieldAnnotationInjectionFactory().createComponentAdapter(componentMonitor,
                                                                                lifecycleStrategy,
                                                                                componentCharacteristic,
                                                                                componentKey,
                                                                                componentImplementation,
                                                                                parameters);
        }

        if (isMethodAnnotationInjection(componentImplementation)) {
            return new MethodAnnotationInjectionFactory().createComponentAdapter(componentMonitor,
                                                                                 lifecycleStrategy,
                                                                                 componentCharacteristic,
                                                                                 componentKey,
                                                                                 componentImplementation,
                                                                                 parameters);
        }

        if (ComponentCharacteristics.SDI.isCharacterizedIn(componentCharacteristic)) {
            ComponentAdapter componentAdapter = new SetterInjectionFactory().createComponentAdapter(componentMonitor,
                                                                                                    lifecycleStrategy,
                                                                                                    componentCharacteristic,
                                                                                                    componentKey,
                                                                                                    componentImplementation,
                                                                                                    parameters);
            ComponentCharacteristics.SDI.setProcessedIn(componentCharacteristic);
            return componentAdapter;
        }
        ComponentCharacteristics.CDI.setProcessedIn(componentCharacteristic);
        return new ConstructorInjectionFactory().createComponentAdapter(componentMonitor,
                                                                        lifecycleStrategy,
                                                                        componentCharacteristic,
                                                                        componentKey,
                                                                        componentImplementation,
                                                                        parameters);
    }

    private boolean isMethodAnnotationInjection(Class componentImplementation) {
        Method[] methods = componentImplementation.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotation(Inject.class) != null) {
                return true;
            }
        }
        return false;
    }

    private boolean isFieldAnnotationInjection(Class componentImplementation) {
        Field[] fields = componentImplementation.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Inject.class) != null) {
                return true;
            }
        }
        return false;
    }

}
