/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

import junit.framework.TestCase;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.WilmaImpl;
import picocontainer.hierarchical.HierarchicalPicoContainer;

/**
 * Can Pico host itself ?
 */
public class PicoPicoTestCase extends TestCase {

    public void testDefaultPicoContainer() throws PicoRegistrationException, PicoInitializationException {

        HierarchicalPicoContainer pc = new HierarchicalPicoContainer.Default();
        pc.registerComponent(HierarchicalPicoContainer.Default.class);
        pc.initializeContainer();

        tryDefaultPicoContainer((HierarchicalPicoContainer) pc.getComponent(HierarchicalPicoContainer.Default.class));

    }

    private void tryDefaultPicoContainer(HierarchicalPicoContainer pc2) throws PicoRegistrationException, PicoInitializationException {

        pc2.registerComponent(FredImpl.class);
        pc2.registerComponent(WilmaImpl.class);

        pc2.initializeContainer();

        assertTrue( "There should have been a Fred in the container", pc2.hasComponent( FredImpl.class ) );
        assertTrue( "There should have been a Wilma in the container", pc2.hasComponent( WilmaImpl.class ) );

    }

}
