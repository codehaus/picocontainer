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

import org.nanocontainer.testmodel.WilmaImpl;

public class NanoMXComponentFactoryTestCase extends AbstractNanoMXTestCase {

    public void testClassAndKeyRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        pico.registerComponent("nano:name=one", WilmaImpl.class);
        pico.registerComponent("nano:name=two", WilmaImpl.class);

        assertEquals("Wrong number of comps in the internals", 2, pico.getComponents().size());

        assertExistsInJMX(pico, "nano:name=one");
        assertExistsInJMX(pico, "nano:name=two");
    }

    public void testClassOnlyRegistration() throws Exception {
        NanoMXContainer pico = createNanoMXContainer();

        pico.registerComponentByClass(WilmaImpl.class);

        assertEquals("Wrong number of comps in the internals", 1, pico.getComponents().size());

        assertExistsInJMX(pico, "nanomx:type=" + WilmaImpl.class.getName());
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
    }

}
