/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.ejb.rmi.test;

import junit.framework.TestCase;
import org.nanocontainer.ejb.rmi.mock.EJBHomeMock;
import org.nanocontainer.ejb.rmi.mock.EJBObjectMock;
import org.nanocontainer.ejb.rmi.mock.PortableRemoteObjectDelegateMock;

import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import java.util.HashMap;
import java.util.Map;


/**
 * Test for the Mock :)
 * @author J&ouml;rg Schaible
 */
public class PortableRemoteObjectDelegateMockTest
        extends TestCase {

    /** Simple EJB interface */
    public static interface Hello
            extends EJBObject {
        
        /** @return Returns &quot;Hello World!&quot; */
        public String getHelloWorld();
    }

    /** EJB Home interface */
    public static interface HelloHome
            extends EJBHome {

        /** @return Returns the EJB */
        public Hello create();
    }

    /** EJB implementation */
    public static class HelloImpl
            extends EJBObjectMock
            implements Hello {

        /** @return Returns &quot;Hello World!&quot; */
        public String getHelloWorld() {
            return "Hello World!";
        }
    }

    /** EJB Home implementation */
    public static class HelloHomeImpl
            extends EJBHomeMock
            implements HelloHome {

        /** Default ctor. */
        public HelloHomeImpl() {
            super();
        }

        /**
         * Non-valid ctor with arg.
         * @param foo An argument.
         */
        public HelloHomeImpl(final String foo) {
            super();
            if (foo == null)
            ;
        }

        /** @return Returns the EJB */
        public Hello create() {
            return new HelloImpl();
        }
    }

    /** EJB Home iinterface */
    public static interface FooHome
            extends EJBHome {
        /** @return Returns the EJB */
        public Hello create();
    }

    /**
     * Test the narrow method.
     * @throws Exception
     */

    public final void testNarrow() throws Exception {
        final Map narrowMap = new HashMap();
        narrowMap.put("Hello", HelloHomeImpl.class.getConstructor(null));
        PortableRemoteObjectDelegateMock.setNarrowMap(narrowMap);
        final PortableRemoteObjectDelegateMock delegate = new PortableRemoteObjectDelegateMock();
        
        final HelloHome helloHome = (HelloHome)delegate.narrow("Hello", HelloHome.class);
        assertNotNull(helloHome);
        final Hello hello = helloHome.create();
        assertEquals("Hello World!", hello.getHelloWorld());
        
        try {
            delegate.narrow("Hello", FooHome.class);
            fail("Should have thrown.");
        } catch (ClassCastException expected) {
        }

        try {
            delegate.narrow("Hello", HelloHomeImpl.class);
            fail("Should have thrown.");
        } catch (ClassCastException expected) {
        }

        narrowMap.put("Hello", HelloHomeImpl.class.getConstructor(new Class[]{String.class}));
        try {
            delegate.narrow("Hello", HelloHome.class);
            fail("Should have thrown.");
        } catch (ClassCastException expected) {
        }
        assertNull(delegate.narrow("foo", HelloHome.class));
        
        narrowMap.put("Hello", String.class.getConstructor(null));
        try {
            delegate.narrow("Hello", HelloHome.class);
            fail("Should have thrown.");
        } catch (ClassCastException expected) {
        }
    }
}

