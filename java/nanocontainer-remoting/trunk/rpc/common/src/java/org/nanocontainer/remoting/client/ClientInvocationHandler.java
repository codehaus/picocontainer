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
package org.nanocontainer.remoting.client;

import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.common.CallbackException;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.ThreadPool;

/**
 * Interface ClientInvocationHandler
 *
 * @author Paul Hammant
 * @version * $Revision: 1.3 $
 */
public interface ClientInvocationHandler {

    /**
     * Handle a method invocation
     *
     * @param request The request to handle
     * @return the reply that is a consequence of the request
     */
    Reply handleInvocation(Request request);

    /**
     * Method initialize
     *
     * @throws ConnectionException
     */
    void initialize() throws ConnectionException;

    /**
     * Method close
     */
    void close();

    /**
     * Method getLastRealRequest
     *
     * @return
     */
    long getLastRealRequest();

    /**
     * Method ping
     */
    void ping();

    /**
     * Method getInterfacesClassLoader
     *
     * @return
     */
    ClassLoader getInterfacesClassLoader();

    /**
     * morphObject handles any changes  to the arguments being
     * marshalled to the server.
     *
     * @param remoteObjName         String remote objecct name
     * @param inputArgumentClass    Class of the input argument
     * @param inputArgumentInstance instance of the object being marshalled to the server
     * @return Object new object that replaces the input argument.
     */

    Object resolveArgument(String remoteObjName, String methodSignature, Class inputArgumentClass, Object inputArgumentInstance);

    boolean isCallBackEnabled();

    /**
     * For CallBackEnabled only
     *
     * @param exposedObject
     * @param exposedInterface
     * @return
     * @throws CallbackException
     */
    boolean exposeObject(Object exposedObject, Class exposedInterface) throws CallbackException;

    /**
     * For CallBackEnabled only
     *
     * @param exposedObject
     * @return
     */
    String getPublishedName(Object exposedObject);

    ThreadPool getThreadPool();

    ClientMonitor getClientMonitor();

}
