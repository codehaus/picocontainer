/* ====================================================================
 * Copyright 2005 NanoContainer Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nanocontainer.remoting.server.transports.socket;

import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;


/**
 * A Complete Sockect Object Stream Piped Connection.
 *
 * @author Paul Hammant
 */
public class CompleteSocketObjectStreamPipedConnection {

    private CompleteCustomStreamPipedServer m_completeCustomStreamPipedServer;
    private CompleteSocketObjectStreamPipedBinder m_completeSocketObjectStreamPipedBinder;

    /**
     * Create a Complete Socket ObjectStream Piped Connection
     *
     * @param invocationHandlerAdapter the invocation adapter from the SocketObjectStream
     * @param completeSocketObjectStreamPipedBinder
     *                                 The binder that controls this connection
     * @param inputStream              the piped input stream
     * @param outputStream             the piped output stream
     * @throws ConnectionException if a problem
     */
    public CompleteSocketObjectStreamPipedConnection(InvocationHandlerAdapter invocationHandlerAdapter, CompleteSocketObjectStreamPipedBinder completeSocketObjectStreamPipedBinder, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory, PipedInputStream inputStream, PipedOutputStream outputStream) throws ConnectionException {
        m_completeCustomStreamPipedServer = new CompleteCustomStreamPipedServer(invocationHandlerAdapter, serverMonitor, threadPool, contextFactory);
        m_completeSocketObjectStreamPipedBinder = completeSocketObjectStreamPipedBinder;
        m_completeCustomStreamPipedServer.makeNewConnection(inputStream, outputStream);
        m_completeCustomStreamPipedServer.start();
    }

    /**
     * Close the connection
     */
    public void close() {
        m_completeSocketObjectStreamPipedBinder.endConnection(this);
    }

    /**
     * Stop the server.
     */
    protected void stop() {
        m_completeCustomStreamPipedServer.stop();
    }
}

