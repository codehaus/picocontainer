/*****************************************************************************
 * Copyright (c) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.picoextras.jmx;

import org.picoextras.testmodel.WilmaImpl;

public class NanoMXComponentFactoryTestCase extends AbstractNanoMXTestCase {

    public void testClassAndKeyRegistration() throws Exception {
        NanoMXContainer pico = new NanoMXContainer();

        pico.registerComponentImplementation("nano:name=one", WilmaImpl.class);
        pico.registerComponentImplementation("nano:name=two", WilmaImpl.class);

        assertEquals("Wrong number of comps in the internals", 2, pico.getComponentInstances().size());
        assertExistsInJMX(pico.getMBeanServer(), "nano:name=one");
        assertExistsInJMX(pico.getMBeanServer(), "nano:name=two");
    }

    public void testClassOnlyRegistration() throws Exception {
        NanoMXContainer pico = new NanoMXContainer();

        pico.registerComponentImplementation(WilmaImpl.class);

        assertEquals("Wrong number of comps in the internals", 1, pico.getComponentInstances().size());

        assertExistsInJMX(pico.getMBeanServer(), "nanomx:type=" + WilmaImpl.class.getName());
    }

    public void testObjectAndNullKeyRegistration() throws Exception {
        NanoMXContainer pico = new NanoMXContainer();

        try {
            pico.registerComponentInstance(null, new WilmaImpl());
            fail("should have thrown NullPointerException");
        } catch (NullPointerException e) {
            // worked
        }
    }

}
