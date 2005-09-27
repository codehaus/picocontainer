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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.nanocontainer.remoting.client.ClientStreamReadWriter;
import org.nanocontainer.remoting.client.transports.AbstractStreamClientInvocationHandler;
import org.nanocontainer.remoting.client.transports.AbstractStreamClientInvocationHandler;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.InvocationException;
import org.nanocontainer.remoting.client.*;

/**
 * Class AbstractPipedStreamInvocationHandler
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public abstract class AbstractPipedStreamInvocationHandler extends AbstractStreamClientInvocationHandler
{

    private PipedInputStream m_inputStream;
    private PipedOutputStream m_outputStream;

    /**
     * Constructor AbstractPipedStreamInvocationHandler
     *
     * @param threadPool
     * @param clientMonitor
     * @param connectionPinger
     * @param is
     * @param os
     * @param interfacesClassLoader
     */
    public AbstractPipedStreamInvocationHandler( ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger,
        PipedInputStream is, PipedOutputStream os, ClassLoader interfacesClassLoader )
    {

        super( threadPool, clientMonitor, connectionPinger, interfacesClassLoader );

        m_inputStream = is;
        m_outputStream = os;
    }

    /**
     * Method initialize
     *
     *
     * @throws ConnectionException
     *
     */
    public void initialize() throws ConnectionException
    {
        setObjectReadWriter( createClientStreamReadWriter( m_inputStream, m_outputStream ) );
        super.initialize();
    }

    protected boolean tryReconnect()
    {

        // blimey how do we reconnect this?
        throw new InvocationException( "Piped connection broken, unable to reconnect." );
    }

    protected abstract ClientStreamReadWriter createClientStreamReadWriter(
        InputStream in, OutputStream out ) throws ConnectionException;

}
