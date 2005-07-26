/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mauro Talevi                                             *
 *****************************************************************************/

package org.picocontainer.gems.monitors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.monitors.AbstractComponentMonitor;

/**
 * A {@link ComponentMonitor} which writes to a {@link Log} instance. 
 * 
 * @author Mauro Talevi
 * @version $Revision: $
 */
public class LoggingComponentMonitor extends AbstractComponentMonitor {

    private Log log;
    
    /**
     * Creates a LoggingComponentMonitor with a given log instance
     * @param log the Log to write to
     */
    public LoggingComponentMonitor(Log log) {
        this.log = log;
    }

    /**
     * Creates a LoggingComponentMonitor using the LogFactory to create
     * the default log 
     */
    public LoggingComponentMonitor() {
        this.log = LogFactory.getLog(LoggingComponentMonitor.class);
    }
    
    public void instantiating(Constructor constructor) {
        log.debug(format(INSTANTIATING, new Object[]{constructor}));
    }

    public void instantiated(Constructor constructor, long duration) {
        log.debug(format(INSTANTIATED, new Object[]{constructor, new Long(duration)}));
    }

    public void instantiationFailed(Constructor constructor, Exception e) {
        log.warn(format(INSTANTIATION_FAILED, new Object[]{constructor, e.getMessage()}));
    }

    public void invoking(Method method, Object instance) {
        log.debug(format(INVOKING, new Object[]{method, instance}));
    }

    public void invoked(Method method, Object instance, long duration) {
        log.debug(format(INVOKED, new Object[]{method, instance, new Long(duration)}));
    }

    public void invocationFailed(Method method, Object instance, Exception e) {
        log.warn(format(INVOCATION_FAILED, new Object[]{method, instance, e.getMessage()}));
    }

}
