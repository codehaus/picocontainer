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
package org.nanocontainer.remoting.server.transports.direct;

import org.nanocontainer.remoting.commands.InvocationExceptionReply;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerMarshalledInvocationHandler;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;
import org.nanocontainer.remoting.server.adapters.MarshalledInvocationHandlerAdapter;
import org.nanocontainer.remoting.server.authenticators.DefaultAuthenticator;
import org.nanocontainer.remoting.server.classretrievers.NoClassRetriever;
import org.nanocontainer.remoting.server.monitors.NullServerMonitor;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.DefaultServerSideClientContextFactory;

/**
 * Class DirectMarshalledServer
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class DirectMarshalledServer extends AbstractServer implements ServerMarshalledInvocationHandler {

    private final MarshalledInvocationHandlerAdapter marshalledInvocationHandlerAdapter;

    /**
     * Constructor DirectMarshalledServer for use with pre-exiting InvocationHandlerAdapter and MarshalledInvocationHandler
     *
     * @param invocationHandlerAdapter
     * @param serverMonitor
     * @param threadPool
     * @param contextFactory
     * @param marshalledInvocationHandlerAdapter
     *
     */
    public DirectMarshalledServer(InvocationHandlerAdapter invocationHandlerAdapter, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory, MarshalledInvocationHandlerAdapter marshalledInvocationHandlerAdapter) {
        super(invocationHandlerAdapter, serverMonitor, threadPool, contextFactory);
        this.marshalledInvocationHandlerAdapter = marshalledInvocationHandlerAdapter;
    }

    public static class WithMarshalledInvocationHandlerAdapter extends DirectMarshalledServer {
        public WithMarshalledInvocationHandlerAdapter(InvocationHandlerAdapter invocationHandlerAdapter, MarshalledInvocationHandlerAdapter marshalledInvocationHandlerAdapter) {
            super(invocationHandlerAdapter, new NullServerMonitor(), new DefaultThreadPool(), new DefaultServerSideClientContextFactory(), marshalledInvocationHandlerAdapter);
        }
    }

    public static class WithInvocationHandlerAdapter extends WithMarshalledInvocationHandlerAdapter {
        public WithInvocationHandlerAdapter(InvocationHandlerAdapter invocationHandlerAdapter) {
            super(invocationHandlerAdapter, new MarshalledInvocationHandlerAdapter(invocationHandlerAdapter));
        }
    }

//    public DirectMarshalledServer(
//
//            ClassRetriever classRetriever,
//            Authenticator authenticator,
//            ServerMonitor serverMonitor,
//            ThreadPool threadPool,
//            ServerSideClientContextFactory contextFactory,
//            MarshalledInvocationHandlerAdapter marshalledInvocationHandlerAdapter)
//    {
//        super(
//                new InvocationHandlerAdapter(
//                        classRetriever,
//                        authenticator,
//                        serverMonitor,
//                        contextFactory),
//                serverMonitor, threadPool, contextFactory);
//        marshalledInvocationHandlerAdapter = marshalledInvocationHandlerAdapter;
//    }

    public static class WithSimpleDefaults extends WithInvocationHandlerAdapter {
        public WithSimpleDefaults() {
            super(new InvocationHandlerAdapter(new NoClassRetriever(), new DefaultAuthenticator(), new NullServerMonitor(), new DefaultServerSideClientContextFactory()));
        }
    }

    /**
     * Method start
     */
    public void start() {
        setState(STARTED);
    }

    /**
     * Method stop
     */
    public void stop() {

        setState(SHUTTINGDOWN);

        killAllConnections();

        setState(STOPPED);
    }

    /**
     * Method handleInvocation
     *
     * @param request
     * @return
     */
    public byte[] handleInvocation(byte[] request, Object connectionDetails) {
        return marshalledInvocationHandlerAdapter.handleInvocation(request, connectionDetails);
    }

    /**
     * Method handleInvocation
     *
     * @param request
     * @return
     */
    public Reply handleInvocation(Request request, Object connectionDetails) {

        if (getState() == STARTED) {
            return super.handleInvocation(request, connectionDetails);
        } else {
            return new InvocationExceptionReply("Service is not started");
        }
    }
}
