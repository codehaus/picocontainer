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
import org.nanocontainer.remoting.registry.BindException;
import org.nanocontainer.remoting.registry.Binder;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Vector;


/**
 * A Complete Socket Custom Stream Piped Binder
 *
 * @author Paul Hammant
 */
public class CompleteSocketCustomStreamPipedBinder implements Binder {

    private InvocationHandlerAdapter m_invocationHandlerAdapter;
    private final ServerMonitor m_serverMonitor;
    private final ThreadPool m_threadPool;
    private final ServerSideClientContextFactory m_contextFactory;
    private Vector m_connections = new Vector();

    /**
     * Construuct a Piped Binder
     *
     * @param invocationHandlerAdapter An invocation handler adapter to handle requests
     */
    public CompleteSocketCustomStreamPipedBinder(InvocationHandlerAdapter invocationHandlerAdapter, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory) {
        m_invocationHandlerAdapter = invocationHandlerAdapter;
        m_serverMonitor = serverMonitor;
        m_threadPool = threadPool;
        m_contextFactory = contextFactory;
    }

    /**
     * Bind to an piped stream
     *
     * @param bindParms the piped input stream and piped output stream
     * @return thebound object
     * @throws org.nanocontainer.remoting.registry.BindException
     *          if a problem
     */
    public Object bind(Object[] bindParms) throws BindException {
        PipedInputStream inputStream = (PipedInputStream) bindParms[0];
        PipedOutputStream outputStream = (PipedOutputStream) bindParms[1];
        try {
            Object connection = new CompleteSocketCustomStreamPipedConnection(m_invocationHandlerAdapter, this, inputStream, outputStream, m_threadPool, m_contextFactory, m_serverMonitor);
            m_connections.add(connection);
            return connection;
        } catch (ConnectionException e) {
            throw new BindException("Problem binding: " + e.getMessage());
        }
    }

    /**
     * Stop the server
     */
    public void stop() {
        for (int i = 0; i < m_connections.size(); i++) {
            CompleteSocketCustomStreamPipedConnection completeSocketCustomStreamPipedConnection = (CompleteSocketCustomStreamPipedConnection) m_connections.elementAt(i);
            completeSocketCustomStreamPipedConnection.stop();

        }
    }

    /**
     * End a connection
     *
     * @param connection the connection
     */
    void endConnection(CompleteSocketCustomStreamPipedConnection connection) {
        for (int i = 0; i < m_connections.size(); i++) {
            CompleteSocketCustomStreamPipedConnection completeSocketCustomStreamPipedConnection = (CompleteSocketCustomStreamPipedConnection) m_connections.elementAt(i);
            if (connection == completeSocketCustomStreamPipedConnection) {
                connection.stop();
            }
        }
    }
}
