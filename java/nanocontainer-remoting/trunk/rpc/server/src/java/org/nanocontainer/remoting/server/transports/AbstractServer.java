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
package org.nanocontainer.remoting.server.transports;

import org.nanocontainer.remoting.commands.MethodRequest;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.common.ThreadPoolAware;
import org.nanocontainer.remoting.server.MethodInvocationHandler;
import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.PublicationException;
import org.nanocontainer.remoting.server.Server;
import org.nanocontainer.remoting.server.ServerConnection;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;

import java.util.Vector;

/**
 * Class AbstractServer
 *
 * @author Paul Hammant
 * @author Vinay Chandrasekharan <a href="mailto:vinayc77@yahoo.com">vinayc77@yahoo.com</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractServer implements Server, ThreadPoolAware {

    /**
     * A vector of connections
     */
    private Vector m_connections = new Vector();

    /**
     * The invocation handler
     */
    private InvocationHandlerAdapter m_invocationHandlerAdapter;

    /**
     * The state of the system.
     */
    private int m_state = UNSTARTED;

    protected final ServerMonitor m_serverMonitor;
    protected final ThreadPool m_threadPool;
    protected final ServerSideClientContextFactory m_contextFactory;

    /**
     * Construct a AbstractServer
     *
     * @param invocationHandlerAdapter The invocation handler adapter to use.
     * @param serverMonitor            The Server monitor
     */
    public AbstractServer(InvocationHandlerAdapter invocationHandlerAdapter, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory) {
        m_invocationHandlerAdapter = invocationHandlerAdapter;
        m_serverMonitor = serverMonitor;
        m_threadPool = threadPool;
        m_contextFactory = contextFactory;
    }


    public synchronized ThreadPool getThreadPool() {
        return m_threadPool;
    }


    /**
     * Handle an Invocation
     *
     * @param request The request of the invocation.
     * @return An suitable reply.
     */
    public Reply handleInvocation(Request request, Object connectionDetails) {
        return m_invocationHandlerAdapter.handleInvocation(request, connectionDetails);
    }

    /**
     * Suspend the server with open connections.
     */
    public void suspend() {
        m_invocationHandlerAdapter.suspend();
    }

    /**
     * Resume a server with open connections.
     */
    public void resume() {
        m_invocationHandlerAdapter.resume();
    }

    /**
     * Strart a connection
     *
     * @param connection The connection
     */
    protected void connectionStart(ServerConnection connection) {
        m_connections.add(connection);
    }

    /**
     * Complete a connection.
     *
     * @param connection The connection
     */
    protected void connectionCompleted(ServerConnection connection) {
        m_connections.remove(connection);
    }

    /**
     * Kill connections.
     */
    protected void killAllConnections() {
        // Copy the connections into an array to avoid ConcurrentModificationExceptions
        //  as the connections are closed.
        ServerConnection[] connections = (ServerConnection[]) m_connections.toArray(new ServerConnection[0]);
        for (int i = 0; i < connections.length; i++) {
            connections[i].endConnection();
        }
    }

    /**
     * Publish an object via its interface
     *
     * @param impl              The implementation
     * @param asName            as this name.
     * @param interfaceToExpose The interface to expose.
     * @throws org.nanocontainer.remoting.server.PublicationException
     *          if an error during publication.
     */
    public void publish(Object impl, String asName, Class interfaceToExpose) throws PublicationException {
        m_invocationHandlerAdapter.publish(impl, asName, interfaceToExpose);
    }

    /**
     * Publish an object via its publication description
     *
     * @param impl                   The implementation
     * @param asName                 as this name.
     * @param publicationDescription The publication description.
     * @throws PublicationException if an error during publication.
     */
    public void publish(Object impl, String asName, PublicationDescription publicationDescription) throws PublicationException {
        m_invocationHandlerAdapter.publish(impl, asName, publicationDescription);
    }

    /**
     * UnPublish an object.
     *
     * @param impl   The implementation
     * @param asName as this name.
     * @throws PublicationException if an error during publication.
     */
    public void unPublish(Object impl, String asName) throws PublicationException {
        m_invocationHandlerAdapter.unPublish(impl, asName);
    }

    /**
     * Replace the server side instance of a published object
     *
     * @param oldImpl       The previous implementation.
     * @param publishedName The name it is published as.
     * @param withImpl      The impl to superceed.
     * @throws PublicationException if an error during publication.
     */
    public void replacePublished(Object oldImpl, String publishedName, Object withImpl) throws PublicationException {
        m_invocationHandlerAdapter.replacePublished(oldImpl, publishedName, withImpl);
    }

    /**
     * Get the Method Invocation Handler for a particular request.
     *
     * @param methodRequest The method request
     * @param objectName    The object Name.
     * @return The Method invocation handler
     */
    public MethodInvocationHandler getMethodInvocationHandler(MethodRequest methodRequest, String objectName) {
        return m_invocationHandlerAdapter.getMethodInvocationHandler(methodRequest, objectName);
    }

    /**
     * Get the MethodInvocationHandler for a particular published name.
     *
     * @param publishedName The published name.
     * @return The Method invocation handler
     */
    public MethodInvocationHandler getMethodInvocationHandler(String publishedName) {
        return m_invocationHandlerAdapter.getMethodInvocationHandler(publishedName);
    }

    /**
     * Get the Invocation Handler Adapter.
     *
     * @return the invocation handler adapter.
     */
    public InvocationHandlerAdapter getInovcationHandlerAdapter() {
        return m_invocationHandlerAdapter;
    }


    /**
     * Set the state for the server
     *
     * @param state The state
     */
    protected void setState(int state) {
        m_state = state;
    }

    /**
     * Get the state for teh server.
     *
     * @return the state.
     */
    protected int getState() {
        return m_state;
    }

    protected ServerSideClientContextFactory getClientContextFactory() {
        return m_contextFactory;
    }


}
