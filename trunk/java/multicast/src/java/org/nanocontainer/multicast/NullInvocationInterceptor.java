/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.multicast;

import org.nanocontainer.multicast.InvocationInterceptor;

import java.lang.reflect.Method;

public class NullInvocationInterceptor implements InvocationInterceptor {
    public void intercept(Method method, Object target, Object[] args) {
        // do nothing
    }
}