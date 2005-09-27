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

import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.AbstractServerStreamReadWriter;
import org.nanocontainer.remoting.server.transports.AbstractStreamServerConnection;

import java.io.IOException;
import java.net.Socket;

/**
 * Class SocketStreamServerConnection
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class SocketStreamServerConnection extends AbstractStreamServerConnection {

    /**
     * The socket for the connection
     */
    private Socket m_socket;

    /**
     * Construct a Socket Stream Server Connection
     *
     * @param abstractServer The Abstract Server that will process invocations and requests
     * @param socket         The Socket
     * @param readWriter     The readWriter for the transport type
     */
    public SocketStreamServerConnection(final AbstractServer abstractServer, final Socket socket, AbstractServerStreamReadWriter readWriter, ServerMonitor serverMonitor) {

        super(abstractServer, readWriter, serverMonitor);
        m_socket = socket;
    }

    /**
     * Kill connections
     */
    protected void killConnection() {

        try {
            m_socket.close();
        } catch (IOException e) {
            m_serverMonitor.closeError(this.getClass(), "SocketStreamServerConnection.killConnection(): Error closing Connection", e);
        }
    }
}
