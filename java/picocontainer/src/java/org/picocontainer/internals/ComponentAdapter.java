package org.picocontainer.internals;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

public interface ComponentAdapter {
    Parameter createDefaultParameter();

    Object getComponentKey();

    Class getComponentImplementation();

    Object instantiateComponent(ComponentRegistry componentRegistry) throws PicoInitializationException;

    void addConstantParameterBasedOnType(Class parameter, Object arg) throws PicoIntrospectionException;
}
