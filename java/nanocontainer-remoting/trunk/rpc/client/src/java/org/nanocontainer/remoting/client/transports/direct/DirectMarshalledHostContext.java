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

import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.factories.AbstractHostContext;
import org.nanocontainer.remoting.client.pingers.NeverConnectionPinger;
import org.nanocontainer.remoting.client.monitors.DumbClientMonitor;
import org.nanocontainer.remoting.client.factories.AbstractHostContext;
import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerMarshalledInvocationHandler;


/**
 * Class DirectMarshalledHostContext
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class DirectMarshalledHostContext extends AbstractHostContext
{

    /**
     * Constructor DirectMarshalledHostContext
     *
     * @param threadPool
     * @param clientMonitor
     * @param connectionPinger
     * @param interfacesClassLoader
     * @param invocationHandler
     */
    public DirectMarshalledHostContext(ThreadPool threadPool,
                                       ClientMonitor clientMonitor,
                                       ConnectionPinger connectionPinger,
                                       ClassLoader interfacesClassLoader,
                                       ServerMarshalledInvocationHandler invocationHandler)
    {
        super(
                new DirectMarshalledInvocationHandler(
                        threadPool,
                        clientMonitor,
                        connectionPinger,
                        invocationHandler,
                        interfacesClassLoader)
        );
    }

    public static class WithSimpleDefaults extends DirectMarshalledHostContext
    {
        /**
         *
         * @param invocationHandler
         */
        public WithSimpleDefaults(ServerMarshalledInvocationHandler invocationHandler)
        {
            super(
                    new DefaultThreadPool(),
                    new DumbClientMonitor(),
                    new NeverConnectionPinger(),
                    DirectMarshalledHostContext.class.getClassLoader(),
                    invocationHandler
            );
        }
    }
}
