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
package org.nanocontainer.remoting.test.mismatch;

import junit.framework.TestCase;
import org.nanocontainer.remoting.client.factories.ClientSideClassFactory;
import org.nanocontainer.remoting.client.transports.rmi.RmiHostContext;
import org.nanocontainer.remoting.client.transports.socket.SocketCustomStreamHostContext;
import org.nanocontainer.remoting.client.transports.socket.SocketObjectStreamHostContext;
import org.nanocontainer.remoting.common.BadConnectionException;
import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.transports.rmi.RmiServer;
import org.nanocontainer.remoting.server.transports.socket.CompleteSocketCustomStreamServer;
import org.nanocontainer.remoting.server.transports.socket.CompleteSocketObjectStreamServer;
import org.nanocontainer.remoting.test.TestInterface;
import org.nanocontainer.remoting.test.TestInterface2;
import org.nanocontainer.remoting.test.TestInterface3;
import org.nanocontainer.remoting.test.TestInterfaceImpl;

/**
 * Test Custom Stream over sockets.
 *
 * @author Paul Hammant
 */
public class SocketMismatchTestCase extends TestCase {


    public SocketMismatchTestCase(String name) {
        super(name);
    }

    public void testCustomStreamObjectStreamMismatch() throws Exception {

        // server side setup.
        CompleteSocketCustomStreamServer server = new CompleteSocketCustomStreamServer.WithSimpleDefaults(12001);
        TestInterfaceImpl testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        ClientSideClassFactory factory = null;
        try {

            // Client side setup
            factory = new ClientSideClassFactory(new SocketObjectStreamHostContext.WithSimpleDefaults("127.0.0.1", 12001), false);
            TestInterface testClient = (TestInterface) factory.lookup("Hello");

            // just a kludge for unit testing given we are intrinsically dealing with
            // threads, NanoContainer Remoting being a client/server thing
            Thread.yield();

            testClient.hello("hello");
            fail("CustomStreams and ObjectStreams cannot interoperate");
        } catch (BadConnectionException bce) {
            // expected.
        } finally {

            System.gc();
            Thread.yield();

            try {
                factory.close();
            } catch (Exception e) {
            }
            Thread.yield();
            server.stop();
            Thread.yield();
        }
    }

    public void dont_testObjectStreamCustomStreamMismatch() throws Exception {

        // server side setup.
        CompleteSocketObjectStreamServer server = new CompleteSocketObjectStreamServer.WithSimpleDefaults(12002);
        TestInterfaceImpl testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        ClientSideClassFactory factory = null;
        try {

            // Client side setup
            factory = new ClientSideClassFactory(new SocketCustomStreamHostContext.WithSimpleDefaults("127.0.0.1", 12002), false);
            TestInterface testClient = (TestInterface) factory.lookup("Hello");

            // just a kludge for unit testing given we are intrinsically dealing with
            // threads, NanoContainer Remoting being a client/server thing
            Thread.yield();

            testClient.hello("hello");
            fail("CustomStreams and ObjectStreams cannot interoperate");
        } catch (BadConnectionException bce) {
            // expected.
        } finally {

            System.gc();
            Thread.yield();

            try {
                factory.close();
            } catch (Exception e) {
            }
            Thread.yield();
            server.stop();
            Thread.yield();
        }
    }


    public void dont_testCustomStreamRmiMismatch() throws Exception {

        // server side setup.
        CompleteSocketCustomStreamServer server = new CompleteSocketCustomStreamServer.WithSimpleDefaults(12003);
        TestInterfaceImpl testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        ClientSideClassFactory factory = null;
        try {

            // Client side setup
            factory = new ClientSideClassFactory(new RmiHostContext.WithSimpleDefaults("127.0.0.1", 12003), false);
            TestInterface testClient = (TestInterface) factory.lookup("Hello");

            // just a kludge for unit testing given we are intrinsically dealing with
            // threads, NanoContainer Remoting being a client/server thing
            Thread.yield();

            testClient.hello("hello");
            fail("CustomStreams and RMI trasnports cannot interoperate");
        } catch (BadConnectionException bce) {
            // expected.
        } finally {

            System.gc();
            Thread.yield();

            try {
                factory.close();
            } catch (Exception e) {
            }
            Thread.yield();
            server.stop();
            Thread.yield();
        }
    }

    public void dont_testRmiCustomStreamMismatch() throws Exception {

        // server side setup.
        RmiServer server = new RmiServer.WithSimpleDefaults(12004);
        TestInterfaceImpl testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        ClientSideClassFactory factory = null;
        try {

            // Client side setup
            factory = new ClientSideClassFactory(new SocketCustomStreamHostContext.WithSimpleDefaults("127.0.0.1", 12004), false);
            TestInterface testClient = (TestInterface) factory.lookup("Hello");

            // just a kludge for unit testing given we are intrinsically dealing with
            // threads, NanoContainer Remoting being a client/server thing
            Thread.yield();

            testClient.hello("hello");
            fail("CustomStreams and RMI trasnports cannot interoperate");
        } catch (BadConnectionException bce) {
            // expected.
        } finally {

            System.gc();
            Thread.yield();

            try {
                factory.close();
            } catch (Exception e) {
            }
            Thread.yield();
            server.stop();
            Thread.yield();
        }
    }

}
