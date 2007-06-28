/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.gems.monitors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.picocontainer.ComponentMonitor;

/**
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public class MultipleLoggerLog4JComponentMonitorTestCase extends AbstractComponentMonitorTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected ComponentMonitor makeComponentMonitor() {
        return new Log4JComponentMonitor();
    }

    protected Method getMethod() throws NoSuchMethodException {
        return String.class.getMethod("toString",new Class[0]);
    }

    protected Constructor getConstructor() {
        return String.class.getConstructors()[0];
    }

    protected String getLogPrefix() {
        return "[" + String.class.getName() + "] ";
    }

}
