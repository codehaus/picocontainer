/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.dynaop;

import java.net.URL;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import dynaop.remote.Home;

/**
 * @author Stephen Molitor
 */
public class RemoteComponentAdapterTestCase extends TestCase {

    static class MockHome extends Home {
        URL urlPassed;
        Class intfPassed;

        public Object create(URL url, Class intf) {
            urlPassed = url;
            intfPassed = intf;
            return "mock";
        }
    }

    public void testGetComponentInstance() throws Exception {
        MockHome home = new MockHome();
        URL url = new URL("http://url");

        ComponentAdapter adapter = new RemoteComponentAdapter(url,
                        String.class, "key", home);
        Object remoteComponent = adapter.getComponentInstance();
        assertEquals("mock", remoteComponent);
        assertSame(home.urlPassed, url);
        assertSame(home.intfPassed, String.class);
    }

    public void testProperties() throws Exception {
        URL url = new URL("http://url");
        ComponentAdapter adapter = new RemoteComponentAdapter(url,
                        String.class, "key");
        assertEquals("key", adapter.getComponentKey());
        assertEquals(String.class, adapter.getComponentImplementation());

        PicoContainer container = new DefaultPicoContainer();
        assertNull(adapter.getContainer());
        adapter.setContainer(container);
        assertSame(container, adapter.getContainer());
    }

}