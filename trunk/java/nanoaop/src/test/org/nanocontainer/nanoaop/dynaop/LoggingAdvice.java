/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.dynaop;

import org.nanocontainer.nanoaop.Interceptor;
import org.nanocontainer.nanoaop.Invocation;

/**
 * @author Stephen Molitor
 */
public class LoggingAdvice implements Interceptor {

    private final StringBuffer log;

    public LoggingAdvice(StringBuffer log) {
        this.log = log;
    }

    public Object intercept(Invocation invocation) throws Throwable {
        log.append("start");
        Object result = invocation.proceed();
        log.append("stop");
        return result;
    }

}