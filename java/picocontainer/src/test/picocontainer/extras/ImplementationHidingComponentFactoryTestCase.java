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
import java.lang.reflect.Constructor;
import picocontainer.PicoInvocationTargetInitailizationException;
import picocontainer.extras.ImplementationHidingComponentFactory;

public class ImplementationHidingComponentFactoryTestCase extends TestCase {

    public void testBasic() throws NoSuchMethodException, PicoInvocationTargetInitailizationException {
        ImplementationHidingComponentFactory cf = new ImplementationHidingComponentFactory();
        Object o = cf.createComponent(List.class, ArrayList.class, null, null);
        assertTrue(o instanceof List);
        assertFalse(o instanceof ArrayList);
        ((List) o).add("hello");
    }

}
