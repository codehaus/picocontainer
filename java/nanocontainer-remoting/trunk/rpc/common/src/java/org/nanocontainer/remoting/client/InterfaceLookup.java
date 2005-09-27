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

import org.nanocontainer.remoting.Authentication;
import org.nanocontainer.remoting.common.ConnectionException;

/**
 * InterfaceLookup describes the initial client's interaction with
 * with the server (lookup/listing of remote services.)
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public interface InterfaceLookup {

    /**
     * Lookup a name by which the remote service is
     * published by the server.
     * Usage:
     * <code>
     * InterfaceLookup lookupService= . . . . ;
     * RemoteInterface remoteInterface = lookupService.lookup("Published-Name-Of-The-Remote-Server");
     * </code>
     *
     * @param publishedServiceName
     * @return proxy to the Remote service.
     * @throws ConnectionException
     */
    Object lookup(String publishedServiceName) throws ConnectionException;

    /**
     * Lookup a name by which the remote service is
     * published by the server within the context of
     * the Authentication credentials supplied.
     *
     * @param publishedServiceName
     * @param authentication
     * @return
     * @throws ConnectionException
     */
    Object lookup(String publishedServiceName, Authentication authentication) throws ConnectionException;

    /**
     * This method returns the list of names of the
     * remote services.
     *
     * @return
     */
    String[] list();

    /**
     * Method getTextToSignForAuthentication
     *
     * @return
     */
    String getTextToSignForAuthentication();

    /**
     * Method close
     */
    void close();

    /**
     * Has a service.
     *
     * @param publishedServiceName
     * @return
     */
    boolean hasService(String publishedServiceName);


}
