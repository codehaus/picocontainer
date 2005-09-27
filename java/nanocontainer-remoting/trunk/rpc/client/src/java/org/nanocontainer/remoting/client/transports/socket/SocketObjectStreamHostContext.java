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
package org.nanocontainer.remoting.client.transports.socket;

import org.nanocontainer.remoting.client.transports.piped.PipedObjectStreamHostContext;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.factories.AbstractHostContext;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.client.pingers.NeverConnectionPinger;
import org.nanocontainer.remoting.client.monitors.DumbClientMonitor;
import org.nanocontainer.remoting.client.factories.AbstractSameVmBindableHostContext;
import org.nanocontainer.remoting.client.factories.AbstractHostContext;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Method;

/**
 * Class SocketObjectStreamHostContext
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class SocketObjectStreamHostContext extends AbstractSameVmBindableHostContext
{

    private final ThreadPool m_threadPool;
    private final ClientMonitor m_clientMonitor;
    private final ConnectionPinger m_connectionPinger;
    private int m_port;
    private ClassLoader m_classLoader;

    /**
     * Constructor SocketObjectStreamHostContext
     *
     * @param threadPool
     * @param clientMonitor
     * @param connectionPinger
     * @param interfacesClassLoader
     * @param host
     * @param port
     * @throws ConnectionException
     */
    public SocketObjectStreamHostContext( ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger, ClassLoader interfacesClassLoader, String host, int port )
        throws ConnectionException
    {
        super(
                threadPool, clientMonitor, connectionPinger,
                new SocketObjectStreamInvocationHandler( threadPool, clientMonitor, connectionPinger, host, port, interfacesClassLoader )
        );
        m_threadPool = threadPool;
        m_clientMonitor = clientMonitor;
        m_connectionPinger = connectionPinger;
        m_classLoader = interfacesClassLoader;
        m_port = port;
    }

    public static class WithCurrentClassLoader extends SocketObjectStreamHostContext
    {
        /**
         *
         * @param threadPool
         * @param clientMonitor
         * @param connectionPinger
         * @param host
         * @param port
         * @throws ConnectionException
         */
        public WithCurrentClassLoader(ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger, String host, int port) throws ConnectionException
        {
            super(threadPool,
                    clientMonitor,
                    connectionPinger,
                    SocketObjectStreamHostContext.class.getClassLoader(),
                    host, port);
        }
    }

    public static class WithSimpleDefaults extends SocketObjectStreamHostContext
    {
        /**
         *
         * @param host
         * @param port
         * @throws ConnectionException
         */
        public WithSimpleDefaults(String host, int port) throws ConnectionException
        {
            super(new DefaultThreadPool(),
                    new DumbClientMonitor(),
                    new NeverConnectionPinger(),
                    SocketObjectStreamHostContext.class.getClassLoader(),
                    host, port);
        }
    }


    /**
     * Make a HostContext for this using SameVM connections nstead of socket based ones.
     * @return the HostContext
     * @throws ConnectionException if a problem
     */
    public AbstractHostContext makeSameVmHostContext() throws ConnectionException
    {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream();
        try
        {
            Object binder = getOptmization("port=" + m_port);
            if (binder == null)
            {
                return null;
            }
            Object bound = bind(binder, in, out);
            if (bound == null)
            {
                return null;
            }
            PipedObjectStreamHostContext pipedCustomStreamHostContext
                    = new PipedObjectStreamHostContext(m_threadPool, m_clientMonitor, m_connectionPinger, in, out, m_classLoader);
            pipedCustomStreamHostContext.initialize();
            return pipedCustomStreamHostContext;
        }
        catch (Exception e)
        {
            throw new ConnectionException("Naming exception during bind :" + e.getMessage());
        }
    }

    private Object bind(Object object, PipedInputStream inputStream,
                       PipedOutputStream outputStream)
    {

        try
        {
            Object[] parms = new Object[]{ inputStream, outputStream };
            Method method = object.getClass().getMethod("bind", new Class[] { parms.getClass() });
            return method.invoke(object, new Object[] { parms });
        }
        catch (Exception e)
        {
            return null;
        }
    }


}
