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

import org.nanocontainer.remoting.client.ClientInvocationHandler;
import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.RegistryHelper;
import org.nanocontainer.remoting.common.ThreadPool;

public abstract class AbstractSameVmBindableHostContext extends AbstractHostContext {

    protected final ThreadPool m_threadPool;
    protected final ClientMonitor m_clientMonitor;
    protected final ConnectionPinger m_connectionPinger;

    public AbstractSameVmBindableHostContext(ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger, ClientInvocationHandler clientInvocationHandler) {
        super(clientInvocationHandler);
        m_threadPool = threadPool;
        m_clientMonitor = clientMonitor;
        m_connectionPinger = connectionPinger;
    }


    /**
     * Make a HostContext for this using SameVM connections nstead of socket based ones.
     *
     * @return the HostContext
     * @throws ConnectionException if a problem
     */
    public abstract AbstractHostContext makeSameVmHostContext() throws ConnectionException;

    protected Object getOptmization(String uniqueID) {
        return new RegistryHelper().get("/.nanocontainerRemoting/optimizations/" + uniqueID);
    }

}
