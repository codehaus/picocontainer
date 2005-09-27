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

import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.factories.AbstractHostContext;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.client.pingers.PerpetualConnectionPinger;
import org.nanocontainer.remoting.client.monitors.DefaultClientMonitor;
import org.nanocontainer.remoting.client.factories.AbstractHostContext;

/**
 * Class RmiHostContext
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class RmiHostContext extends AbstractHostContext
{

    //TODO constr with classloader

    /**
     * Constructor RmiHostContext
     *
     *
     * @param host
     * @param port
     *
     * @throws ConnectionException
     *
     */
    public RmiHostContext( ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger, String host, int port ) throws ConnectionException
    {
        super( new RmiClientInvocationHandler( threadPool, clientMonitor, connectionPinger, host, port ) );
    }

    public static class WithSimpleDefaults extends RmiHostContext {
        public WithSimpleDefaults(String host, int port) throws ConnectionException
        {
            super(new DefaultThreadPool(), new DefaultClientMonitor(), new PerpetualConnectionPinger(),
                    host, port);
        }

    }
}
