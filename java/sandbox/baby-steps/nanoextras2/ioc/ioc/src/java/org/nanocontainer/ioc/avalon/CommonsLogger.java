/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.ioc.avalon;

import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;

/**
 * An implementation of {@link Logger} which delegates to a {@link Log Commons-Log}.
 * 
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class CommonsLogger implements Logger, Log {
    /**
     * The Log to delegate to.
     */ 
    private final Log m_logger;

    /**
     * Create a logger that delegates to specified Log.
     * 
     * @param logImpl the Commons-Logging Log to delegate to.
     */
    public CommonsLogger(final Log logImpl) {
        Check.argumentNotNull("logImpl", logImpl);
        m_logger = logImpl;
    }

    /**
     * Log a debug message.
     * 
     * @param message the message.
     */
    public final void debug(final String message) {
        m_logger.debug(message);
    }

    /**
     * Log a debug message.
     * 
     * @param message   the message.
     * @param throwable the throwable.
     */
    public final void debug(final String message, final Throwable throwable) {
        m_logger.debug(message, throwable);
    }

    /**
     * Determine if messages of priority "debug" will be logged.
     * 
     * @return true if "debug" messages will be logged.
     */
    public final boolean isDebugEnabled() {
        return m_logger.isDebugEnabled();
    }

    /**
     * Log a info message.
     * 
     * @param message the message.
     */
    public final void info(final String message) {
        m_logger.info(message);
    }

    /**
     * Log a info message.
     * 
     * @param message   the message.
     * @param throwable the throwable.
     */
    public final void info(final String message, final Throwable throwable) {
        m_logger.info(message, throwable);
    }

    /**
     * Determine if messages of priority "info" will be logged.
     * 
     * @return true if "info" messages will be logged.
     */
    public final boolean isInfoEnabled() {
        return m_logger.isInfoEnabled();
    }

    /**
     * Log a warn message.
     * 
     * @param message the message.
     */
    public final void warn(final String message) {
        m_logger.warn(message);
    }

    /**
     * Log a warn message.
     * 
     * @param message   the message.
     * @param throwable the throwable.
     */
    public final void warn(final String message, final Throwable throwable) {
        m_logger.warn(message, throwable);
    }

    /**
     * Determine if messages of priority "warn" will be logged.
     * 
     * @return true if "warn" messages will be logged.
     */
    public final boolean isWarnEnabled() {
        return m_logger.isWarnEnabled();
    }

    /**
     * Log a error message.
     * 
     * @param message the message.
     */
    public final void error(final String message) {
        m_logger.error(message);
    }

    /**
     * Log a error message.
     * 
     * @param message   the message.
     * @param throwable the throwable.
     */
    public final void error(final String message, final Throwable throwable) {
        m_logger.error(message, throwable);
    }

    /**
     * Determine if messages of priority "error" will be logged.
     * 
     * @return true if "error" messages will be logged.
     */
    public final boolean isErrorEnabled() {
        return m_logger.isErrorEnabled();
    }

    /**
     * Log a fatalError message.
     * 
     * @param message the message.
     */
    public final void fatalError(final String message) {
        m_logger.fatal(message);
    }

    /**
     * Log a fatalError message.
     * 
     * @param message   the message.
     * @param throwable the throwable.
     */
    public final void fatalError(final String message, final Throwable throwable) {
        m_logger.fatal(message, throwable);
    }

    /**
     * Determine if messages of priority "fatalError" will be logged.
     * 
     * @return true if "fatalError" messages will be logged.
     */
    public final boolean isFatalErrorEnabled() {
        return m_logger.isFatalEnabled();
    }

    /**
     * <p>Create a new child logger. The name of the child logger is set to be equal to the passed-in name.</p>
     * 
     * <p>Note that this method does <b>not</b> behave completely correctly as specified by the Logger contract, as the
     * commons-logging Log interface does not allow access to the name of the current logger. Hence use of this method
     * is <b>not</b> recommended.</p>
     * 
     * @throws org.apache.commons.logging.LogConfigurationException if the logger cannot be created. 
     * @param name the subname of this logger.
     * @return the new logger.
     */
    public final Logger getChildLogger(final String name) throws LogConfigurationException {
        return new CommonsLogger(LogFactory.getLog(name));
    }

    /**
     * Determine if messages of priority "fatalError" will be logged.
     * 
     * @return true if "fatalError" messages will be logged.
     */
    public boolean isFatalEnabled() {
        return m_logger.isFatalEnabled();
    }

    /**
     * Determine if messages of priority "trace" will be logged.
     * 
     * @return true if "trace" messages will be logged.
     */
    public boolean isTraceEnabled() {
        return m_logger.isTraceEnabled();
    }

    /**
     * Log a trace message.
     * 
     * @param o the message.
     */
    public final void trace(final Object o) {
        m_logger.trace(o);
    }

    /**
     * Log a trace message.
     * 
     * @param o         the message.
     * @param throwable the throwable.
     */
    public final void trace(final Object o, final Throwable throwable) {
        m_logger.trace(o, throwable);
    }

    /**
     * Log a debug message.
     * 
     * @param o the message.
     */
    public final void debug(final Object o) {
        m_logger.debug(o);
    }

    /**
     * Log a debug message.
     * 
     * @param o         the message.
     * @param throwable the throwable.
     */
    public final void debug(final Object o, final Throwable throwable) {
        m_logger.debug(o, throwable);
    }

    /**
     * Log an info message.
     * 
     * @param o the message
     */
    public final void info(final Object o) {
        m_logger.info(o);
    }

    /**
     * Log an info message.
     * 
     * @param o         the message.
     * @param throwable the throwable.
     */
    public final void info(final Object o, final Throwable throwable) {
        m_logger.info(o, throwable);
    }

    /**
     * Log a warning message.
     * 
     * @param o the message
     */
    public final void warn(final Object o) {
        m_logger.warn(o);
    }

    /**
     * Log a warning message.
     * 
     * @param o         the message.
     * @param throwable the throwable.
     */
    public final void warn(final Object o, final Throwable throwable) {
        m_logger.warn(o, throwable);
    }

    /**
     * Log an error message.
     * 
     * @param o the message.
     */
    public final void error(final Object o) {
        m_logger.error(o);
    }

    /**
     * Log an error message.
     * 
     * @param o         the message.
     * @param throwable the throwable.
     */
    public final void error(final Object o, final Throwable throwable) {
        m_logger.error(o, throwable);
    }

    /**
     * Log a fatal error message.
     * 
     * @param o the message.
     */
    public final void fatal(final Object o) {
        m_logger.fatal(o);
    }

    /**
     * Log a fatal error message.
     * 
     * @param o         the message.
     * @param throwable the throwable.
     */
    public final void fatal(final Object o, final Throwable throwable) {
        m_logger.fatal(o, throwable);
    }

}
