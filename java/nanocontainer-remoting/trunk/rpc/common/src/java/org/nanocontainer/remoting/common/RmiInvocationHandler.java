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
package org.nanocontainer.remoting.common;

import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface RmiClientInvocationHandler
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public interface RmiInvocationHandler extends Remote {

    /**
     * Handle method invocation.
     *
     * @param request the request to marshall over RMI
     * @return the reply got back from the server
     * @throws RemoteException in case there is outage.
     */
    Reply handleInvocation(Request request) throws RemoteException;
}
