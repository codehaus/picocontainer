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
package org.nanocontainer.remoting.commands;

/**
 * Interface RequestConstants a set of constants for requests.
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $*
 */
public interface RequestConstants {
    /**
     * A request for a class
     */
    int CLASSREQUEST = 301;
    /**
     * A method request
     */
    int METHODREQUEST = 302;
    /**
     * A method request to a facade
     */
    int METHODFACADEREQUEST = 303;
    /**
     * A initial lookup of a service.
     */
    int LOOKUPREQUEST = 304;
    /**
     * An initial opening of connection
     */
    int OPENCONNECTIONREQUEST = 305;
    /**
     * A ping to keep the connection alive
     */
    int PINGREQUEST = 306;
    /**
     * A list of published services
     */
    int LISTREQUEST = 307;
    /**
     * A request for garbage collection for a finished with proxy
     */
    int GCREQUEST = 308;
    /**
     * A request for listing the methods within the publishedName
     */
    int LISTMETHODSREQUEST = 309;

    /**
     * A method request
     */
    int METHODASYNCREQUEST = 310;

}
