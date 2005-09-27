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

import org.nanocontainer.remoting.ThreadContext;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.common.NanoContainerRemotingRuntimeException;
import org.nanocontainer.remoting.server.ServerException;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.AbstractServerStreamReadWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class CompleteSocketObjectStreamServer
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public abstract class AbstractCompleteSocketStreamServer extends AbstractServer implements Runnable {

    /**
     * The server socket.
     */
    private ServerSocket m_serverSocket;

    /**
     * The thread handling the listening
     */
    private ThreadContext m_threadContext;
    private int m_port;

    /**
     * Construct a AbstractCompleteSocketStreamServer
     *
     * @param invocationHandlerAdapter The invocation handler adapter to use.
     * @param port                     The port to use
     * @param serverMonitor
     */
    public AbstractCompleteSocketStreamServer(InvocationHandlerAdapter invocationHandlerAdapter, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory, int port) {

        super(invocationHandlerAdapter, serverMonitor, threadPool, contextFactory);
        m_port = port;
    }

    /**
     * Method run
     */
    public void run() {

        boolean accepting = false;
        try {
            while (getState() == STARTED) {

                accepting = true;
                Socket sock = m_serverSocket.accept();
                accepting = false;

                // see http://developer.java.sun.com/developer/bugParade/bugs/4508149.html
                sock.setSoTimeout(36000);

                AbstractServerStreamReadWriter ssrw = createServerStreamReadWriter();

                ssrw.setStreams(sock.getInputStream(), sock.getOutputStream(), sock);

                SocketStreamServerConnection sssc = new SocketStreamServerConnection(this, sock, ssrw, m_serverMonitor);

                //TODO ? Two of these getThreadContexts? PH
                ThreadContext threadContext = getThreadPool().getThreadContext(sssc);

                threadContext.start();

            }
        } catch (IOException ioe) {
            if (accepting & ioe.getMessage().equals("socket closed")) {
                // do nothing, server shut down during accept();
            } else {

                m_serverMonitor.unexpectedException(this.getClass(), "AbstractCompleteSocketStreamServer.run(): Some problem connecting client " + "via sockets: ", ioe);
            }
        }
    }

    /**
     * Method start
     */
    public void start() throws ServerException {

        setState(STARTING);
        try {
            m_serverSocket = new ServerSocket(m_port);
        } catch (IOException ioe) {
            throw new ServerException("Could not bind to a socket when setting up the server", ioe);
        }
        setState(STARTED);
        getThreadContext().start();
    }

    /**
     * Method stop
     */
    public void stop() {

        if (getState() != STARTED) {
            throw new NanoContainerRemotingRuntimeException("Server Not Started at time of stop");
        }

        setState(SHUTTINGDOWN);
        try {
            m_serverSocket.close();
        } catch (IOException ioe) {
            throw new NanoContainerRemotingRuntimeException("Error stopping Complete Socket server", ioe);
        }
        killAllConnections();
        getThreadContext().interrupt();

        setState(STOPPED);
    }

    /**
     * Get the thread used for connection processing
     *
     * @return
     */
    private ThreadContext getThreadContext() {

        if (m_threadContext == null) {
            m_threadContext = getThreadPool().getThreadContext(this);

        }

        return m_threadContext;
    }

    /**
     * Create a Server Stream Read Writer.
     *
     * @return The Server Stream Read Writer.
     */
    protected abstract AbstractServerStreamReadWriter createServerStreamReadWriter();
}
