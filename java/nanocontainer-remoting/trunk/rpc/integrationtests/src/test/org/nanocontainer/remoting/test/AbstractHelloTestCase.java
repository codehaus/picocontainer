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

import java.beans.PropertyVetoException;
import java.io.IOException;

/**
 * Extended by classes that name the transport.
 *
 * @author Paul Hammant
 * @author Benjamin David Hall
 */
public abstract class AbstractHelloTestCase extends AbstractNanoContainerRemotingTestCase {

    public AbstractHelloTestCase(String name) {
        super(name);
    }

    public void testHello2Call() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        // Invoke a method over rpc.
        int retVal = testClient.hello2(11);

        // test our returned result
        assertEquals(11, retVal);

        // test the server has logged the message.
        assertEquals("11", ((TestInterfaceImpl) testServer).getStoredState("int:hello2(int)"));
    }

    /**
     * Test the bytes method on the TestInterface.
     *
     * @throws Exception
     */
    public void testBytes() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        // Invoke a method over rpc.
        byte retVal = testClient.bytes((byte) 5, new byte[]{1, 2});

        // test our returned result
        assertEquals(13, retVal);

        // test the server has logged the message.
        assertEquals("5", ((TestInterfaceImpl) testServer).getStoredState("byte:bytes(byte, byte[]#1)"));
    }

    /**
     * Test throwing special exceptions.
     *
     * @throws Exception
     */
    public void testThrowSpecialException() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        try {
            // Invoke a method over rpc.
            testClient.throwSpecialException(0);
            fail();
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().equals("hello"));
        }

        try {
            // Invoke a method over rpc.
            testClient.throwSpecialException(1);
            fail();
        } catch (Error e) {
            assertTrue(e.getMessage().equals("world"));
        }
    }

    public void testHello3CallPart1() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        // Invoke a method over rpc.
        boolean retVal = testClient.hello3((short) 22);

        // test our returned result
        assertTrue(retVal);

        // test the server has logged the message.
        assertEquals("22", ((TestInterfaceImpl) testServer).getStoredState("boolean:hello3(short)"));
    }


    public void testHello3CallPart2() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        // Invoke a method over rpc.
        try {
            testClient.hello3((short) 90);
            fail("Expected a Excaption to be throw for hardcoded test 90");
        } catch (PropertyVetoException e) {
            // expected
        } catch (IOException e) {
            fail("Wrong exception throw for hardcoded test 90");
        }

        // test the server has logged the message.
        assertEquals("90", ((TestInterfaceImpl) testServer).getStoredState("boolean:hello3(short)"));
    }

    public void testHello3CallPart3() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        // Invoke a method over rpc.
        try {
            testClient.hello3((short) 91);
            fail("Expected a Exception to be throw for hardcoded test 91");
        } catch (PropertyVetoException e) {
            fail("Wrong exception throw for hardcoded test 91");
        } catch (IOException e) {
            // expected
        }

        // test the server has logged the message.
        assertEquals("91", ((TestInterfaceImpl) testServer).getStoredState("boolean:hello3(short)"));
    }

    public void testHello4Call() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        // Invoke a method over rpc.
        StringBuffer sb = testClient.hello4((float) 10.2, (double) 11.9);
        StringBuffer sb2 = (StringBuffer) ((TestInterfaceImpl) testServer).getStoredState("StringBuffer:hello4(float,double)");

        // test the server has logged the message.
        assertEquals("10.2 11.9", sb2.toString());
        // test if the same instance.
        //assertEquals(sb, sb2);
    }

    public void testBasicAdditionalFacade() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        TestInterface2 xyz = testClient.makeTestInterface2("XYZ");

        assertEquals("XYZ", xyz.getName());

        xyz.setName("123");

        assertEquals("123", xyz.getName());


    }

    public void testAdditionalFacadeFunctionality() throws Exception {
        // lookup worked ?
        assertNotNull(testClient);

        TestInterface3 abc = (TestInterface3) testClient.makeTestInterface2("abc");
        TestInterface2 def = testClient.makeTestInterface2("def");

        testClient.morphName(abc);

        assertEquals("A_B_C_", abc.getName());

        TestInterface2 def2 = testClient.findTestInterface2ByName("def");

        assertNotNull(def2);
        assertTrue(def == def2);

        TestInterface2[] ti2s = testClient.getTestInterface2s();

        assertNotNull(ti2s);

        assertEquals("Array of returned testInterface2s should be two", 2, ti2s.length);

        for (int i = 0; i < ti2s.length; i++) {
            TestInterface2 ti2 = ti2s[i];
            assertNotNull(ti2);
        }

    }

    public void testBugParadeBugNumber4499841() throws Exception {
        TestObject[] tos = testClient.getTestObjects();
        testClient.changeTestObjectNames();
        TestObject[] tos2 = testClient.getTestObjects();
        for (int i = 0; i < tos.length; i++) {
            TestObject to = tos[i];
            TestObject to2 = tos2[i];
            if (testForBug4499841) {
                assertEquals(to.getName().toLowerCase(), to2.getName());
            }
        }
    }

    public void testSpeed() throws Exception {

        int iterations = 10000; // default
        String iterationsStr = "@SPEEDTEST-ITERATIONS@";
        try {
            iterations = Integer.parseInt(iterationsStr);
        } catch (NumberFormatException e) {
            // half expected.  The @SPEEDTEST-ITERATIONS@ thing above may be replaced by the
            // the Ant task before the test is run.  However this may be run in a
            // IDE, in which case the test is not run.
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            testClient.testSpeed();
        }
        long end = System.currentTimeMillis();
        //System.out.println("--> ST " + this.getClass().getName() + " " + iterations + " " + ((end - start)/1000) );


    }

    public void testToString() {

        // lookup worked ?
        assertNotNull(testClient);

        // Invoke a method over rpc.
        String retVal = testClient.toString();

        // test our returned result
        assertEquals("YeeeeHaaaa", retVal);

    }

    public void testEquals() {
        assertTrue(!testClient.equals(null));
        assertTrue(!testClient.equals("ha!"));

        TestInterface2 one = testClient.makeTestInterface2("equals-test-one");
        TestInterface2 two = testClient.makeTestInterface2("equals-test-two");

        // These seem to contradict at first glance, but it is what we want.
        assertFalse(one == two);
        assertTrue(one.equals(two));


    }

    public void testLongParamMethod() {

        testClient.testLong((long) 1);

    }

    public void testCustomSerializableParameter() {
        // lookup worked ?
        assertNotNull(testClient);

        CustomSerializableParam sendParam = new CustomSerializableParam();
        sendParam.name = "sent-by-caller";
        CustomSerializableParam recvParam = testClient.testCustomSerializableParameter(sendParam);
        //test receipt of serialized value object from server
        assertNotNull(recvParam);
        //check whether its the same as one sent (server merely echos back whatever it received)
        assertEquals(sendParam.name, recvParam.name);
    }

}
