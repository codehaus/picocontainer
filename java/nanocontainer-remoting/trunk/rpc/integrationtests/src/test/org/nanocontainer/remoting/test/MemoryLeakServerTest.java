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
package org.nanocontainer.remoting.test;

import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.socket.CompleteSocketCustomStreamServer;

/**
 * Class MemoryLeakServerTest
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.2 $
 */
public class MemoryLeakServerTest {

    /**
     * Method main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        System.out.println("MemoryLeak Server");

        AbstractServer as = new CompleteSocketCustomStreamServer.WithSimpleDefaults(1277);
        MemoryLeakImpl ml = new MemoryLeakImpl();

        as.publish(ml, "MemLeak", new PublicationDescription(MemoryLeak.class, MemoryLeak.class));
        as.start();
    }
}
