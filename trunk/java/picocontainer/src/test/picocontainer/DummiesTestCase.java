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

import java.lang.reflect.InvocationTargetException;

public class DummiesTestCase extends TestCase {

    public void testDummyContainer() {
        NullContainer dc = new NullContainer();
        assertFalse(dc.hasComponent(String.class));
        assertNull(dc.getComponent(String.class));
        assertEquals(0, dc.getComponents().length);
    }

    public void testDummyStartableLifecycleManager() throws PicoStartException, PicoStopException {
        NullStartableLifecycleManager ds = new NullStartableLifecycleManager();
        Object o = new Object();
        ds.startComponent(o);
        ds.stopComponent(o);
        //TODO check no methods were called, via proxy ?
    }

    public void testDefaultComponentFactory() throws PicoStartException,
                                                     PicoStopException,
                                                     NoSuchMethodException,
                                                     InvocationTargetException,
                                                     IllegalAccessException,
                                                     InstantiationException {
        DefaultComponentFactory dcd = new DefaultComponentFactory();
        Object decorated = dcd.createComponent(Object.class, Object.class.getConstructor(null), null);
        assertNotNull(decorated);
        //TODO check no methods were called, via proxy ?

    }


}