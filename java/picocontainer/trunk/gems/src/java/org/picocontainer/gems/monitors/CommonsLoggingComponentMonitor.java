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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Member;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.monitors.AbstractComponentMonitor;


/**
 * A {@link ComponentMonitor} which writes to a Commons Logging {@link Log Log} instance.
 * The Log instance can either be injected or, if not set, the {@link LogFactory LogFactory}
 * will be used to retrieve it at every invocation of the monitor.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 * @version $Revision: $
 */
public class CommonsLoggingComponentMonitor extends AbstractComponentMonitor implements Serializable {

    private Log log;

    /**
     * Creates a CommonsLoggingComponentMonitor with no Log instance set.
     * The {@link LogFactory LogFactory} will be used to retrieve the Log instance
     * at every invocation of the monitor.
     */
    public CommonsLoggingComponentMonitor() {
        // no log set
    }

    /**
     * Creates a CommonsLoggingComponentMonitor with a given Log instance class. 
     * The class name is used to retrieve the Log instance.
     * 
     * @param logClass the class of the Log
     */
    public CommonsLoggingComponentMonitor(Class logClass) {
        this(logClass.getName());
    }

    /**
     * Creates a CommonsLoggingComponentMonitor with a given Log instance name. It uses the
     * {@link LogFactory LogFactory} to create the Log instance.
     * 
     * @param logName the name of the Log
     */
    public CommonsLoggingComponentMonitor(String logName) {
        this(LogFactory.getLog(logName));
    }

    /**
     * Creates a CommonsLoggingComponentMonitor with a given Log instance
     * 
     * @param log the Log to write to
     */
    public CommonsLoggingComponentMonitor(Log log) {
        this.log = log;
    }

    public void instantiating(Constructor constructor) {
        Log log = getLog(constructor);
        if (log.isDebugEnabled()) {
            log.debug(format(INSTANTIATING, new Object[]{constructor}));
        }
    }

    public void instantiated(Constructor constructor, long duration) {
        Log log = getLog(constructor);
        if (log.isDebugEnabled()) {
            log.debug(format(INSTANTIATED, new Object[]{constructor, new Long(duration)}));
        }
    }

    public void instantiationFailed(Constructor constructor, Exception e) {
        Log log = getLog(constructor);
        if (log.isWarnEnabled()) {
            log.warn(format(INSTANTIATION_FAILED, new Object[]{constructor, e.getMessage()}),e);
        }
    }

    public void invoking(Method method, Object instance) {
        Log log = getLog(method);
        if (log.isDebugEnabled()) {
            log.debug(format(INVOKING, new Object[]{method, instance}));
        }
    }

    public void invoked(Method method, Object instance, long duration) {
        Log log = getLog(method);
        if (log.isDebugEnabled()) {
            log.debug(format(INVOKED, new Object[]{method, instance, new Long(duration)}));
        }
    }

    public void invocationFailed(Method method, Object instance, Exception e) {
        Log log = getLog(method);
        if (log.isWarnEnabled()) {
            log.warn(format(INVOCATION_FAILED, new Object[]{method, instance, e.getMessage()}),e);
        }
    }

    protected Log getLog(Member member) {
        if ( log != null ){
            return log;
        } 
        return LogFactory.getLog(member.getDeclaringClass());
    }

}
