/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import picocontainer.ComponentFactory;
import picocontainer.PicoInvocationTargetStartException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultComponentFactory implements ComponentFactory {
    public Object createComponent(Class compType, Constructor constructor, Object[] args) throws PicoInvocationTargetStartException {
        try {
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new PicoInvocationTargetStartException(e.getCause());
        } catch (InstantiationException e) {
            throw new RuntimeException("#1 Can we have a concerted effort to try to force these excptions?");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("#2 Can we have a concerted effort to try to force these excptions?");
        }
    }
}
