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
 * A Complete Sockect Custom Stream Piped Instance.
 *
 * @author Paul Hammant
 */
public class CompleteSocketCustomStreamPipedConnection {

    private CompleteCustomStreamPipedServer m_pipedCustomStreamServer;
    private CompleteSocketCustomStreamPipedBinder m_completeSocketCustomStreamPipedBinder;

    /**
     * Create a Complete Socket CustomStream Piped Connection
     *
     * @param invocationHandlerAdapter the invocation adapter from the SocketCustomStream
     * @param completeSocketCustomStreamPipedBinder
     *                                 The binder that controls this connection
     * @param inputStream              the piped input stream
     * @param outputStream             the piped output stream
     * @throws ConnectionException if a problem
     */
    public CompleteSocketCustomStreamPipedConnection(InvocationHandlerAdapter invocationHandlerAdapter, CompleteSocketCustomStreamPipedBinder completeSocketCustomStreamPipedBinder, PipedInputStream inputStream, PipedOutputStream outputStream, ThreadPool threadPool, ServerSideClientContextFactory contextFactory, ServerMonitor serverMonitor) throws ConnectionException {
        m_pipedCustomStreamServer = new CompleteCustomStreamPipedServer(invocationHandlerAdapter, serverMonitor, threadPool, contextFactory);
        m_pipedCustomStreamServer.start();
        m_completeSocketCustomStreamPipedBinder = completeSocketCustomStreamPipedBinder;
        m_pipedCustomStreamServer.makeNewConnection(inputStream, outputStream);

    }

    /**
     * Close the connection
     */
    public void close() {
        m_completeSocketCustomStreamPipedBinder.endConnection(this);
    }

    /**
     * Stop the server.
     */
    protected void stop() {
        m_pipedCustomStreamServer.stop();
    }
}
