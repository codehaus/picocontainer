/* ====================================================================
 * Copyright 2005 NanoContainer Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.nanocontainer.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nanocontainer.remoting.client.monitors;

import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionClosedException;
import org.nanocontainer.remoting.client.InvocationException;
import org.nanocontainer.remoting.commands.Request;

import java.io.IOException;

/**
 * Interface DefaultClientMonitor
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public class DefaultClientMonitor implements ClientMonitor {

    private int m_maxReconnectAttempts;

    /**
     * Creates a new DefaultClientMonitor.
     */
    public DefaultClientMonitor() {
        this(3);  // Default to 3 reconnect attempts.
    }

    /**
     * Creates a new DefaultClientMonitor.
     *
     * @param maxReconnectAttempts Specifies the maximum number of times that
     *                             the client will attempt to reconnect to
     *                             the server if the connection is lost.  A
     *                             value of 0 implies that no reconnect
     *                             attempts should be made.
     */
    public DefaultClientMonitor(int maxReconnectAttempts) {
        m_maxReconnectAttempts = maxReconnectAttempts;
    }

    /**
     * Method methodCalled
     *
     * @param methodSignature
     * @param duration
     */
    public void methodCalled(Class clazz, final String methodSignature, final long duration, String annotation) {

        // do mothing in default impl, could do logging.
    }

    /**
     * Method methodLogging tests if the implementing class intends to do method logging.
     *
     * @return
     */
    public boolean methodLogging() {
        return false;
    }

    /**
     * Method serviceSuspended
     *
     * @param request
     * @param attempt
     * @param suggestedWaitMillis
     */
    public void serviceSuspended(Class clazz, final Request request, final int attempt, final int suggestedWaitMillis) {

        // Lets say that ten retries is too many.
        if (attempt == 10) {
            throw new InvocationException("Too many retries on suspended service");
        }

        printMessage("NanoContainer Remoting service suspended, Trying to reconnect (attempt " + attempt + ", waiting for " + suggestedWaitMillis / 1000 + " seconds)");

        // We are quite happy with the recommended wait time.
        try {
            Thread.sleep(suggestedWaitMillis);
        } catch (InterruptedException ie) {
            unexpectedInterruption(this.getClass(), this.getClass().getName(), ie);
        }
    }

    /**
     * Method serviceAbend
     *
     * @param attempt
     */
    public void serviceAbend(Class clazz, int attempt, IOException cause) {

        // Lets say that ten retries is too many.
        if (attempt >= m_maxReconnectAttempts) {
            String msg;
            if (m_maxReconnectAttempts <= 0) {
                msg = "Reconnect to abended service disabled.";
            } else {
                msg = "Too many retries on abended service. ";
                if (cause != null) {
                    msg = msg + "Possible cause of abend (exception=" + cause.getClass().getName() + "). ";
                    if (cause.getMessage() != null) {
                        msg = msg + "Message= '" + cause.getMessage() + "'";
                    } else {
                        msg = msg + "No Message in exception.";
                    }
                } else {
                    msg = msg + "Unknown cause of abend.";
                }
            }
            throw new InvocationException(msg);
        }

        printMessage("NanoContainer Remoting service abnormally ended, Trying to reconnect (attempt " + attempt + ")");

        // Increasing wait time.
        try {
            Thread.sleep((2 ^ attempt) * 500);
        } catch (InterruptedException ie) {
            unexpectedInterruption(this.getClass(), this.getClass().getName(), ie);
        }
    }

    public void invocationFailure(Class clazz, String name, InvocationException ie) {
    }

    public void unexpectedClosedConnection(Class clazz, String name, ConnectionClosedException cce) {
    }

    public void unexpectedInterruption(Class clazz, String name, InterruptedException ie) {
    }

    void printMessage(String message) {
        System.out.println(message);
    }

}
