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

import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.client.NotPublishedException;
import org.nanocontainer.remoting.commands.PublishedNameRequest;
import org.nanocontainer.remoting.commands.TryLaterReply;
import org.nanocontainer.remoting.commands.RequestConstants;
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
 * Class DirectInvocationHandler
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public abstract class AbstractDirectInvocationHandler extends AbstractClientInvocationHandler
{

    protected boolean m_methodLogging = false;
    protected long m_lastRealRequest = System.currentTimeMillis();


    public AbstractDirectInvocationHandler(ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger)
    {
        super(threadPool, clientMonitor, connectionPinger);
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
    public Reply handleInvocation( Request request )
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
                reply = performInvocation( request );
            }
            catch( IOException ioe )
            {
                ioe.printStackTrace();
            }

            //if ((reply instanceof ProblemReply))  // slower by 11%
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

    protected boolean tryReconnect()
    {

        // blimey how do we reconnect this?
        throw new InvocationException( "Direct connection broken, unable to reconnect." );
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

    protected abstract Reply performInvocation( Request request ) throws IOException;
}
