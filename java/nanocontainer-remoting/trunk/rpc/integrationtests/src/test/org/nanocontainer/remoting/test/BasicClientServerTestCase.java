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

import junit.framework.TestCase;
import org.nanocontainer.remoting.client.ConnectionRefusedException;
import org.nanocontainer.remoting.client.factories.ClientSideClassFactory;
import org.nanocontainer.remoting.client.transports.rmi.RmiHostContext;
import org.nanocontainer.remoting.client.transports.socket.SocketCustomStreamHostContext;
import org.nanocontainer.remoting.client.transports.socket.SocketObjectStreamHostContext;
import org.nanocontainer.remoting.common.BadConnectionException;
import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.transports.socket.CompleteSocketCustomStreamServer;
import org.nanocontainer.remoting.server.transports.socket.CompleteSocketObjectStreamServer;


/**
 * Test basic client server features.
 *
 * @author Paul Hammant
 */
public class BasicClientServerTestCase extends TestCase {

    public BasicClientServerTestCase(String name) {
        super(name);
    }

    public void testNoServer() throws Exception {
        try {
            new ClientSideClassFactory(new SocketCustomStreamHostContext.WithSimpleDefaults("127.0.0.1", 12345), false);
            fail("Should have have failed.");
        } catch (ConnectionRefusedException e) {
            // what we expetcted
        }
    }

    public void testMismatch1() throws Exception {

        // server side setup.
        CompleteSocketCustomStreamServer server = new CompleteSocketCustomStreamServer.WithSimpleDefaults(12346);

        TestInterfaceImpl testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        // Client side setup
        try {

            new ClientSideClassFactory(new SocketObjectStreamHostContext.WithSimpleDefaults("127.0.0.1", 12346), false);
            fail("Expected mismatch exception");
        } catch (BadConnectionException e) {
            if (e.getMessage().indexOf("mismatch") < 0) {
                throw e;
            }
        } finally {
            //server.stop();
        }

    }

    public void donttestMismatch2() throws Exception {

        // server side setup.
        CompleteSocketObjectStreamServer server = new CompleteSocketObjectStreamServer.WithSimpleDefaults(12347);
        TestInterfaceImpl testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();


        // Client side setup
        try {
            new ClientSideClassFactory(new SocketCustomStreamHostContext.WithSimpleDefaults("127.0.0.1", 12347), false);
            fail("Expected mismatch exception");
        } catch (BadConnectionException e) {
            if (e.getMessage().indexOf("mismatch") < 0) {
                throw e;
            }

        } finally {
            server.stop();
        }
    }

    public void donttestMismatch3() throws Exception {

        // server side setup.
        CompleteSocketCustomStreamServer server = new CompleteSocketCustomStreamServer.WithSimpleDefaults(12348);
        TestInterfaceImpl testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        // Client side setup
        try {
            new ClientSideClassFactory(new RmiHostContext.WithSimpleDefaults("127.0.0.1", 12348), false);
            fail("Expected mismatch exception");
        } catch (BadConnectionException e) {
            if (e.getMessage().indexOf("mismatch") < 0) {
                throw e;
            }

        } finally {
            server.stop();
        }
    }

}
