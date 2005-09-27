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
package org.nanocontainer.remoting.test.invalidstate;

import junit.framework.TestCase;
import org.nanocontainer.remoting.client.ClientInvocationHandler;
import org.nanocontainer.remoting.client.HostContext;
import org.nanocontainer.remoting.client.NoSuchSessionException;
import org.nanocontainer.remoting.client.factories.ClientSideClassFactory;
import org.nanocontainer.remoting.client.transports.socket.SocketCustomStreamHostContext;
import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.PublicationException;
import org.nanocontainer.remoting.server.ServerException;
import org.nanocontainer.remoting.server.transports.socket.CompleteSocketCustomStreamServer;
import org.nanocontainer.remoting.test.TestInterface;
import org.nanocontainer.remoting.test.TestInterface2;
import org.nanocontainer.remoting.test.TestInterface3;
import org.nanocontainer.remoting.test.TestInterfaceImpl;


/**
 * Tests concerning the bouncing of a server.
 *
 * @author Paul Hammant
 */
public class BouncingServerTestCase extends TestCase {

    public BouncingServerTestCase(String name) {
        super(name);
    }

    public void testBouncingOfServerCausesClientProblems() throws Exception {

        // server side setup.
        CompleteSocketCustomStreamServer server = startServer();

        ClientSideClassFactory factory = null;
        try {

            // Client side setup
            HostContext hostContext = new SocketCustomStreamHostContext.WithSimpleDefaults("127.0.0.1", 12201);
            factory = new ClientSideClassFactory(hostContext, false);
            ClientInvocationHandler ih = hostContext.getInvocationHandler();
            TestInterface testClient = (TestInterface) factory.lookup("Hello");

            // just a kludge for unit testing given we are intrinsically dealing with
            // threads, NanoContainer Remoting being a client/server thing
            Thread.yield();

            testClient.hello2(100);

            // Stop server and restarting (essentially binning sessions).
            server.stop();
            server = startServer();

            try {
                testClient.hello2(123);
                fail("Should have barfed with NoSuchSessionException");
            } catch (NoSuchSessionException e) {
                // expected
            }


        } finally {
            System.gc();
            Thread.yield();
            factory.close();
            Thread.yield();
            server.stop();
            Thread.yield();
        }
    }

    private CompleteSocketCustomStreamServer startServer() throws ServerException, PublicationException {
        CompleteSocketCustomStreamServer server = new CompleteSocketCustomStreamServer.WithSimpleDefaults(12201);
        TestInterfaceImpl testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();
        return server;
    }

}
