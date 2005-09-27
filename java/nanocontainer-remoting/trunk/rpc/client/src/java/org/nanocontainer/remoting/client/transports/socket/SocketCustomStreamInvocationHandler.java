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
package org.nanocontainer.remoting.client.transports.socket;

import java.io.InputStream;
import java.io.OutputStream;
import org.nanocontainer.remoting.client.transports.ClientCustomStreamReadWriter;
import org.nanocontainer.remoting.client.transports.ClientCustomStreamReadWriter;
import org.nanocontainer.remoting.client.ClientStreamReadWriter;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.client.ClientMonitor;

/**
 * Class SocketCustomStreamInvocationHandler
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public final class SocketCustomStreamInvocationHandler
    extends AbstractSocketStreamInvocationHandler
{

    /**
     * Constructor SocketCustomStreamInvocationHandler
     *
     *
     * @param host the host name
     * @param port the port
     * @param interfacesClassLoader the classloader for deserialization hints.
     *
     * @throws ConnectionException if a problem
     *
     */
    public SocketCustomStreamInvocationHandler( ThreadPool threadPool, ClientMonitor clientMonitor,
                                                ConnectionPinger connectionPinger,
                                                ClassLoader interfacesClassLoader,
                                                String host, int port )
        throws ConnectionException
    {
        super( threadPool, clientMonitor, connectionPinger, interfacesClassLoader, host, port );
    }

    /**
     * Create a client stream read/writer
     * @param in the input stream
     * @param out the output stream
     * @return the read/writer
     * @throws ConnectionException if a problem
     */
    protected ClientStreamReadWriter createClientStreamReadWriter(
        InputStream in, OutputStream out ) throws ConnectionException
    {
        return new ClientCustomStreamReadWriter( in, out, m_interfacesClassLoader );
    }
}
