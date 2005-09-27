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

import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.common.ThreadPool;

/**
 * Interface InterfaceLookupFactory
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public interface InterfaceLookupFactory {

    /**
     * Method getInterfaceLookup
     * <p/>
     * FactoryStrings as listed here should look like this:
     * <p/>
     * "SocketObjectStream:abcde.com:1234:S:BO"
     * "SocketCustomStream:abcde.com:1235:C:NBO"
     * "RMI:abcde.com:1236:S:NBO"
     * <p/>
     * :S: and alike is
     * - "S" for server side proxy classes
     * - "C" for client side proxy classes
     *
     * @param factoryString
     * @param optimize
     * @return
     */
    InterfaceLookup getInterfaceLookup(String factoryString, boolean optimize) throws ConnectionException;

    /**
     * Method getInterfaceLookup
     *
     * @param factoryString
     * @param interfacesClassLoader
     * @param optimize
     * @return
     * @throws ConnectionException
     */
    InterfaceLookup getInterfaceLookup(String factoryString, ClassLoader interfacesClassLoader, boolean optimize) throws ConnectionException;

    void setThreadPool(ThreadPool threadPool);

    void setClientMonitor(ClientMonitor clientMonitor);

    void setConnectionPinger(ConnectionPinger connectionPinger);

}
