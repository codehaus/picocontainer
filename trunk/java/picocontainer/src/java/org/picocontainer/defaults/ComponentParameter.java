/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;

import java.io.Serializable;

/**
 * A ComponentParameter should be used to pass in a particular component
 * as argument to a different component's constructor. This is particularly
 * useful in cases where several components of the same type have been registered,
 * but with a different key. Passing a ComponentParameter as a parameter
 * when registering a component will give PicoContainer a hint about what
 * other component to use in the constructor.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentParameter implements Parameter, Serializable {

    private Object componentKey;

    /**
     * Expect a parameter matching a component of a specific key.
     *
     * @param componentKey the key of the desired component
     */
    public ComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    /**
     * Expect any paramter of the appropriate type.
     */
    public ComponentParameter() {
    }

    public ComponentAdapter resolveAdapter(PicoContainer picoContainer, Class expectedType) throws PicoIntrospectionException {
        ComponentAdapter result;
        if (componentKey != null) {
            result = picoContainer.getComponentAdapter(componentKey);
            if (result != null && !expectedType.isAssignableFrom(result.getComponentImplementation())) {
                // found one, but it wasn't of the expected type
                result = null;
            }
        } else {
            result = picoContainer.getComponentAdapterOfType(expectedType);
        }
        return result;
    }

}
