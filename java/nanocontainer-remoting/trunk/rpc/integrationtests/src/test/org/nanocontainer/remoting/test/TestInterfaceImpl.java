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
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * Class TestInterfaceImpl
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @author Benjamin David Hall
 * @version $Revision: 1.3 $
 */
public class TestInterfaceImpl implements TestInterface {

    Vector ti2Holder = new Vector();
    TestObject[] m_testObjects;

    Vector listeners = new Vector();

    HashMap storedState = new HashMap();

    public Object getStoredState(String key) {
        return storedState.get(key);
    }

    public void hello(String greeting) {
        storedState.put("void:hello(String)", greeting);
    }

    public int hello2(int greeting) {
        storedState.put("int:hello2(int)", "" + greeting);
        return greeting;
    }

    public boolean hello3(short greeting) throws PropertyVetoException, IOException {
        storedState.put("boolean:hello3(short)", "" + greeting);
        switch (greeting) {
            case 90:
                throw new PropertyVetoException("Forced Exception Test", null);
            case 91:
                throw new IOException("Forced Exception");
        }
        return true;
    }

    public StringBuffer hello4(float greeting1, double greeting2) {
        StringBuffer sb = new StringBuffer("" + greeting1 + " " + greeting2);
        storedState.put("StringBuffer:hello4(float,double)", sb);
        return sb;
    }

    public void testSpeed() {
        // do nothing
    }

    /**
     * Method makeTestInterface2
     *
     * @param thingName
     * @return
     */
    public TestInterface2 makeTestInterface2(String thingName) {

        TestInterface2 ti2;
        if (thingName.equals("abc")) {
            // even calls only
            ti2 = new TestInterface3Impl(new Date(), thingName);
        } else {
            ti2 = new TestInterface2Impl(thingName);
        }

        ti2Holder.add(ti2);

        return ti2;
    }

    /**
     * Method morphName
     *
     * @param forThisImpl
     */
    public void morphName(TestInterface2 forThisImpl) {

        String name = forThisImpl.getName();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < name.length(); i++) {
            sb.append(name.substring(i, i + 1).toUpperCase());
            sb.append("_");
        }

        forThisImpl.setName(sb.toString());
    }

    /**
     * Method findTestInterface2ByName
     *
     * @param nameToFind
     * @return
     */
    public TestInterface2 findTestInterface2ByName(String nameToFind) {

        for (int i = 0; i < ti2Holder.size(); i++) {
            TestInterface2 ti2 = (TestInterface2) ti2Holder.elementAt(i);

            if (ti2.getName().equals(nameToFind)) {
                return ti2;
            }
        }

        return new TestInterface2Impl("Not Found");
    }

    /**
     * Method getTestInterfaces
     *
     * @return
     */
    public TestInterface2[] getTestInterface2s() {

        TestInterface2[] retVal = new TestInterface2[ti2Holder.size()];

        for (int i = 0; i < ti2Holder.size(); i++) {
            TestInterface2 interface2 = (TestInterface2) ti2Holder.elementAt(i);

            retVal[i] = interface2;
        }

        return retVal;
    }

    /**
     * Method getTestObjects
     * Helps ilustrate the bug http://developer.java.sun.com/developer/bugParade/bugs/4499841.html
     *
     * @return
     */
    public TestObject[] getTestObjects() {

        if (m_testObjects == null) {
            m_testObjects = new TestObject[3];
            m_testObjects[0] = new TestObject("AAA");
            m_testObjects[1] = new TestObject("BBB");
            m_testObjects[2] = new TestObject("CCC");
        }

        return m_testObjects;
    }

    /**
     * Method changeTestObjectNames
     * Helps ilustrate the bug http://developer.java.sun.com/developer/bugParade/bugs/4499841.html
     */
    public void changeTestObjectNames() {

        m_testObjects[0].setName("aaa");
        m_testObjects[1].setName("bbb");
        m_testObjects[2].setName("ccc");
    }

    /**
     * Method makeNewTestObjectNames
     * Helps ilustrate the bug http://developer.java.sun.com/developer/bugParade/bugs/4499841.html
     */
    public void makeNewTestObjectNames() {

        m_testObjects[0] = new TestObject("aAa");
        m_testObjects[1] = new TestObject("bBb");
        m_testObjects[2] = new TestObject("cCc");
    }

    protected void finalize() throws Throwable {
        super.finalize();
        //System.out.println( "impl finalized" );
    }

    public boolean addCallBackListener(TestCallBackListener testCallbackListener) {

        listeners.add(testCallbackListener);
        return true;
    }

    public void ping() {
        for (int i = 0; i < listeners.size(); i++) {
            TestCallBackListener testCallBackListener = (TestCallBackListener) listeners.elementAt(i);
            testCallBackListener.serverCallingClient("Ping!");
            //testCallBackListener.serverCallingClient2(this);
        }
    }

    public byte bytes(byte b, byte[] array) {
        storedState.put("byte:bytes(byte, byte[]#1)", "" + b);
        storedState.put("byte:bytes(byte, byte[]#2)", array);
        byte val = 0;
        for (int i = 0; i < array.length; i++) {
            val += array[i];
        }
        return (byte) ((b * 2) + val);
    }

    public void throwSpecialException(int i) {
        if (i == 0) {
            throw new RuntimeException("hello");
        } else if (i == 1) {
            throw new Error("world");
        }
    }

    public void testLong(long l) {
    }

    public String toString() {
        return "YeeeeHaaaa";
    }

    public CustomSerializableParam testCustomSerializableParameter(CustomSerializableParam param) {
        return param;
    }

}
