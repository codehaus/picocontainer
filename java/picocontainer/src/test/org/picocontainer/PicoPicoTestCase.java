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
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;


/**
 * Can Pico host itself ?
 */
public class PicoPicoTestCase extends TestCase {

    public void testDefaultPicoContainer() throws PicoException, PicoInitializationException {

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(DefaultPicoContainer.class);

        MutablePicoContainer hostedPico = (MutablePicoContainer) pico.getComponentInstance(DefaultPicoContainer.class);
        hostedPico.registerComponentImplementation(DependsOnTouchable.class);
        hostedPico.registerComponentImplementation(SimpleTouchable.class);

        assertTrue(hostedPico.hasComponent(DependsOnTouchable.class));
        assertTrue(hostedPico.hasComponent(SimpleTouchable.class));

    }

}
