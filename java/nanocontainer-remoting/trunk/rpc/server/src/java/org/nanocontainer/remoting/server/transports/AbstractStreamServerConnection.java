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
package org.nanocontainer.remoting.server.transports;

import org.nanocontainer.remoting.commands.EndConnectionReply;
import org.nanocontainer.remoting.commands.InvocationExceptionReply;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.common.BadConnectionException;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.server.ServerConnection;
import org.nanocontainer.remoting.server.ServerMonitor;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;

/**
 * Class AbstractStreamServerConnection
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public abstract class AbstractStreamServerConnection implements Runnable, ServerConnection {

    /**
     * The Abstract Server
     */
    private AbstractServer m_abstractServer;

    /**
     * End connections
     */
    private boolean m_endConnection = false;

    /**
     * The Sever Stream Read Writer.
     */
    private AbstractServerStreamReadWriter m_readWriter;

    protected final ServerMonitor m_serverMonitor;

    /**
     * Construct a AbstractStreamServerConnection
     *
     * @param abstractServer The Abstract Server handling requests
     * @param readWriter     The Read Writer.
     */
    public AbstractStreamServerConnection(AbstractServer abstractServer, AbstractServerStreamReadWriter readWriter, ServerMonitor serverMonitor) {
        m_abstractServer = abstractServer;
        m_readWriter = readWriter;
        m_serverMonitor = serverMonitor;
    }


    /**
     * Method run
     */
    public void run() {

        m_abstractServer.connectionStart(this);

        try {

            m_readWriter.initialize();

            boolean more = true;
            Request request = null;
            Reply reply = null;
            while (more) {
                try {
                    if (request != null) {
                        reply = m_abstractServer.handleInvocation(request, m_readWriter.getConnectionDetails());
                    }

                    request = m_readWriter.writeReplyAndGetRequest(reply);

                    // http://developer.java.sun.com/developer/bugParade/bugs/4499841.html
                    // halves the performance though.
                    //oOS.reset();
                    if (m_endConnection) {
                        reply = new EndConnectionReply();
                        more = false;
                    }
                } catch (BadConnectionException bce) {
                    more = false;
                    m_serverMonitor.badConnection(this.getClass(), "AbstractStreamServerConnection.run(): Bad connection #0", bce);
                    m_readWriter.close();
                } catch (ConnectionException ace) {
                    more = false;
                    m_serverMonitor.unexpectedException(this.getClass(), "AbstractStreamServerConnection.run(): Unexpected ConnectionException #0", ace);
                    m_readWriter.close();
                } catch (IOException ioe) {
                    more = false;

                    if (ioe instanceof EOFException) {
                        m_readWriter.close();
                    } else if (isSafeEnd(ioe)) {

                        // TODO implement implementation indepandant logger
                        m_readWriter.close();
                    } else {
                        m_serverMonitor.unexpectedException(this.getClass(), "AbstractStreamServerConnection.run(): Unexpected IOE #1", ioe);
                        m_readWriter.close();
                    }
                } catch (NullPointerException npe) {
                    m_serverMonitor.unexpectedException(this.getClass(), "AbstractStreamServerConnection.run(): Unexpected NPE", npe);
                    reply = new InvocationExceptionReply("NullPointerException on server: " + npe.getMessage());
                }
            }
        } catch (IOException e) {
            m_serverMonitor.unexpectedException(this.getClass(), "AbstractStreamServerConnection.run(): Unexpected IOE #2", e);
        } catch (ClassNotFoundException e) {
            m_serverMonitor.classNotFound(this.getClass(), e);
        }

        m_abstractServer.connectionCompleted(this);
    }

    private boolean isSafeEnd(IOException ioe) {
        if ((ioe instanceof SocketException) || ioe.getClass().getName().equals("java.net.SocketTimeoutException") // 1.3 safe
                || (ioe instanceof InterruptedIOException)) {
            return true;
        }

        if (ioe.getMessage() != null) {
            String msg = ioe.getMessage();
            if (msg.equals("Write end dead") | msg.equals("Pipe broken") | msg.equals("Pipe closed")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method endConnection
     */
    public void endConnection() {
        m_endConnection = true;
        m_readWriter.close();
    }

    /**
     * Method killConnection
     */
    protected abstract void killConnection();

}
