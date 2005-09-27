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
package org.nanocontainer.remoting.test.dynamic;

import junit.framework.TestCase;
import org.nanocontainer.remoting.client.HostContext;
import org.nanocontainer.remoting.client.NotPublishedException;
import org.nanocontainer.remoting.client.impl.DynamicInvoker;
import org.nanocontainer.remoting.client.transports.socket.SocketCustomStreamHostContext;
import org.nanocontainer.remoting.common.ConnectionException;
import org.nanocontainer.remoting.server.PublicationDescription;
import org.nanocontainer.remoting.server.transports.AbstractServer;
import org.nanocontainer.remoting.server.transports.socket.CompleteSocketCustomStreamServer;
import org.nanocontainer.remoting.test.TestInterface;
import org.nanocontainer.remoting.test.TestInterface2;
import org.nanocontainer.remoting.test.TestInterface3;
import org.nanocontainer.remoting.test.TestInterfaceImpl;

/**
 * Test case for the stubless invoker of remote methods
 *
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 */
public class DynamicInvokerTestCase extends TestCase {

    //-------Variables------------//
    protected AbstractServer server;
    protected TestInterfaceImpl testServer;
    protected TestInterface testClient;
    protected HostContext hostContext;
    protected DynamicInvoker dynamicInvoker;
    //-------Constructor----------//
    /**
     * Cons.
     *
     * @param name
     */
    public DynamicInvokerTestCase(String name) {
        super(name);
    }

    //-------TestCase overrides-----//

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        // server side setup.
        server = new CompleteSocketCustomStreamServer.WithSimpleDefaults(10001);
        testServer = new TestInterfaceImpl();
        PublicationDescription pd = new PublicationDescription(TestInterface.class, new Class[]{TestInterface3.class, TestInterface2.class});
        server.publish(testServer, "Hello", pd);
        server.start();

        // Client side setup
        hostContext = new SocketCustomStreamHostContext.WithSimpleDefaults("127.0.0.1", 10001);
        dynamicInvoker = new DynamicInvoker(hostContext);

        // just a kludge for unit testing given we are intrinsically dealing with
        // threads, NanoContainer Remoting being a client/server thing
        Thread.yield();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */

    protected void tearDown() throws Exception {
        testClient = null;
        System.gc();
        Thread.yield();
        dynamicInvoker.close(); //close@client
        Thread.yield();
        server.stop(); //close@server
        Thread.yield();
        server = null;
        testServer = null;
        super.tearDown();
    }

    //-------test cases------------//
    /**
     * A very simple test
     */
    public void testInvocation() throws ConnectionException {

        // Invoking the methods returning void
        dynamicInvoker.invoke("Hello", "hello(java.lang.String)", new Object[]{"Hello!?"}, new Class[]{String.class});
        // test the server has logged the message.
        assertEquals("Hello!?", ((TestInterfaceImpl) testServer).getStoredState("void:hello(String)"));

        //invoke a method returning primitive type
        Integer ret = (Integer) dynamicInvoker.invoke("Hello", "hello2(int)", new Object[]{new Integer(11)}, new Class[]{Integer.TYPE});
        assertEquals(ret, new Integer(11));

        // Invoke on a  non-existent remote object
        try {
            dynamicInvoker.invoke("Hellooo?", "some method", null, null);
            fail("Dynamic Invoker should have failed");
        } catch (NotPublishedException e) {
            // expected
        }


    }

    public void testListMethods() {
        String[] methods = dynamicInvoker.listOfMethods("Hello");
        assertNotNull(methods);
        assertTrue(methods.length > 0);

        methods = dynamicInvoker.listOfMethods("does not exist");
        assertNotNull(methods);
        assertTrue(methods.length == 0);

    }


    public void testList() {
        String[] publications = dynamicInvoker.list();
        assertNotNull(publications);
        assertTrue(publications.length > 0);
        assertEquals("Hello", publications[0]);
    }


    /**
     * test methods with multiple arguments
     */

    public void testMultiArgumentMethodInvocation() throws Exception {
        /* NanoContainer Remoting right now expects method signature in a specific format.
         * within the MethodRequest.namely with  arguments spaced out by
         * a comma+space.
         *         e.g. hello4(float, double) )
         */
        //Here we test a case where the signature is developed liberally

        StringBuffer buf = (StringBuffer) dynamicInvoker.invoke("Hello", "hello4(float,double)", new Object[]{new Float(10.12), new Double(10.13)}, new Class[]{Float.TYPE, Double.TYPE});
        assertEquals("10.12 10.13", buf.toString());

        buf = (StringBuffer) dynamicInvoker.invoke("Hello", "  hello4  (  float    ,     double   )  ", new Object[]{new Float(10.15), new Double(10.17)}, new Class[]{Float.TYPE, Double.TYPE});
        assertEquals("10.15 10.17", buf.toString());
    }
}
