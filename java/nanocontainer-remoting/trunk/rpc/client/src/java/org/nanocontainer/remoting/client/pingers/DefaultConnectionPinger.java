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
package org.nanocontainer.remoting.client.pingers;

/**
 * Interface DefaultConnectionPinger
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public class DefaultConnectionPinger extends AbstractConnectionPinger {

    /**
     * Constructor DefaultConnectionPinger
     *
     * @param pingIntervalSeconds
     * @param giveupIntervalSeconds
     */
    public DefaultConnectionPinger(int pingIntervalSeconds, int giveupIntervalSeconds) {
        super(pingIntervalSeconds, giveupIntervalSeconds);
    }

    /**
     * Constructor DefaultConnectionPinger
     *
     * @param pingIntervalMilliSeconds
     * @param giveupIntervalMilliSeconds
     */
    public DefaultConnectionPinger(long pingIntervalMilliSeconds, long giveupIntervalMilliSeconds) {
        super(pingIntervalMilliSeconds, giveupIntervalMilliSeconds);
    }


    /**
     * Constructor DefaultConnectionPinger
     */
    public DefaultConnectionPinger() {
    }

    /**
     * Implemnt the abstract ping() from the AbstractConnectionPinger
     */
    protected void ping() {

        if (getInvocationHandler().getLastRealRequest() > (System.currentTimeMillis() - (getGiveupInterval()))) {
            getInvocationHandler().ping();
        } else {

            //TODO should be restartable after reconnect of socket.
            stop();

            // if more than 100 seconds since last request, stop pinging
            // Let the server do a disconnect according to its rules.
        }
    }
}
