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
import picocontainer.PicoInstantiationException;

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
                                                     IllegalAccessException,
                                                     InstantiationException {
        picocontainer.defaults.DefaultComponentFactory dcd = new picocontainer.defaults.DefaultComponentFactory();
        Object decorated = dcd.createComponent(Object.class, Object.class, null, null);
        assertNotNull(decorated);
        //TODO check no methods were called, via proxy ?

    }


}