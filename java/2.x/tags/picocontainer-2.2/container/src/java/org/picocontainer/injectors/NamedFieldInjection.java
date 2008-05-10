/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.InjectionFactory;
import org.picocontainer.Characteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import static org.picocontainer.Characteristics.immutable;

import java.util.Properties;
import java.io.Serializable;

/**
 * A {@link org.picocontainer.InjectionFactory} for named fields.
 *
 * Use like so: pico.as(injectionFieldNames("field1", "field2")).addComponent(...)
 *
 * The factory creates {@link org.picocontainer.injectors.NamedFieldInjector}.
 *
 * @author Paul Hammant
 */
public class NamedFieldInjection implements InjectionFactory, Serializable {


    /**
	 * Serialization UUID.
	 */
    private static final long serialVersionUID = 5805385846324233960L;

    private static final String INJECTION_FIELD_NAMES = "injectionFieldNames";

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   Properties componentProperties,
                                                   Object componentKey,
                                                   Class<T> componentImplementation,
                                                   Parameter... parameters) throws PicoCompositionException {
        String fieldNames = (String) componentProperties.remove(INJECTION_FIELD_NAMES);
        if (fieldNames == null) {
            fieldNames = "";
        }
        return new NamedFieldInjector(componentKey, componentImplementation, parameters, componentMonitor,
                lifecycleStrategy, fieldNames);
    }

    public static Properties injectionFieldNames(String... fieldNames) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldNames.length; i++) {
            sb.append(" ").append(fieldNames[i]);
        }
        Properties retVal = new Properties();
        return immutable(INJECTION_FIELD_NAMES, sb.toString().trim());
    }

}