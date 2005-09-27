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
package org.nanocontainer.remoting.client.transports.rmi;

import org.nanocontainer.remoting.client.HostContext;
import org.nanocontainer.remoting.client.InterfaceLookup;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.*;
import org.nanocontainer.remoting.client.factories.AbstractFactoryHelper;
import org.nanocontainer.remoting.client.factories.AbstractFactoryHelper;

/**
 * Class RmiFactoryHelper
 *
 *   "RMI:abcde.com:1234"
 *            0         :  1      : 2
 *
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class RmiFactoryHelper extends AbstractFactoryHelper
{
    private ThreadPool m_threadPool;
    private ClientMonitor m_clientMonitor;
    private ConnectionPinger m_connectionPinger;

    /**
     * Method getInterfaceLookup
     *
     *
     * @param factoryString
     * @param interfacesClassLoader
     *
     * @return
     *
     */
    public InterfaceLookup getInterfaceLookup(
            String factoryString, ClassLoader interfacesClassLoader, boolean optimize)
            throws ConnectionException
    {

        // TODO maybe we should cache these.  Or the abstract parent class should.
        String[] terms = processFactoryString(factoryString);
        HostContext hc = new RmiHostContext(m_threadPool,  m_clientMonitor, m_connectionPinger, terms[1], Integer.parseInt(terms[2]));
        Factory af = createFactory(terms[3], hc, optimize);

        return af;
    }

    public void setThreadPool(ThreadPool threadPool)
    {
        m_threadPool = threadPool;
    }

    public void setClientMonitor(ClientMonitor clientMonitor)
    {
        m_clientMonitor = clientMonitor;
    }

    public void setConnectionPinger(ConnectionPinger connectionPinger)
    {
        m_connectionPinger = connectionPinger;
    }
}
