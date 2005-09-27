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

import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.common.RmiInvocationHandler;
import org.nanocontainer.remoting.server.transports.AbstractServer;

import java.rmi.RemoteException;

/**
 * Class RmiinvocationAdapter for 'over RMI' invocation adaptation.
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class RmiInvocationAdapter implements RmiInvocationHandler {
    /**
     * The abstract server
     */
    private AbstractServer m_abstractServer;

    /**
     * Constructor a RmiInvocationAdapter with an abstract server.
     *
     * @param abstractServer The abstract server
     */
    public RmiInvocationAdapter(AbstractServer abstractServer) {
        m_abstractServer = abstractServer;
    }

    /**
     * Handle an Invocation
     *
     * @param request The request
     * @return a reply object
     * @throws RemoteException if a problem during processing
     */
    public Reply handleInvocation(Request request) throws RemoteException {
        return m_abstractServer.handleInvocation(request, "RMI-TODO");
    }
}
