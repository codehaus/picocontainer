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
package org.nanocontainer.remoting.server.transports.socket;

import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.Authenticator;
import org.nanocontainer.remoting.server.ClassRetriever;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;
import org.nanocontainer.remoting.server.authenticators.DefaultAuthenticator;
import org.nanocontainer.remoting.server.classretrievers.NoClassRetriever;
import org.nanocontainer.remoting.server.monitors.NullServerMonitor;
import org.nanocontainer.remoting.server.transports.AbstractServerStreamReadWriter;
import org.nanocontainer.remoting.server.transports.DefaultServerSideClientContextFactory;
import org.nanocontainer.remoting.server.transports.ServerCustomStreamReadWriter;

/**
 * Class CompleteSocketObjectStreamServer
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */

public class CompleteSocketCustomStreamServer extends AbstractCompleteSocketStreamServer {
    /**
     * Construct a CompleteSocketCustomStreamServer
     *
     * @param classRetriever
     * @param authenticator
     * @param serverMonitor
     * @param threadPool
     * @param contextFactory
     * @param port
     */
    public CompleteSocketCustomStreamServer(ClassRetriever classRetriever, Authenticator authenticator, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory, int port) {
        super(new InvocationHandlerAdapter(classRetriever, authenticator, serverMonitor, contextFactory), serverMonitor, threadPool, contextFactory, port);
    }

    public static class WithSimpleDefaults extends CompleteSocketCustomStreamServer {
        public WithSimpleDefaults(int port) {
            super(new NoClassRetriever(), new DefaultAuthenticator(), new NullServerMonitor(), new DefaultThreadPool(), new DefaultServerSideClientContextFactory(), port);
        }
    }

    /**
     * Create a Server Stream Read Writer.
     *
     * @return The Server Stream Read Writer.
     */
    protected AbstractServerStreamReadWriter createServerStreamReadWriter() {
        ServerCustomStreamReadWriter rw = new ServerCustomStreamReadWriter(m_serverMonitor, m_threadPool);
        return rw;
    }
}
