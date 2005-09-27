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
package org.nanocontainer.remoting.server;


/**
 * Interface Server
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public interface Server extends Publisher, ServerInvocationHandler {

    /**
     * The state of the system while shutting down.
     */
    int SHUTTINGDOWN = 101;

    /**
     * The state of the starting system.
     */
    int STARTING = 202;

    /**
     * The state of the starting system.
     */
    int STARTED = 303;

    /**
     * The state of the system when stopped
     */
    int STOPPED = 404;

    /**
     * The state of the un started system.
     */
    int UNSTARTED = 505;

    /**
     * Suspend publishing
     */
    void suspend();

    /**
     * Resume publishing
     */
    void resume();

    /**
     * Start publishing
     *
     * @throws ServerException if the server cannot start.
     */
    void start() throws ServerException;

    /**
     * Stop publishing
     */
    void stop();

}
