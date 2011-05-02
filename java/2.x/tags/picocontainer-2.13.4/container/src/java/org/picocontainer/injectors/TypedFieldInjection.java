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
import java.util.Properties;
import static org.picocontainer.Characteristics.immutable;

/**
 * A {@link org.picocontainer.InjectionFactory} for named fields.
 *
 * Use like so: pico.as(injectionFieldNames("field1", "field2")).addComponent(...)
 *
 * The factory creates {@link TypedFieldInjector}.
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class TypedFieldInjection extends AbstractInjectionFactory {

    private static final String INJECTION_FIELD_TYPES = "injectionFieldTypes";

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor monitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   Properties componentProperties,
                                                   Object componentKey,
                                                   Class<T> componentImplementation,
                                                   Parameter... parameters) throws PicoCompositionException {
        String fieldTypes = (String) componentProperties.remove(INJECTION_FIELD_TYPES);
        if (fieldTypes == null) {
            fieldTypes = "";
        }
        return wrapLifeCycle(monitor.newInjector(new TypedFieldInjector(componentKey, componentImplementation, parameters, monitor,
                fieldTypes)), lifecycleStrategy);
    }

    public static Properties injectionFieldTypes(String... fieldTypes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldTypes.length; i++) {
            sb.append(" ").append(fieldTypes[i]);
        }
        return immutable(INJECTION_FIELD_TYPES, sb.toString().trim());
    }

}