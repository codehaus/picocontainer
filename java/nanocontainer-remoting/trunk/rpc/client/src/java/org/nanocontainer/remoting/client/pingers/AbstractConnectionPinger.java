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
package org.nanocontainer.remoting.client.pingers;

import org.nanocontainer.remoting.ThreadContext;
import org.nanocontainer.remoting.client.ClientInvocationHandler;
import org.nanocontainer.remoting.client.ConnectionClosedException;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.InvocationException;

/**
 * Interface AbstractConnectionPinger
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public abstract class AbstractConnectionPinger implements ConnectionPinger {

    private ClientInvocationHandler m_clientInvocationHandler;
    private boolean m_continue = true;
    private Runnable m_runnable;
    private ThreadContext m_threadContext;
    private final long m_pingInterval;
    private final long m_giveupInterval;

    /**
     * Construct a AbstractConnectionPinger with seconds for interval.
     *
     * @param pingIntervalSeconds   the interval to wait
     * @param giveupIntervalSeconds when to give up
     */
    public AbstractConnectionPinger(int pingIntervalSeconds, int giveupIntervalSeconds) {
        m_pingInterval = pingIntervalSeconds * 1000;
        m_giveupInterval = giveupIntervalSeconds * 1000;
    }

    /**
     * Construct a AbstractConnectionPinger with millisecond intervals
     *
     * @param pingIntervalMilliSeconds   the interval to wait
     * @param giveupIntervalMilliSeconds when to give up
     */
    public AbstractConnectionPinger(long pingIntervalMilliSeconds, long giveupIntervalMilliSeconds) {
        m_pingInterval = pingIntervalMilliSeconds;
        m_giveupInterval = giveupIntervalMilliSeconds;
    }


    /**
     * Constructor AbstractConnectionPinger
     */
    public AbstractConnectionPinger() {
        m_pingInterval = 10 * 1000;       // ten seconds
        m_giveupInterval = 100 * 1000;    // one hundred seconds.
    }

    /**
     * Method setInvocationHandler
     */
    public void setInvocationHandler(ClientInvocationHandler invocationHandler) {
        m_clientInvocationHandler = invocationHandler;
    }

    /**
     * Get the Invocation handler.
     *
     * @return
     */
    protected ClientInvocationHandler getInvocationHandler() {
        return m_clientInvocationHandler;
    }

    /**
     * Start the pinger
     */
    public void start() {

        m_runnable = new Runnable() {
            public void run() {

                try {
                    while (m_continue) {
                        Thread.sleep(m_pingInterval);
                        if (m_continue) {
                            ping();
                        }
                    }
                } catch (InvocationException ie) {
                    m_clientInvocationHandler.getClientMonitor().invocationFailure(this.getClass(), this.getClass().getName(), ie);
                    // no need to ping anymore.
                } catch (ConnectionClosedException cce) {
                    m_clientInvocationHandler.getClientMonitor().unexpectedClosedConnection(this.getClass(), this.getClass().getName(), cce);
                    // no need to ping anymore.
                } catch (InterruptedException e) {
                    if (m_continue) {
                        m_clientInvocationHandler.getClientMonitor().unexpectedInterruption(this.getClass(), this.getClass().getName(), e);
                    }
                }
            }
        };

        m_threadContext = m_clientInvocationHandler.getThreadPool().getThreadContext(m_runnable);
        m_threadContext.start();
    }

    /**
     * Stop the pinger
     */
    public void stop() {
        m_continue = false;
        m_threadContext.interrupt();
        m_threadContext = null;
    }

    protected abstract void ping();

    public long getGiveupInterval() {
        return m_giveupInterval;
    }
}
