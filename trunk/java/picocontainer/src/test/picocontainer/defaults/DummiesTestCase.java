/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import junit.framework.TestCase;
import picocontainer.PicoContainer;
import picocontainer.PicoInitializationException;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.PicoInitializationException;
import picocontainer.composite.CompositePicoContainer;

import java.lang.reflect.InvocationTargetException;

public class DummiesTestCase extends TestCase {

    public void testDummyContainer() {
        picocontainer.defaults.NullContainer dc = new picocontainer.defaults.NullContainer();
        assertFalse(dc.hasComponent(String.class));
        assertNull(dc.getComponent(String.class));
        assertEquals(0, dc.getComponents().length);
    }

    public void testDefaultComponentFactory() throws PicoInstantiationException,
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException, PicoIntrospectionException {
        DefaultComponentFactory dcd = new DefaultComponentFactory();
        Object decorated = dcd.createComponent(new ComponentSpecification(dcd, Object.class, Object.class), null);
        assertNotNull(decorated);
    }

    public void testInstantiation() throws PicoInitializationException  {
        CompositePicoContainer acc = new CompositePicoContainer(new PicoContainer[0]);
        // Should not barf. Should no nothing, but that hard to test.
        acc.instantiateComponents();
    }
}