/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy                                           *
 *****************************************************************************/

package nanocontainer.aggregating.reflect;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Arrays;

/**
 *
 * @author Aslak Hellesoy
 * @version $Revision: 1.4 $
 */
public class NoInvocationTargetException extends Exception {
    private final Object proxy;
    private final Method method;

    public NoInvocationTargetException(Object proxy, Method method) {
        this.proxy = proxy;
        this.method = method;
    }

    public Object getProxy() {
        return proxy;
    }

    public Method getMethod() {
        return method;
    }

    public String getMessage() {
        List interfaces = Arrays.asList(proxy.getClass().getInterfaces());
        return method.toString() + " doesn't exist in any of the proxy's interfaces: " + interfaces;
    }
}
