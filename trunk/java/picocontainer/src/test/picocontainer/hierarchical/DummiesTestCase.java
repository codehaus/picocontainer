/*****************************************************************************
 * Copyright (C) ClassRegistrationPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.hierarchical;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;

import picocontainer.defaults.NullContainer;
import picocontainer.defaults.NullLifecycleManager;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.PicoStartException;
import picocontainer.PicoStopException;

public class DummiesTestCase extends TestCase {

    public void testDummyContainer() {
        picocontainer.defaults.NullContainer dc = new picocontainer.defaults.NullContainer();
        assertFalse(dc.hasComponent(String.class));
        assertNull(dc.getComponent(String.class));
        assertEquals(0, dc.getComponents().length);
    }

    public void testDummyStartableLifecycleManager() throws PicoStartException, PicoStopException {
        picocontainer.defaults.NullLifecycleManager ds = new picocontainer.defaults.NullLifecycleManager();
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
        picocontainer.defaults.DefaultComponentFactory dcd = new picocontainer.defaults.DefaultComponentFactory();
        Object decorated = dcd.createComponent(Object.class, Object.class.getConstructor(null), null);
        assertNotNull(decorated);
        //TODO check no methods were called, via proxy ?

    }


}