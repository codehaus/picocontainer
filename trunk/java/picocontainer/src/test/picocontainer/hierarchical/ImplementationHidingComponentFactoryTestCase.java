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

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import picocontainer.hierarchical.ImplementationHidingComponentFactory;
import picocontainer.PicoInvocationTargetStartException;

public class ImplementationHidingComponentFactoryTestCase extends TestCase {

    public void testBasic() throws NoSuchMethodException, PicoInvocationTargetStartException {
        ImplementationHidingComponentFactory cf = new ImplementationHidingComponentFactory();
        Constructor ctor = ArrayList.class.getConstructor(new Class[0]);
        Object o = cf.createComponent(List.class, ctor, new Object[0]);
        assertTrue(o instanceof List);
        assertFalse(o instanceof ArrayList);
        ((List) o).add("hello");
    }

}
