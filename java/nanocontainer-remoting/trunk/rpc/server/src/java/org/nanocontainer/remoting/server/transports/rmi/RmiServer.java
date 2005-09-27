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
package org.nanocontainer.remoting.server.transports.rmi;

import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.common.RmiInvocationHandler;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.Authenticator;
import org.nanocontainer.remoting.server.ClassRetriever;
import org.nanocontainer.remoting.server.ServerException;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;
import org.nanocontainer.remoting.server.authenticators.DefaultAuthenticator;
import org.nanocontainer.remoting.server.classretrievers.PlainClassRetriever;
import org.nanocontainer.remoting.server.monitors.NullServerMonitor;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.DefaultServerSideClientContextFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class RmiServer for serving of 'over RMI'
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class RmiServer extends AbstractServer {

    /**
     * The invocation adapter
     */
    private RmiInvocationAdapter m_rmiInvocationAdapter;

    /**
     * The port
     */
    private int m_port;
    /**
     * The registry
     */
    private Registry m_registry;

    /**
     * Constructor a RmiServer with a preexiting invocation handler.
     *
     * @param invocationHandlerAdapter
     * @param serverMonitor
     * @param threadPool
     * @param contextFactory
     * @param port
     */
    public RmiServer(InvocationHandlerAdapter invocationHandlerAdapter, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory, int port) {
        super(invocationHandlerAdapter, serverMonitor, threadPool, contextFactory);
        m_port = port;
    }

    public static class WithNoInvocationHandler extends RmiServer {
        /**
         * @param classRetriever
         * @param authenticator
         * @param serverMonitor
         * @param threadPool
         * @param contextFactory
         * @param port
         */
        public WithNoInvocationHandler(ClassRetriever classRetriever, Authenticator authenticator, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory, int port) {
            super(new InvocationHandlerAdapter(classRetriever, authenticator, serverMonitor, contextFactory), serverMonitor, threadPool, contextFactory, port);
        }
    }

    public static class WithSimpleDefaults extends WithNoInvocationHandler {
        /**
         * @param port
         */
        public WithSimpleDefaults(int port) {
            super(new PlainClassRetriever(), new DefaultAuthenticator(), new NullServerMonitor(), new DefaultThreadPool(), new DefaultServerSideClientContextFactory(), port);
        }
    }

    /**
     * Start the server.
     *
     * @throws ServerException if an exception during starting.
     */
    public void start() throws ServerException {
        setState(STARTING);
        try {
            m_rmiInvocationAdapter = new RmiInvocationAdapter(this);

            UnicastRemoteObject.exportObject(m_rmiInvocationAdapter);

            m_registry = LocateRegistry.createRegistry(m_port);

            m_registry.rebind(RmiInvocationHandler.class.getName(), m_rmiInvocationAdapter);
            setState(STARTED);
        } catch (RemoteException re) {
            throw new ServerException("Some problem setting up RMI server", re);
        }
    }

    /**
     * Stop the server.
     */
    public void stop() {

        setState(SHUTTINGDOWN);

        killAllConnections();

        try {
            m_registry.unbind(RmiInvocationHandler.class.getName());
        } catch (RemoteException re) {
            m_serverMonitor.stopServerError(this.getClass(), "RmiServer.stop(): Error stopping RMI server - RemoteException", re);
        } catch (NotBoundException nbe) {
            m_serverMonitor.stopServerError(this.getClass(), "RmiServer.stop(): Error stopping RMI server - NotBoundException", nbe);
        }

        setState(STOPPED);
    }
}
