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
package org.nanocontainer.remoting.test.piped;

import org.nanocontainer.remoting.client.factories.ClientSideClassFactory;
import org.nanocontainer.remoting.client.transports.piped.PipedObjectStreamHostContext;
import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.transports.piped.PipedObjectStreamServer;
import org.nanocontainer.remoting.test.AbstractHelloTestCase;
import org.nanocontainer.remoting.test.TestInterface;
import org.nanocontainer.remoting.test.TestInterface2;
import org.nanocontainer.remoting.test.TestInterface3;
import org.nanocontainer.remoting.test.TestInterfaceImpl;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Test Piped Trasnport (Object Stream)
 *
 * @author Paul Hammant
 */
public class PipedObjectStreamTestCase extends AbstractHelloTestCase {


    public PipedObjectStreamTestCase(String name) {
        super(name);

        // See http://developer.java.sun.com/developer/bugParade/bugs/4499841.html
        // This bug prevents ObjectStream from functioning correctly when used
        // by NanoContainer Remoting.  You can still use the ObjectStream transports, but
        // should be aware of the limitations.  See testBugParadeBugNumber4499841()
        // in the parent class.
        testForBug4499841 = false;

    }

    protected void setUp() throws Exception {
        super.setUp();

        // server side setup.
        server = new PipedObjectStreamServer.WithSimpleDefaults();
        testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        // For piped, server and client can see each other
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream();
        ((PipedObjectStreamServer) server).makeNewConnection(in, out);

        // Client side setup
        factory = new ClientSideClassFactory(new PipedObjectStreamHostContext.WithSimpleDefaults(in, out), false);
        testClient = (TestInterface) factory.lookup("Hello");

        // just a kludge for unit testing given we are intrinsically dealing with
        // threads, NanoContainer Remoting being a client/server thing
        Thread.yield();
    }

    protected void tearDown() throws Exception {

        testClient = null;
        System.gc();
        Thread.yield();
        factory.close();
        Thread.yield();
        server.stop();
        Thread.yield();
        server = null;
        testServer = null;
        super.tearDown();
    }


}
