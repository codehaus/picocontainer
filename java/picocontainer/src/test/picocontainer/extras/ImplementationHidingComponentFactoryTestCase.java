/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.extras;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.extras.ImplementationHidingComponentFactory;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoIntrospectionException;

public class ImplementationHidingComponentFactoryTestCase extends TestCase {

    private static boolean addCalled = false;

    public static class OneConstructorArrayList extends ArrayList {
        public OneConstructorArrayList() {
            super();
        }

        public boolean add(Object o) {
            addCalled = true;
            return super.add(o);
        }
    }

    public void testBasic() throws NoSuchMethodException, PicoInstantiationException, PicoIntrospectionException {
        ImplementationHidingComponentFactory cf = new ImplementationHidingComponentFactory(new DefaultComponentFactory());
        Object o = cf.createComponent(List.class, OneConstructorArrayList.class, null, null);
        assertTrue(o instanceof List);
        assertFalse(o instanceof OneConstructorArrayList);
        ((List) o).add("hello");
        assertTrue("Add was called", addCalled);
    }

}
