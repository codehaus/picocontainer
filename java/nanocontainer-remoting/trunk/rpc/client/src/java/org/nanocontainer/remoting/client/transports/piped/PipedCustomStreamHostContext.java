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
package org.nanocontainer.remoting.client.transports.piped;

import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.pingers.DefaultConnectionPinger;
import org.nanocontainer.remoting.client.monitors.DumbClientMonitor;
import org.nanocontainer.remoting.client.factories.AbstractHostContext;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Class PipedCustomStreamHostContext
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class PipedCustomStreamHostContext extends AbstractHostContext
{

    /**
     * Constructor PipedCustomStreamHostContext
     *
     * @param threadPool
     * @param clientMonitor
     * @param connectionPinger
     * @param inputStream
     * @param outputStream
     */
    public PipedCustomStreamHostContext(ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger,
                                        PipedInputStream inputStream,
                                        PipedOutputStream outputStream)
    {
        super(new PipedCustomStreamInvocationHandler(threadPool, clientMonitor, connectionPinger, inputStream, outputStream));
    }

    public static class WithSimpleDefaults extends PipedCustomStreamHostContext {

        public WithSimpleDefaults(PipedInputStream inputStream, PipedOutputStream outputStream)
        {
            super(new DefaultThreadPool(), new DumbClientMonitor(), new DefaultConnectionPinger(), inputStream, outputStream);
        }
    }

    /**
     * Method initialize
     *
     *
     * @throws ConnectionException If a problem
     *
     */
    public void initialize() throws ConnectionException
    {
         m_invocationHandler.initialize();
    }
}
