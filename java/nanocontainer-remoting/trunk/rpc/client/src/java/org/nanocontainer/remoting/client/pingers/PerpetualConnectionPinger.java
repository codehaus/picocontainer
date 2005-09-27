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

import org.nanocontainer.remoting.commands.PingRequest;

/**
 * Interface PerpetualConnectionPinger
 * <p/>
 * Pings every ten seconds.
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public class PerpetualConnectionPinger extends AbstractConnectionPinger {

    /**
     * Constructor PerpetualConnectionPinger
     *
     * @param pingIntervalSeconds
     * @param giveupIntervalSeconds
     */
    public PerpetualConnectionPinger(int pingIntervalSeconds, int giveupIntervalSeconds) {
        super(pingIntervalSeconds, giveupIntervalSeconds);
    }

    /**
     * Constructor PerpetualConnectionPinger
     */
    public PerpetualConnectionPinger() {
    }

    protected void ping() {
        getInvocationHandler().handleInvocation(new PingRequest());
    }
}
