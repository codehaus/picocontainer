/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
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
            Method method = component.getClass().getMethod("start", NOPARMS);
            if ((method.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
               method.invoke(component, NOARGS);
            }
        } catch (NoSuchMethodException e) {
            // fine.
        } catch (SecurityException e) {
            throw new SecurityStartException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Will never happen!");
        } catch (InvocationTargetException e) {
            throw new PicoInvocationTargetStartException(e.getCause());
        }

    }

    public void stopComponent(Object component) throws PicoStopException {
        try {
            Method method = component.getClass().getMethod("stop", NOPARMS);
            method.invoke(component, NOARGS);
        } catch (NoSuchMethodException e) {
            // fine.
        } catch (SecurityException e) {
            // could be running in applet space or other
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }
}
