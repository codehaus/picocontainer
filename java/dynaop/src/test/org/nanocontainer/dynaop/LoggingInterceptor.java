/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.dynaop;

import dynaop.Interceptor;
import dynaop.Invocation;


public class LoggingInterceptor implements Interceptor {
    private StringBuffer log;
    
    public LoggingInterceptor(StringBuffer log) {
        this.log = log;
    }
    
    public Object intercept(Invocation invocation) throws Throwable {
        log.append("start");
        Object result = invocation.proceed();
        log.append("end");
        return result;
    }

}