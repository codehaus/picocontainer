/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.PicoInitializationException;

import java.lang.reflect.Constructor;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CyclicDependencyException extends PicoInitializationException {
    private final Constructor constructor;

    public CyclicDependencyException(Constructor constructor) {
        this.constructor = constructor;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public String getMessage() {
        return "Cyclic dependency: " + constructor.getName() + "(" + getCtorParams(constructor) + ")";
    }

    private String getCtorParams(Constructor constructor) {
        String retval = "";
        Class[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            retval = retval + parameterTypes[i].getName();
            if (i+1 < parameterTypes.length) {
                retval += ",";
            }
        }
        return retval;
    }
}
