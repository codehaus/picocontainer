/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant & Obie Fernandez & Aslak                    *
 *****************************************************************************/

package org.picocontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * A component monitor is responsible for monitoring the component instantiation and method invocation.
 * 
 * @author Paul Hammant
 * @author Obie Fernandez
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @version $Revision$
 * @since 1.2
 */
public interface ComponentMonitor {

    void instantiating(Constructor constructor);

    void instantiated(Constructor constructor, long duration);

    void instantiationFailed(Constructor constructor, Exception e);

    void invoking(Method method, Object instance);

    void invoked(Method method, Object instance, long duration);

    void invocationFailed(Method method, Object instance, Exception e);

}
