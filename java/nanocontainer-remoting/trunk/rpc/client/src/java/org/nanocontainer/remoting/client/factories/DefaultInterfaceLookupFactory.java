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
package org.nanocontainer.remoting.client.factories;

import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.InterfaceLookupFactory;
import org.nanocontainer.remoting.client.monitors.DumbClientMonitor;
import org.nanocontainer.remoting.client.pingers.DefaultConnectionPinger;
import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.common.ThreadPool;

/**
 * Class DefaultInterfaceLookupFactory
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class DefaultInterfaceLookupFactory extends AbstractInterfaceLookupFactory {

    public static final String[] SUPPORTEDSTREAMS = new String[]{"SocketObjectStream", "SocketCustomStream", "RMI"};
    private ThreadPool m_threadPool;
    private ClientMonitor m_clientMonitor;
    private ConnectionPinger m_connectionPinger;

    public DefaultInterfaceLookupFactory() {
        this(new DefaultThreadPool(), new DumbClientMonitor(), new DefaultConnectionPinger());
    }


    public DefaultInterfaceLookupFactory(ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger) {
        m_threadPool = threadPool;
        m_connectionPinger = connectionPinger;
        m_clientMonitor = clientMonitor;

        try {
            InterfaceLookupFactory factory = (InterfaceLookupFactory) this.getClass().getClassLoader().loadClass("org.nanocontainer.remoting.client.impl.socket.SocketObjectStreamFactoryHelper").newInstance();
            factory.setClientMonitor(m_clientMonitor);
            factory.setConnectionPinger(m_connectionPinger);
            factory.setThreadPool(m_threadPool);
            addFactory("SocketObjectStream:", factory);
        } catch (ClassNotFoundException cnfe) {
        } catch (InstantiationException ie) {
            ie.printStackTrace();


        } catch (IllegalAccessException iae) {

        }

        try {
            InterfaceLookupFactory factory = (InterfaceLookupFactory) this.getClass().getClassLoader().loadClass("org.nanocontainer.remoting.client.impl.socket.SocketCustomStreamFactoryHelper").newInstance();
            factory.setClientMonitor(m_clientMonitor);
            factory.setConnectionPinger(m_connectionPinger);
            factory.setThreadPool(m_threadPool);
            addFactory("SocketCustomStream:", factory);


        } catch (ClassNotFoundException cnfe) {

        } catch (InstantiationException ie) {

        } catch (IllegalAccessException iae) {

        }

        try {
            InterfaceLookupFactory factory = (InterfaceLookupFactory) Class.forName("org.nanocontainer.remoting.client.impl.rmi.RmiFactoryHelper").newInstance();
            factory.setClientMonitor(m_clientMonitor);
            factory.setConnectionPinger(m_connectionPinger);
            factory.setThreadPool(m_threadPool);
            addFactory("RMI:", factory);

        } catch (ClassNotFoundException cnfe) {
        } catch (InstantiationException ie) {
        } catch (IllegalAccessException iae) {
        }

        // TODO - add the rest.
    }

    public void setThreadPool(ThreadPool threadPool) {
        m_threadPool = threadPool;
    }

    public void setClientMonitor(ClientMonitor clientMonitor) {
        m_clientMonitor = clientMonitor;
    }

    public void setConnectionPinger(ConnectionPinger connectionPinger) {
        m_connectionPinger = connectionPinger;
    }
}
