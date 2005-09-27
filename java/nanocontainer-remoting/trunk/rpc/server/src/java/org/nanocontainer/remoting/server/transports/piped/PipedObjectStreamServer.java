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
package org.nanocontainer.remoting.server.transports.piped;

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
import org.nanocontainer.remoting.server.transports.ServerObjectStreamReadWriter;

/**
 * Class PipedObjectStreamServer
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class PipedObjectStreamServer extends AbstractPipedServer {


    /**
     * @param classRetriever
     * @param authenticator
     * @param serverMonitor
     * @param threadPool
     * @param contextFactory
     */
    public PipedObjectStreamServer(ClassRetriever classRetriever, Authenticator authenticator, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory) {
        super(new InvocationHandlerAdapter(classRetriever, authenticator, serverMonitor, contextFactory), serverMonitor, threadPool, contextFactory);
    }

    public static class WithSimpleDefaults extends PipedObjectStreamServer {
        public WithSimpleDefaults() {
            super(new NoClassRetriever(), new DefaultAuthenticator(), new NullServerMonitor(), new DefaultThreadPool(), new DefaultServerSideClientContextFactory());
        }
    }

    protected AbstractServerStreamReadWriter createServerStreamReadWriter() {
        return new ServerObjectStreamReadWriter(m_serverMonitor, m_threadPool);
    }
}
