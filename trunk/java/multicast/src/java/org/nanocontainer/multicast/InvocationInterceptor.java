/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 26-Jan-2004
 * Time: 21:14:38
 */
package org.nanocontainer.multicast;

import java.lang.reflect.Method;

public interface InvocationInterceptor {
    void intercept(Method method, Object target, Object[] args);
}