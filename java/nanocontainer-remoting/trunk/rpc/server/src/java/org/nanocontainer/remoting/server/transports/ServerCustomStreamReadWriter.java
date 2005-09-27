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
import org.nanocontainer.remoting.common.BadConnectionException;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.SerializationHelper;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerMonitor;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Class ServerCustomStreamReadWriter
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class ServerCustomStreamReadWriter extends AbstractServerStreamReadWriter {

    private DataInputStream m_dataInputStream;
    private DataOutputStream m_dataOutputStream;

    public ServerCustomStreamReadWriter(ServerMonitor serverMonitor, ThreadPool threadPool) {
        super(serverMonitor, threadPool);
    }

    /**
     * Initialize
     *
     * @throws IOException if an IO Excpetion
     */
    protected void initialize() throws IOException {
        m_dataInputStream = new DataInputStream(getInputStream());
        m_dataOutputStream = new DataOutputStream(new BufferedOutputStream(getOutputStream()));
    }

    /**
     * Write a reply, and wait for a request
     *
     * @param reply The reply to send
     * @return The new request
     * @throws IOException            In an IO Exception
     * @throws ConnectionException    In an IO Exception
     * @throws ClassNotFoundException If a class not found during deserialization.
     */
    protected synchronized Request writeReplyAndGetRequest(Reply reply) throws IOException, ClassNotFoundException, ConnectionException {

        if (reply != null) {
            writeReply(reply);
        }

        return readRequest();
    }

    private void writeReply(Reply reply) throws IOException {

        byte[] aBytes = SerializationHelper.getBytesFromInstance(reply);

        m_dataOutputStream.writeInt(aBytes.length);
        m_dataOutputStream.write(aBytes);
        m_dataOutputStream.flush();
    }

    protected void close() {
        try {
            m_dataInputStream.close();
        } catch (IOException e) {
        }
        try {
            m_dataOutputStream.close();
        } catch (IOException e) {
        }
        super.close();
    }

    private Request readRequest() throws IOException, ClassNotFoundException, ConnectionException {
        int byteArraySize = m_dataInputStream.readInt();
        if (byteArraySize < 0) {
            throw new BadConnectionException("Transport mismatch, Unable to " + "read packet of data from CustomStream.");
        }
        byte[] byteArray = new byte[byteArraySize];
        int pos = 0;
        int cnt = 0;

        // Loop here until the entire array has been read in.
        while (pos < byteArraySize) {
            //TODO cater for DOS attack here.
            int read = m_dataInputStream.read(byteArray, pos, byteArraySize - pos);

            pos += read;

            cnt++;
        }

        /*
        if (cnt > 1)
        {
            System.out.println( "ServerCustomStreamReadWriter.readReply took " + cnt +
                " reads to read all, " + byteArraySize + ", required bytes." );
        }
        */
        return (Request) SerializationHelper.getInstanceFromBytes(byteArray);
    }
}
