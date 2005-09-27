/* ====================================================================
 * Copyright 2005 NanoContainer Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.nanocontainer.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nanocontainer.remoting.client.transports;

import org.nanocontainer.remoting.client.ClientStreamReadWriter;
import org.nanocontainer.remoting.common.BadConnectionException;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Class ClientObjectStreamReadWriter
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class ClientObjectStreamReadWriter implements ClientStreamReadWriter
{

    private ObjectInputStream m_objectInputStream;
    private ObjectOutputStream m_objectOutputStream;

    /**
     * Constructor ClientObjectStreamReadWriter
     *
     *
     * @param inputStream
     * @param outputStream
     * @throws ConnectionException
     *
     */
    public ClientObjectStreamReadWriter(
        InputStream inputStream, OutputStream outputStream)
        throws ConnectionException
    {

        try
        {
            m_objectOutputStream = new ObjectOutputStream(outputStream);
            m_objectInputStream = new ObjectInputStream(inputStream);
        }
        catch(EOFException eofe)
        {
            throw new BadConnectionException( "Cannot connect to remote NanoContainer Remoting server. Have we a mismatch on transports?");
        }
        catch(IOException ioe)
        {
            throw new ConnectionException( "Some problem instantiating ObjectStream classes: " + ioe.getMessage() );
        }
    }

    public synchronized Reply postRequest( Request request )
        throws IOException, ClassNotFoundException
    {
        writeRequest( request );
        return readReply();
    }

    private void writeRequest( Request request ) throws IOException
    {

        m_objectOutputStream.writeObject( request );
        m_objectOutputStream.flush();

        //m_objectOutputStream.reset();
    }

    private Reply readReply() throws IOException, ClassNotFoundException
    {
        return (Reply)m_objectInputStream.readObject();
    }
}
