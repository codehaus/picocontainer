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
package org.nanocontainer.remoting.test.rmi;

import junit.framework.TestCase;
import org.nanocontainer.remoting.client.Factory;
import org.nanocontainer.remoting.client.factories.ClientSideClassFactory;
import org.nanocontainer.remoting.client.transports.rmi.RmiHostContext;
import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.rmi.RmiServer;
import org.nanocontainer.remoting.test.TestInterface;
import org.nanocontainer.remoting.test.TestInterface2;
import org.nanocontainer.remoting.test.TestInterface3;
import org.nanocontainer.remoting.test.TestInterfaceImpl;


/**
 * Test RMI transport
 * <p/>
 * This test only contains a single tesXXX() method because of
 * http://developer.java.sun.com/developer/bugParade/bugs/4267864.html
 *
 * @author Paul Hammant
 */
public class RmiTestCase extends TestCase {

    private AbstractServer server;
    private TestInterfaceImpl testServer;
    private TestInterface testClient;

    public RmiTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        // server side setup.
        server = new RmiServer.WithSimpleDefaults(10003);
        testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        // Client side setup
        Factory af = new ClientSideClassFactory(new RmiHostContext.WithSimpleDefaults("127.0.0.1", 10003), false);
        testClient = (TestInterface) af.lookup("Hello");

        // just a kludge for unit testing given we are intrinsically dealing with
        // threads, NanoContainer Remoting being a client/server thing
        Thread.yield();
    }

    protected void tearDown() throws Exception {
        server.stop();
        Thread.yield();
        server = null;
        testServer = null;
        super.tearDown();
    }

    public void testSpeed() throws Exception {

        for (int i = 1; i < 10000; i++) {
            testClient.testSpeed();
        }

    }

}
