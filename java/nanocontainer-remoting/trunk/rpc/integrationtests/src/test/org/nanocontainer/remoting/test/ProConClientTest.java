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

import org.nanocontainer.remoting.client.Factory;
import org.nanocontainer.remoting.client.HostContext;
import org.nanocontainer.remoting.client.factories.ClientSideClassFactory;
import org.nanocontainer.remoting.client.factories.ServerSideClassFactory;
import org.nanocontainer.remoting.client.transports.socket.SocketCustomStreamHostContext;
import org.nanocontainer.remoting.client.transports.socket.SocketObjectStreamHostContext;

/**
 * Class ProConClientTest
 *
 * @author Vinay Chandrasekharan
 */
public class ProConClientTest {

    /**
     * Method main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        System.out.println("Stream over Socket Client");

        HostContext arhc;

        if (args[1].equals("ObjectStream")) {
            System.out.println("(Object Stream)");

            arhc = new SocketObjectStreamHostContext.WithSimpleDefaults("127.0.0.1", 1234);
        } else {
            System.out.println("(Custom Stream)");

            arhc = new SocketCustomStreamHostContext.WithSimpleDefaults("127.0.0.1", 1235);
        }

        Factory af = null;

        if (args[0].equals("S")) {
            af = new ServerSideClassFactory(arhc, false);
        } else {
            af = new ClientSideClassFactory(arhc, false);
        }

        //list
        System.out.println("Listing Published Objects At Server...");

        String[] listOfPublishedObjectsOnServer = af.list();

        for (int i = 0; i < listOfPublishedObjectsOnServer.length; i++) {
            System.out.println("..[" + i + "]:" + listOfPublishedObjectsOnServer[i]);
        }

        TestProvider tpi = (TestProvider) af.lookup("P");
        TestConsumer tci = (TestConsumer) af.lookup("C");

        System.out.println("Provider.getName(0)" + tpi.getName(0));
        System.out.println("Consumer.getProviderName(0)" + tci.getProviderName(tpi));
        af.close();
    }
}
