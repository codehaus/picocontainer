/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.Parameter;

public class ImplementationHidingPicoContainerTestCase extends AbstractPicoContainerTestCase {

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new ImplementationHidingPicoContainer();
    }

    public void testInstanceIsNotAutomaticallyHidden() {
        ImplementationHidingPicoContainer pc = new ImplementationHidingPicoContainer();
        pc.registerComponentInstance(Map.class, new HashMap());
        Map map = (Map) pc.getComponentInstance(Map.class);
        assertNotNull(map);
        assertTrue(map instanceof HashMap);
    }

    public void testImplementaionIsAutomaticallyHidden() {
        ImplementationHidingPicoContainer pc = new ImplementationHidingPicoContainer();
        pc.registerComponentImplementation(Map.class, HashMap.class);
        Map map = (Map) pc.getComponentInstance(Map.class);
        assertNotNull(map);
        assertFalse(map instanceof HashMap);
    }

    public void testNonInterfaceImplementaionIsAutomaticallyHidden() {
        ImplementationHidingPicoContainer pc = new ImplementationHidingPicoContainer();
        pc.registerComponentImplementation(HashMap.class, HashMap.class);
        Map map = (Map) pc.getComponentInstance(HashMap.class);
        assertNotNull(map);
        assertTrue(map instanceof HashMap);
    }


    public void testImplementaionWithParametersIsAutomaticallyHidden() {
        ImplementationHidingPicoContainer pc = new ImplementationHidingPicoContainer();
        pc.registerComponentImplementation(Map.class, HashMap.class, new Parameter[0]);
        Map map = (Map) pc.getComponentInstance(Map.class);
        assertNotNull(map);
        assertFalse(map instanceof HashMap);
    }

    public void testSerializedContainerCanRetrieveImplementation() throws PicoException, PicoInitializationException,
            IOException, ClassNotFoundException {
        try {
            super.testSerializedContainerCanRetrieveImplementation();
            fail("The ImplementationHidingPicoContainer should not be able to retrieve the component impl");
        } catch(ClassCastException cce) {
            // expected.
        }
    }

}
