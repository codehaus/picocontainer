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
package org.nanocontainer.remoting.client.transports.direct;

import java.io.IOException;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.commands.RequestFailedReply;
import org.nanocontainer.remoting.common.SerializationHelper;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.server.ServerMarshalledInvocationHandler;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.ConnectionPinger;

/**
 * Class DirectInvocationHandler
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public final class DirectMarshalledInvocationHandler extends AbstractDirectInvocationHandler
{

    private ServerMarshalledInvocationHandler m_invocationHandler;
    private ClassLoader m_interfacesClassLoader;


    /**
     * Constructor DirectInvocationHandler
     *
     * @param threadPool
     * @param clientMonitor
     * @param connectionPinger
     * @param invocationHandler
     */
    public DirectMarshalledInvocationHandler(ThreadPool threadPool, ClientMonitor clientMonitor,
                                             ConnectionPinger connectionPinger,
                                             ServerMarshalledInvocationHandler invocationHandler,
                                             ClassLoader classLoader)
    {
        super(threadPool, clientMonitor, connectionPinger);
        m_invocationHandler = invocationHandler;
        m_interfacesClassLoader = classLoader;
    }

    protected Reply performInvocation( Request request ) throws IOException
    {

        try
        {
            byte[] serRequest = SerializationHelper.getBytesFromInstance( request );
            byte[] serReply = m_invocationHandler.handleInvocation( serRequest, null );

            Object instanceFromBytes = SerializationHelper.getInstanceFromBytes( serReply,
                                                                                      m_interfacesClassLoader );
            return (Reply)instanceFromBytes;
        }
        catch( ClassNotFoundException e )
        {
            e.printStackTrace();

            return new RequestFailedReply( "Some Class not found Exception on server side : "
                                           + e.getMessage() );
        }
    }

    /**
     * Method getInterfacesClassLoader
     *
     *
     * @return
     *
     */
    public ClassLoader getInterfacesClassLoader()
    {
        return m_interfacesClassLoader;
    }

}
