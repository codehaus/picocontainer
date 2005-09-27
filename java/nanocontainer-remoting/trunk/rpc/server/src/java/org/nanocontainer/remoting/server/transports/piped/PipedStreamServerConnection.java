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
package org.nanocontainer.remoting.server.transports.piped;

import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.AbstractServerStreamReadWriter;
import org.nanocontainer.remoting.server.transports.AbstractStreamServerConnection;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Class PipedStreamServerConnection
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class PipedStreamServerConnection extends AbstractStreamServerConnection {

    /**
     * The piped input stream.
     */
    private PipedInputStream m_pipedIn;

    /**
     * The piped output stream.
     */
    private PipedOutputStream m_pipedOut;

    /**
     * Construct a PipedStreamServerConnection
     *
     * @param abstractServer The asbtract server than handles requests
     * @param pipedIn        The piped Input Stream
     * @param pipedOut       The piped Output Stream
     * @param readWriter     The read writer.
     */
    public PipedStreamServerConnection(AbstractServer abstractServer, PipedInputStream pipedIn, PipedOutputStream pipedOut, AbstractServerStreamReadWriter readWriter, ServerMonitor serverMonitor) {

        super(abstractServer, readWriter, serverMonitor);

        m_pipedIn = pipedIn;
        m_pipedOut = pipedOut;
    }

    /**
     * Kill connections
     */
    protected void killConnection() {

        try {
            m_pipedIn.close();
        } catch (IOException e) {
            m_serverMonitor.closeError(this.getClass(), "PipedStreamServerConnection.killConnection(): Some problem during closing of Input Stream", e);
        }

        try {
            m_pipedOut.close();
        } catch (IOException e) {
            m_serverMonitor.closeError(this.getClass(), "PipedStreamServerConnection.killConnection(): Some problem during closing of Output Stream", e);
        }
    }
}
