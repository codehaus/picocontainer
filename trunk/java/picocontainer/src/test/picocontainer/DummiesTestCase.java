/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package picocontainer;

import junit.framework.TestCase;

public class DummiesTestCase extends TestCase
 {

    public void testDummyContainer() {
        DummyContainer dc = new DummyContainer();
        assertFalse(dc.hasComponent(String.class));
        assertNull(dc.getComponent(String.class));
        assertEquals(0,dc.getComponents().length);
    }

    public void testDummyStartableLifecycleManager() throws PicoStartException, PicoStopException {
        DummyStartableLifecycleManagerImpl ds = new DummyStartableLifecycleManagerImpl();
        Object o = new Object();
        ds.startComponent(o);
        ds.stopComponent(o);
        //TODO check no methods were called, via proxy ?
    }

    public void testDummyComponentDecorator() throws PicoStartException, PicoStopException {
        DummyComponentDecorator dcd = new DummyComponentDecorator();
        Object o = new Object();
        Object decorated = dcd.decorateComponent(Object.class, o);
        assertEquals(o, decorated);
        //TODO check no methods were called, via proxy ?

    }




}