/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

/**
 * Abstract baseclass for various PicoContainer implementations.
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public abstract class AbstractContainer implements PicoContainer {

    public final Object[] getComponents() {
        Class[] componentTypes = getComponentTypes();
        Object[] components = new Object[componentTypes.length];
        for (int i = 0; i < componentTypes.length; i++) {
            Class componentType = componentTypes[i];
            components[i] = getComponent(componentType);
        }
        return components;
    }
}
