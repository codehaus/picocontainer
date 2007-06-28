/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by the committers                                           *
 *****************************************************************************/

package org.picocontainer.alternatives;

import junit.framework.TestCase;

import org.picocontainer.PicoContainer;

/**
 * @author Mauro Talevi
 * @version $Revision:  $
 */
public class EmptyPicoContainerTestCase extends TestCase {

    public void testReturnValues() {
        PicoContainer pico = new EmptyPicoContainer();
        assertNull(pico.getComponentAdapter(null));
        assertNull(pico.getComponentAdapterOfType(null));
        assertTrue(pico.getComponentAdapters().isEmpty());
        assertTrue(pico.getComponentAdaptersOfType(null).isEmpty());
        assertNull(pico.getComponentInstance(null));
        assertNull(pico.getComponentInstanceOfType(null));
        assertTrue(pico.getComponentInstances().isEmpty());
        assertTrue(pico.getComponentInstancesOfType(null).isEmpty());
        assertNull(pico.getParent());
    }

    public void testVisitorAndLifecycleMethodsProduceNoResult() {
        PicoContainer pico = new EmptyPicoContainer();
        pico.verify();
        pico.accept(null);
        pico.start();
        pico.stop();
        pico.dispose();
    }
}
