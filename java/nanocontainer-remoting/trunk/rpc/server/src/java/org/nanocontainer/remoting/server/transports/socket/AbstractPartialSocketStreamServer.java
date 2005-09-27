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

import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.AbstractServerStreamReadWriter;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Peter Royal
 * @version $Revision: 1.2 $
 */
public abstract class AbstractPartialSocketStreamServer extends AbstractServer {

    /**
     * Construct a AbstractPartialSocketStreamServer
     *
     * @param invocationHandlerAdapter Use this invocation handler adapter.
     * @param serverMonitor            The server Monitor
     */
    public AbstractPartialSocketStreamServer(InvocationHandlerAdapter invocationHandlerAdapter, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory) {
        super(invocationHandlerAdapter, serverMonitor, threadPool, contextFactory);
    }

    /**
     * Handle a connection.
     *
     * @param socket The socket for the connection
     */
    public void handleConnection(final Socket socket) {

        // see http://developer.java.sun.com/developer/bugParade/bugs/4508149.html
        try {
            socket.setSoTimeout(36000);
        } catch (SocketException se) {
            m_serverMonitor.unexpectedException(this.getClass(), "AbstractPartialSocketStreamServer.handleConnection(): Some error during " + "socket handling", se);
        }

        try {
            if (getState() == STARTED) {
                AbstractServerStreamReadWriter ssrw = createServerStreamReadWriter();

                ssrw.setStreams(socket.getInputStream(), socket.getOutputStream(), socket);

                SocketStreamServerConnection sssc = new SocketStreamServerConnection(this, socket, ssrw, m_serverMonitor);

                sssc.run();
            }
        } catch (IOException ioe) {

            m_serverMonitor.unexpectedException(this.getClass(), "AbstractPartialSocketStreamServer.handleConnection(): Some problem connecting " + "client via sockets: ", ioe);
        }
    }

    /**
     * Create a Server Stream Read Writer.
     *
     * @return The Server Stream Read Writer.
     */
    protected abstract AbstractServerStreamReadWriter createServerStreamReadWriter();

    /**
     * Method start
     */
    public void start() {
        setState(STARTED);
    }

    /**
     * Method stop
     */
    public void stop() {

        setState(SHUTTINGDOWN);

        killAllConnections();

        setState(STOPPED);
    }
}
