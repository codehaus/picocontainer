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
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerInvocationHandler;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;

/**
 * Class DirectInvocationHandler
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public final class DirectInvocationHandler extends AbstractDirectInvocationHandler
{

    private ServerInvocationHandler m_invocationHandler;


    /**
     * Constructor DirectInvocationHandler
     *
     *
     * @param threadPool
     * @param clientMonitor
     * @param connectionPinger
     * @param invocationHandler
     */
    public DirectInvocationHandler(ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger, ServerInvocationHandler invocationHandler)
    {
        super(threadPool, clientMonitor, connectionPinger);
        m_invocationHandler = invocationHandler;
    }

    protected Reply performInvocation( Request request ) throws IOException
    {
        return m_invocationHandler.handleInvocation( request, "" );
    }

}
