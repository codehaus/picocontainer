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
package org.nanocontainer.remoting.test.socket;

import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.transports.socket.CompleteSocketCustomStreamServer;
import org.nanocontainer.remoting.test.AbstractHelloTestCase;
import org.nanocontainer.remoting.test.TestInterface;
import org.nanocontainer.remoting.test.TestInterface2;
import org.nanocontainer.remoting.test.TestInterface3;
import org.nanocontainer.remoting.test.TestInterfaceImpl;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;


/**
 * Test Custom Stream over sockets, using JNDI on the client side ( a small change ).
 *
 * @author Paul Hammant
 */
public class CustomStreamJNDITestCase extends AbstractHelloTestCase {

    private Context jndiContext;

    public CustomStreamJNDITestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        // server side setup.
        server = new CompleteSocketCustomStreamServer.WithSimpleDefaults(10006);
        testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        // Client side setup
        // JNDI lookup.  Note there are no imports of NanoContainer Remoting classes in this test.
        Hashtable env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.nanocontainer.remoting.client.impl.naming.DefaultInitialContextFactory");
        env.put(Context.PROVIDER_URL, "nanocontainer-remoting://localhost:10006/SocketCustomStream");
        env.put("proxy.type", "ClientSideClasses");
        env.put("bean.type", "NotBeanOnly");
        env.put("optimize", "false");

        jndiContext = new InitialContext(env);

        testClient = (TestInterface) jndiContext.lookup("Hello");

        // just a kludge for unit testing given we are intrinsically dealing with
        // threads, NanoContainer Remoting being a client/server thing
        Thread.yield();
    }

    protected void tearDown() throws Exception {
        testClient = null;
        System.gc();
        Thread.yield();
        jndiContext.close();
        Thread.yield();
        server.stop();
        Thread.yield();
        server = null;
        testServer = null;
        super.tearDown();
    }


}
