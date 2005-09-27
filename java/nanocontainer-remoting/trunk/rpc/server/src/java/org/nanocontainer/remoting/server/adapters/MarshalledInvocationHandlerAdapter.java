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
package org.nanocontainer.remoting.server.adapters;

import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.common.SerializationHelper;
import org.nanocontainer.remoting.server.ServerInvocationHandler;
import org.nanocontainer.remoting.server.ServerMarshalledInvocationHandler;

/**
 * Class MarshalledInvocationHandlerAdapter
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class MarshalledInvocationHandlerAdapter implements ServerMarshalledInvocationHandler {

    /**
     * The invocation hamdeler
     */
    private ServerInvocationHandler m_invocationHandler;
    /**
     * The class loader.
     */
    private ClassLoader m_classLoader;

    /**
     * Constructor MarshalledInvocationHandlerAdapter
     *
     * @param invocationHandler The invocation handler
     */
    public MarshalledInvocationHandlerAdapter(ServerInvocationHandler invocationHandler) {
        m_invocationHandler = invocationHandler;
        m_classLoader = getClass().getClassLoader();
    }

    /**
     * Constructor MarshalledInvocationHandlerAdapter
     *
     * @param invocationHandler The invocation handler
     * @param classLoader       The classloader
     */
    public MarshalledInvocationHandlerAdapter(ServerInvocationHandler invocationHandler, ClassLoader classLoader) {
        m_invocationHandler = invocationHandler;
        m_classLoader = classLoader;
    }

    /**
     * Handle an Invocation
     *
     * @param request The request
     * @return The reply
     */
    public byte[] handleInvocation(byte[] request, Object connectionDetails) {

        try {
            Request ar = (Request) SerializationHelper.getInstanceFromBytes(request, m_classLoader);
            Reply reply = m_invocationHandler.handleInvocation(ar, connectionDetails);

            return SerializationHelper.getBytesFromInstance(reply);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }
}
