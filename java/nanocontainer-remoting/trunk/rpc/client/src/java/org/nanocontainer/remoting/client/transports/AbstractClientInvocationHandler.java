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
package org.nanocontainer.remoting.client.transports;

import org.nanocontainer.remoting.client.ClientInvocationHandler;
import org.nanocontainer.remoting.client.ConnectionClosedException;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.common.CallbackException;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.commands.PingRequest;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.common.DefaultThreadPool;

/**
 * Class AbstractClientInvocationHandler
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.3 $
 */
public abstract class AbstractClientInvocationHandler
    implements ClientInvocationHandler
{

    protected final ConnectionPinger m_connectionPinger;
    protected final ClientMonitor m_clientMonitor;
    protected boolean m_stopped = false;
    protected final ThreadPool m_threadPool;
    protected final boolean m_methodLogging;


    public AbstractClientInvocationHandler(ThreadPool threadPool, ClientMonitor clientMonitor,
                                           ConnectionPinger connectionPinger)
    {
        m_threadPool = threadPool;
        m_clientMonitor = clientMonitor;
        m_methodLogging = clientMonitor.methodLogging();
        m_connectionPinger = connectionPinger;
    }

    public ThreadPool getThreadPool()
    {
        return m_threadPool;
    }

    public ClientMonitor getClientMonitor()
    {
        return m_clientMonitor;
    }

    /**
     * Method initialize
     *
     *
     * @throws ConnectionException
     *
     */
    public void initialize() throws ConnectionException
    {
        m_connectionPinger.setInvocationHandler( this );
        m_connectionPinger.start();
    }

    /**
     * Method close
     *
     *
     */
    public void close()
    {

        m_connectionPinger.stop();

        m_stopped = true;
    }

    /**
     * Method ping
     *
     *
     */
    public void ping()
    {

        if( m_stopped )
        {
            throw new ConnectionClosedException( "Connection closed" );
        }

        Reply ar = handleInvocation( new PingRequest() );
    }

    protected abstract boolean tryReconnect();

    /**
     * Method getInterfacesClassLoader
     *
     *
     * @return
     *
     */
    public ClassLoader getInterfacesClassLoader()
    {
        return AbstractClientInvocationHandler.class.getClassLoader();
    }


    /**
     * resolveArgument can handle any changes that one has to  do to the arguments being
     * marshalled to the server.
     * Noop Default behaviour.
     * @param remoteObjName
     * @param objClass
     * @param obj
     * @return Object
     */

    public Object resolveArgument(String remoteObjName, String methodSignature ,Class objClass , Object obj)
    {
        return obj;
    }

    public boolean isCallBackEnabled()
    {
        return false;
    }

    public boolean exposeObject(Object exposedObject, Class exposedInterface) throws CallbackException
    {
        throw new UnsupportedOperationException();
    }

    public String getPublishedName(Object exposedObject)
    {
        throw new UnsupportedOperationException();
    }

}
