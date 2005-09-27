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
package org.nanocontainer.remoting.client.transports.rmi;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.client.NotPublishedException;
import org.nanocontainer.remoting.commands.PublishedNameRequest;
import org.nanocontainer.remoting.commands.TryLaterReply;
import org.nanocontainer.remoting.commands.RequestConstants;
import org.nanocontainer.remoting.common.BadConnectionException;
import org.nanocontainer.remoting.common.RmiInvocationHandler;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.*;
import org.nanocontainer.remoting.client.transports.AbstractClientInvocationHandler;
import org.nanocontainer.remoting.client.transports.AbstractClientInvocationHandler;
import org.nanocontainer.remoting.commands.MethodRequest;
import org.nanocontainer.remoting.commands.NotPublishedReply;
import org.nanocontainer.remoting.commands.*;

/**
 * Class RmiClientInvocationHandler
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public final class RmiClientInvocationHandler extends AbstractClientInvocationHandler
{

    private RmiInvocationHandler m_rmiInvocationHandler;
    private String m_URL;
    private long m_lastRealRequest = System.currentTimeMillis();

    /**
     * Constructor RmiClientInvocationHandler
     *
     *
     * @param host
     * @param port
     *
     * @throws ConnectionException
     *
     */
    public RmiClientInvocationHandler( ThreadPool threadPool, ClientMonitor clientMonitor,
                                       ConnectionPinger connectionPinger,
                                       String host, int port ) throws ConnectionException
    {

        super(threadPool, clientMonitor, connectionPinger);

        m_URL = "rmi://" + host + ":" + port + "/" + RmiInvocationHandler.class.getName();

        try
        {
            m_rmiInvocationHandler = (RmiInvocationHandler)Naming.lookup( m_URL );
        }
        catch( NotBoundException nbe )
        {
            throw new ConnectionException(
                "Cannot bind to the remote RMI service.  Either an IP or RMI issue." );
        }
        catch( MalformedURLException mfue )
        {
            throw new ConnectionException( "Malformed URL, host/port (" + host + "/" + port
                                                 + ") must be wrong: " + mfue.getMessage() );
        }
        catch( ConnectIOException cioe )
        {
            throw new BadConnectionException( "Cannot connect to remote RMI server. "
                    + "It is possible that transport mismatch");
        }
        catch( RemoteException re )
        {
            throw new ConnectionException( "Unknown Remote Exception : " + re.getMessage() );
        }
    }

    /**
     * Method tryReconnect
     *
     * @return
     *
     */
    protected boolean tryReconnect()
    {

        try
        {
            m_rmiInvocationHandler = (RmiInvocationHandler)Naming.lookup( m_URL );

            return true;
        }
        catch( Exception e )
        {
            return false;
        }
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
                reply = m_rmiInvocationHandler.handleInvocation( request );

                if( reply.getReplyCode() >= 100 )
                {
                    if( reply instanceof TryLaterReply )
                    {
                        int millis = ( (TryLaterReply)reply ).getSuggestedDelayMillis();

                        m_clientMonitor.serviceSuspended(this.getClass(), request, tries, millis );

                        again = true;
                    }
                    else if( reply instanceof NoSuchReferenceReply )
                    {
                        throw new NoSuchReferenceException( ( (NoSuchReferenceReply)reply )
                                                            .getReferenceID() );
                    }
                    else if( reply instanceof NotPublishedReply )
                    {
                        PublishedNameRequest pnr = (PublishedNameRequest)request;

                        throw new NotPublishedException( pnr.getPublishedServiceName(),
                                                         pnr.getObjectName() );
                    }
                }
            }
            catch( RemoteException re )
            {
                if( re instanceof ConnectException | re instanceof ConnectIOException )
                {
                    int retryConnectTries = 0;

                    m_rmiInvocationHandler = null;

                    while( !tryReconnect() )
                    {
                        m_clientMonitor.serviceAbend(this.getClass(), retryConnectTries, re);

                        retryConnectTries++;
                    }
                }
                else
                {
                    throw new InvocationException( "Unknown RMI problem : "
                                                         + re.getMessage() );
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
