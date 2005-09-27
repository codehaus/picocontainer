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
package org.nanocontainer.remoting.test.classretrievers;

import junit.framework.TestCase;
import org.nanocontainer.remoting.client.Factory;
import org.nanocontainer.remoting.client.factories.ServerSideClassFactory;
import org.nanocontainer.remoting.client.transports.piped.PipedCustomStreamHostContext;
import org.nanocontainer.remoting.common.DefaultThreadPool;
import org.nanocontainer.remoting.server.authenticators.DefaultAuthenticator;
import org.nanocontainer.remoting.server.classretrievers.AbstractDynamicGeneratorClassRetriever;
import org.nanocontainer.remoting.server.classretrievers.BcelDynamicGeneratorClassRetriever;
import org.nanocontainer.remoting.server.monitors.NullServerMonitor;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.DefaultServerSideClientContextFactory;
import org.nanocontainer.remoting.server.transports.piped.PipedCustomStreamServer;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * A special test case that tests dynamic class generation on the server side.
 *
 * @author Paul Hammant
 */
public class ClassRetrievingTestCase extends TestCase {

    protected AbstractServer server;
    protected TestImpl testServer;
    protected TestInterface testClient;

    public ClassRetrievingTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        // server side setup.
        AbstractDynamicGeneratorClassRetriever dyncgen = new BcelDynamicGeneratorClassRetriever();
        server = new PipedCustomStreamServer(dyncgen, new DefaultAuthenticator(), new NullServerMonitor(), new DefaultThreadPool(), new DefaultServerSideClientContextFactory());
        testServer = new TestImpl();
        server.publish(testServer, "Kewl", TestInterface.class);
        dyncgen.generate("Kewl", TestInterface.class, this.getClass().getClassLoader());

        server.start();

        // For piped, server and client can see each other
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream();
        ((PipedCustomStreamServer) server).makeNewConnection(in, out);

        // Client side setup
        Factory af = new ServerSideClassFactory(new PipedCustomStreamHostContext.WithSimpleDefaults(in, out), false);
        testClient = (TestInterface) af.lookup("Kewl");

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


    /**
     * This is the only testXX() method in this class.  Other features of
     * NanoContainer Remoting are given a thorough test in the 'test' package.
     *
     * @throws Exception as per Junit contract
     */
    public void testDynamicallyGeneratedProxyMethodInvocation() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        // Invoke a method over rpc.
        testClient.method0();

        // test the server has logged the message.
        assertEquals("called", (testServer).getStoredState("method0"));
    }


}
