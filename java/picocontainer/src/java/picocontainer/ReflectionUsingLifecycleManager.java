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

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class ReflectionUsingLifecycleManager implements StartableLifecycleManager {

    private static final Class[] NOPARMS = new Class[0];
    private static final Object[] NOARGS = new Object[0];

    public void startComponent(Object component) throws PicoStartException {
        try {
            invokeMethodByName(component, "start");
        } catch (InvocationTargetException e) {
            throw new PicoInvocationTargetStartException(e.getCause());
        }
        System.out.println("Start Gikk bra");
    }

    public void stopComponent(Object component) throws PicoStopException {
        try {
            invokeMethodByName(component, "stop");
        } catch (InvocationTargetException e) {
            throw new PicoInvocationTargetStopException(e.getCause());
        }
        System.out.println("Stopp Gikk bra");
    }

    protected void invokeMethodByName(Object component, String methodName) throws InvocationTargetException {
        try {
            System.out.println("Getting Method: " + component.getClass().getName() + "." + methodName + "()");
            Method method = component.getClass().getMethod(methodName, NOPARMS);
            System.out.println("Invoking Method: " + Modifier.toString(method.getModifiers()) + " " + method );
            method.invoke(component, NOARGS);
        } catch (NoSuchMethodException e) {
            // fine.
        } catch (IllegalAccessException e) {
            // fine.
        }
    }


}
