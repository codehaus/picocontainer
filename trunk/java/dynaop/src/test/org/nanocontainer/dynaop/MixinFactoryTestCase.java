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

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Stephen Molitor
 */
public class MixinFactoryTestCase extends TestCase {

    public void testCreate() {
        MutablePicoContainer container = new DefaultPicoContainer();

        Object mixin = "myMixin";
        container.registerComponentInstance("mixinComponentKey", mixin);

        PicoMixinFactory mixinFactory = new PicoMixinFactory(container,
                        "mixinComponentKey");
        assertSame(mixin, mixinFactory.create(null));
    }

    public void testMixinNotFoundInContainer() {
        MutablePicoContainer container = new DefaultPicoContainer();
        PicoMixinFactory mixinFactory = new PicoMixinFactory(container,
                        "mixinComponentKey");

        try {
            mixinFactory.create(null);
            fail("NullPointerException should have been raised");
        } catch (NullPointerException e) {
            // expected
        }
    }

}