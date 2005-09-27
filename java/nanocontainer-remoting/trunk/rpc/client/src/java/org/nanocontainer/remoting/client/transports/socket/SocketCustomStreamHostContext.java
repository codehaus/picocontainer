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

import org.nanocontainer.remoting.client.transports.piped.PipedCustomStreamHostContext;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.ClientInvocationHandler;
import org.nanocontainer.remoting.client.factories.AbstractHostContext;
import org.nanocontainer.remoting.client.pingers.NeverConnectionPinger;
import org.nanocontainer.remoting.client.monitors.DumbClientMonitor;
import org.nanocontainer.remoting.client.factories.AbstractSameVmBindableHostContext;
import org.nanocontainer.remoting.client.factories.AbstractHostContext;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.common.DefaultThreadPool;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Method;

/**
 * Class SocketCustomStreamHostContext
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.3 $
 */
public class SocketCustomStreamHostContext extends AbstractSameVmBindableHostContext
{

    private int m_port;

    /**
     * Constructor SocketCustomStreamHostContext
     *
     *
     * @param threadPool
     * @param clientMonitor
     * @param connectionPinger
     * @param host
     * @param port
     * @throws ConnectionException
     */
    public SocketCustomStreamHostContext(ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger, ClassLoader interfacesClassLoader, String host, int port) throws ConnectionException
    {
        super(threadPool, clientMonitor, connectionPinger,
                new SocketCustomStreamInvocationHandler(threadPool, clientMonitor, connectionPinger,
                        interfacesClassLoader, host, port)
        );
        m_port = port;
    }

    public static class WithCurrentClassLoader extends SocketCustomStreamHostContext
    {
        /**
         *
         * @param host
         * @param port
         * @throws ConnectionException
         */
        public WithCurrentClassLoader(String host, int port, ClassLoader classLoader) throws ConnectionException
        {
            super(new DefaultThreadPool(),
                    new DumbClientMonitor(),
                    new NeverConnectionPinger(),
                    classLoader,
                    host, port);
        }
    }

    public static class WithSimpleDefaults extends SocketCustomStreamHostContext
    {
        /**
         * @param host
         * @param port
         * @throws ConnectionException
         */
        public WithSimpleDefaults(String host, int port) throws ConnectionException
        {
            super(new DefaultThreadPool(),
                    new DumbClientMonitor(),
                    new NeverConnectionPinger(),
                    SocketCustomStreamHostContext.class.getClassLoader(), host, port);
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
            PipedCustomStreamHostContext pipedCustomStreamHostContext
                    = new PipedCustomStreamHostContext(m_threadPool, m_clientMonitor, m_connectionPinger, in, out);
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
            Object[] parms = new Object[]{inputStream, outputStream};
            Method method = object.getClass().getMethod("bind", new Class[]{parms.getClass()});
            return method.invoke(object, new Object[]{parms});
        }
        catch (Exception e)
        {
            return null;
        }
    }


}
