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
import picocontainer.PicoInvocationTargetInitailizationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultComponentFactory implements ComponentFactory {
    public Object createComponent(Class compType, Class compImplementation, Class[] dependencies, Object[] args) throws PicoInvocationTargetInitailizationException {
        try {
            Constructor constructor = compImplementation.getConstructor(dependencies);
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new PicoInvocationTargetInitailizationException(e.getCause());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Class[] getDependencies(Class comp) {
        return comp.getConstructors()[0].getParameterTypes();
    }
}
