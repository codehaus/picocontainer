/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentFactory;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

/**
 * CompoentFactory that supports IoC type 3, which is constructor based.
 *
 * @author Aslak Hellesoy
 * @version $Revision: 1.8 $
 */
public class DefaultComponentFactory implements ComponentFactory, Serializable {
    public Object createComponent(ComponentSpecification componentSpec, Object[] instanceDependencies) throws PicoInvocationTargetInitializationException, NoPicoSuitableConstructorException {
        try {
            Constructor constructor = getConstructor(componentSpec.getComponentImplementation());
            return constructor.newInstance(instanceDependencies);
        } catch (InvocationTargetException e) {
            throw new PicoInvocationTargetInitializationException(e.getCause());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Class[] getDependencies(Class componentImplementation) throws PicoIntrospectionException {
        Constructor constructor = getConstructor(componentImplementation);
        return constructor.getParameterTypes();
    }

    /**
     * This is now IoC 2.5 compatible.  Multi ctors next.
     * @param componentImplementation
     * @return
     * @throws NoPicoSuitableConstructorException
     */
    private Constructor getConstructor(Class componentImplementation) throws NoPicoSuitableConstructorException {
        Constructor[] constructors = componentImplementation.getConstructors();
        Constructor picoConstructor = null;
        for (int i = 0; i < constructors.length; i++) {
            Constructor constructor = constructors[i];
            if (constructor.getParameterTypes().length != 0 || constructors.length == 1) {
                if (picoConstructor != null) {
                    throw new NoPicoSuitableConstructorException(componentImplementation);
                }
                picoConstructor = constructor;
            }
        }
        if (picoConstructor == null) {
            throw new NoPicoSuitableConstructorException(componentImplementation);
        }
        // Get the pico enabled constructor
        return picoConstructor;
    }
}
