/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import picocontainer.ComponentFactory;
import picocontainer.PicoIntrospectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * CompoentFactory that supports IoC type 3, which is constructor based.
 *
 * @author Aslak Hellesoy
 * @version $Revision: 1.8 $
 */
public class DefaultComponentFactory implements ComponentFactory {
    public Object createComponent(Class componentType, Class componentImplementation, Class[] dependencies, Object[] instanceDependencies) throws PicoInvocationTargetInitializationException, WrongNumberOfConstructorsException {
        try {
            Constructor constructor = getConstructor(componentImplementation);
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

    private Constructor getConstructor(Class componentImplementation) throws WrongNumberOfConstructorsException {
        Constructor[] constructors = componentImplementation.getConstructors();
        if (constructors.length != 1) {
            throw new WrongNumberOfConstructorsException(constructors.length);
        }
        // Get the sole constructor
        return componentImplementation.getConstructors()[0];
    }
}
