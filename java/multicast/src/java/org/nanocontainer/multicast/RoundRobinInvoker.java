/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.multicast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class RoundRobinInvoker implements Invoker {
    private int current = 0;

    public void invoke(Object[] targets, Class declaringClass, Method method, Object[] args, List results) throws IllegalAccessException, InvocationTargetException {
        results.add(method.invoke(targets[current], args));
        current++;
        current = current % targets.length;
    }
}
