/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer;

import junit.framework.TestCase;
import org.picocontainer.tck.DependsOnTouchable;
import org.picocontainer.tck.SimpleTouchable;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * Can Pico host itself ?
 */
public class PicoPicoTestCase extends TestCase {

    public void testDefaultPicoContainer() throws PicoRegistrationException, PicoInitializationException {

        DefaultPicoContainer pc = new DefaultPicoContainer.Default();
        pc.registerComponentByClass(DefaultPicoContainer.Default.class);
        pc.instantiateComponents();

        tryDefaultPicoContainer((DefaultPicoContainer) pc.getComponent(DefaultPicoContainer.Default.class));

    }

    private void tryDefaultPicoContainer(DefaultPicoContainer pc2) throws PicoRegistrationException, PicoInitializationException {

        pc2.registerComponentByClass(DependsOnTouchable.class);
        pc2.registerComponentByClass(SimpleTouchable.class);

        pc2.instantiateComponents();

        assertTrue("There should have been a Fred in the container", pc2.hasComponent(DependsOnTouchable.class));
        assertTrue("There should have been a Touchable in the container", pc2.hasComponent(SimpleTouchable.class));
    }
}
