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

import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerMonitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class ServerObjectStreamReadWriter
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class ServerObjectStreamReadWriter extends AbstractServerStreamReadWriter {

    /**
     * The Object Input Stream
     */
    private ObjectInputStream m_objectInputStream;

    /**
     * The Object Output Stream
     */
    private ObjectOutputStream m_objectOutputStream;

    /**
     * Constructor ServerObjectStreamReadWriter
     */
    public ServerObjectStreamReadWriter(ServerMonitor serverMonitor, ThreadPool threadPool) {
        super(serverMonitor, threadPool);
    }

    /**
     * Initialize
     *
     * @throws IOException if an IO Excpetion
     */
    protected void initialize() throws IOException {
        m_objectInputStream = new ObjectInputStream(getInputStream());
        m_objectOutputStream = new ObjectOutputStream(getOutputStream());
    }

    /**
     * Write a reply, and wait for a request
     *
     * @param reply The reply to send
     * @return The new request
     * @throws IOException            In an IO Exception
     * @throws ClassNotFoundException If a class not found during deserialization.
     */
    protected synchronized Request writeReplyAndGetRequest(Reply reply) throws IOException, ClassNotFoundException {

        if (reply != null) {
            writeReply(reply);
        }

        return readRequest();
    }

    /**
     * Write a rpely.
     *
     * @param reply The reply to write
     * @throws IOException If and IO Exception
     */
    private void writeReply(Reply reply) throws IOException {

        m_objectOutputStream.writeObject(reply);
        m_objectOutputStream.flush();

        //m_objectOutputStream.reset();
    }

    protected void close() {
        try {
            m_objectInputStream.close();
        } catch (IOException e) {
        }
        try {
            m_objectOutputStream.close();
        } catch (IOException e) {
        }
        super.close();
    }

    /**
     * Read a request
     *
     * @return The request
     * @throws IOException            If an IO Exception
     * @throws ClassNotFoundException If a class not found during deserialization.
     */
    private Request readRequest() throws IOException, ClassNotFoundException {
        return (Request) m_objectInputStream.readObject();
    }
}
