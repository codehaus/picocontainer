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

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;

import org.nanocontainer.remoting.client.ClientStreamReadWriter;
import org.nanocontainer.remoting.client.InvocationException;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.client.NoSuchReferenceException;
import org.nanocontainer.remoting.client.NotPublishedException;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.commands.NotPublishedReply;
import org.nanocontainer.remoting.commands.PublishedNameRequest;
import org.nanocontainer.remoting.commands.TryLaterReply;
import org.nanocontainer.remoting.commands.RequestConstants;
import org.nanocontainer.remoting.commands.ReplyConstants;
import org.nanocontainer.remoting.commands.NoSuchSessionReply;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.client.NoSuchSessionException;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.*;
import org.nanocontainer.remoting.commands.ClientInvocationAbendReply;
import org.nanocontainer.remoting.commands.NoSuchReferenceReply;
import org.nanocontainer.remoting.commands.*;

import javax.swing.*;

/**
 * Class AbstractStreamClientInvocationHandler
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public abstract class AbstractStreamClientInvocationHandler extends AbstractClientInvocationHandler
{

    private ClientStreamReadWriter m_objectReadWriter;
    private boolean m_methodLogging = false;
    private long m_lastRealRequest = System.currentTimeMillis();
    protected final ClassLoader m_interfacesClassLoader;

    /**
     * Constructor AbstractStreamClientInvocationHandler
     *
     * @param threadPool
     * @param clientMonitor
     * @param connectionPinger
     * @param interfacesClassLoader
     */
    public AbstractStreamClientInvocationHandler(ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger, ClassLoader interfacesClassLoader)
    {
        super(threadPool, clientMonitor, connectionPinger);
        m_interfacesClassLoader = interfacesClassLoader;
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

    protected void setObjectReadWriter( ClientStreamReadWriter objectReadWriter )
    {
        m_objectReadWriter = objectReadWriter;
    }

    protected void requestWritten()
    {
    }

    /**
     * Method handleInvocation
     *
     *
     * @param request
     *
     * @return
     *
     */
    public synchronized Reply handleInvocation( Request request )
    {
        if( request.getRequestCode() != RequestConstants.PINGREQUEST )
        {
            m_lastRealRequest = System.currentTimeMillis();
        }

        try
        {
            while( true )
            {
                boolean again = true;
                Reply reply = null;
                int tries = 0;
                long start = 0;

                if( m_methodLogging )
                {
                    start = System.currentTimeMillis();
                }

                while( again )
                {
                    tries++;

                    again = false;

                    try
                    {
                        long t1 = System.currentTimeMillis();

                        reply = (Reply)m_objectReadWriter.postRequest( request );

                        long t2 = System.currentTimeMillis();

                        if( reply.getReplyCode() >= 100 )
                        {
                            // special case for callabcks.
                            if (reply.getReplyCode() == ReplyConstants.CLIENTABEND)
                            {
                                ClientInvocationAbendReply abendReply = (ClientInvocationAbendReply) reply;
                                throw abendReply.getIOException();
                            }

                            if( reply instanceof TryLaterReply )
                            {
                                int millis = ( (TryLaterReply)reply ).getSuggestedDelayMillis();

                                m_clientMonitor.serviceSuspended(this.getClass(), request, tries,
                                                                            millis );

                                again = true;
                            }
                            else if( reply instanceof NoSuchReferenceReply )
                            {
                                throw new NoSuchReferenceException( ( (NoSuchReferenceReply)reply )
                                                                    .getReferenceID() );
                            }
                            else if( reply instanceof NoSuchSessionReply )
                            {
                                throw new NoSuchSessionException( ( (NoSuchSessionReply)reply )
                                                                    .getSessionID() );
                            }
                            else if( reply instanceof NotPublishedReply )
                            {
                                PublishedNameRequest pnr = (PublishedNameRequest)request;

                                throw new NotPublishedException( pnr.getPublishedServiceName(),
                                                                 pnr.getObjectName() );
                            }
                        }
                    }
                    catch( IOException ioe )
                    {
                        if(isSafeEnd(ioe))
                        {
                            int retryConnectTries = 0;

                            again = true;

                            while( !tryReconnect() )
                            {
                                m_clientMonitor.serviceAbend(this.getClass(), retryConnectTries, ioe);

                                retryConnectTries++;
                            }
                        }
                        else
                        {
                            ioe.printStackTrace();

                            throw new InvocationException(
                                "IO Exception during invocation to server :" + ioe.getMessage() );
                        }
                    }
                }
                if( m_methodLogging )
                {
                    if( request instanceof MethodRequest )
                    {
                        m_clientMonitor.methodCalled(
                                this.getClass(), ( (MethodRequest)request ).getMethodSignature(),
                            System.currentTimeMillis() - start, "" );
                    }
                }

                return reply;
            }
        }
        catch( ClassNotFoundException e )
        {
            throw new InvocationException( "Class definition missing on Deserialization: "
                                                 + e.getMessage() );
        }
    }

    private boolean isSafeEnd(IOException ioe) {
        if (ioe instanceof SocketException | ioe instanceof EOFException
                            | ioe instanceof InterruptedIOException) {
                                return true;
                            }
        if (ioe.getMessage() != null) {
            String msg = ioe.getMessage();
            if (msg.equals("Read end dead") | msg.equals("Pipe closed")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method getLastRealRequest
     *
     *
     * @return
     *
     */
    public long getLastRealRequest()
    {
        return m_lastRealRequest;
    }
}
