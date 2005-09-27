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

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.common.SerializationHelper;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.client.ClientStreamReadWriter;
import org.nanocontainer.remoting.client.ClientStreamReadWriter;

/**
 * Class ClientCustomStreamReadWriter
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.3 $
 */
public class ClientCustomStreamReadWriter implements ClientStreamReadWriter
{

    private DataInputStream m_dataInputStream;
    private DataOutputStream m_dataOutputStream;
    private ClassLoader m_interfacesClassLoader;



    /**
     * Constructor ClientCustomStreamReadWriter
     *
     *
     * @param inputStream
     * @param outputStream
     * @param interfacesClassLoader
     *
     * @throws ConnectionException
     *
     */
    public ClientCustomStreamReadWriter(
        InputStream inputStream, OutputStream outputStream, ClassLoader interfacesClassLoader )
        throws ConnectionException
    {

        m_dataOutputStream = new DataOutputStream( new BufferedOutputStream( outputStream ) );
        m_dataInputStream = new DataInputStream( inputStream );
        m_interfacesClassLoader = interfacesClassLoader;
    }

    public synchronized Reply postRequest( Request request )
        throws IOException, ClassNotFoundException
    {

        writeRequest( request );

        Reply r = readReply();

        return r;
    }

    private void writeRequest( Request request ) throws IOException
    {

        byte[] aBytes = SerializationHelper.getBytesFromInstance( request );

        m_dataOutputStream.writeInt( aBytes.length );
        m_dataOutputStream.write( aBytes );
        m_dataOutputStream.flush();
    }

    private Reply readReply() throws IOException, ClassNotFoundException
    {

        int byteArraySize = m_dataInputStream.readInt();
        byte[] byteArray = new byte[ byteArraySize ];
        int pos = 0;
        int cnt = 0;
        // Loop here until the entire array has been read in.
        while( pos < byteArraySize )
        {
            int read = m_dataInputStream.read( byteArray, pos, byteArraySize - pos );
            pos += read;
            cnt++;
        }
        Object reply = SerializationHelper.getInstanceFromBytes( byteArray,
                                                                      m_interfacesClassLoader );
        return (Reply) reply;
    }
}
