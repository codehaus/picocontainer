/*****************************************************************************
 * Copyright (c) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.nanocontainer.jmx;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;
import org.nanocontainer.testmodel.WilmaImpl;
import org.nanocontainer.jmx.AbstractNanoMXTestCase;

public class NanoMXComponentFactoryTestCase extends AbstractNanoMXTestCase {

    public void testClassAndKeyRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        pico.registerComponent("nano:name=one", WilmaImpl.class);
        pico.registerComponent("nano:name=two", WilmaImpl.class);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 2, pico.getComponents().size());

        assertExistsInJMX(pico, "nano:name=one");
        assertExistsInJMX(pico, "nano:name=two");
    }

    public void testClassOnlyRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        pico.registerComponentByClass(WilmaImpl.class);

        pico.instantiateComponents();

        assertEquals("Wrong number of comps in the container", 1, pico.getComponents().size());

        assertExistsInJMX(pico, "nanomx:type=" + WilmaImpl.class.getName());
    }

    public void testClassAndNullKeyRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        pico.registerComponent(null, WilmaImpl.class);

        try {
            pico.instantiateComponents();
            fail("should have thrown NanoMXInitializationException");
        }
        catch (NanoMXInitializationException e) {
            // worked
        }

    }

    public void testObjectAndNullKeyRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        try {
            pico.registerComponent(null, new WilmaImpl());
            fail("should have thrown NullPointerException");
        }
        catch (NullPointerException e) {
            // worked
        }

        pico.instantiateComponents();
    }

}
