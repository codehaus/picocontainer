/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.nanocontainer.proxytoys;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class AssimilatingComponentAdapterTest extends TestCase {

    public static interface Foo {
        int size();
    }

    public static class Bar {
        public int size() {
            return 1;
        }
    }

    public void testInstanceIsBorged() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponent(new AssimilatingComponentAdapter(Foo.class, new Bar()));
        final Foo foo = (Foo)mpc.getComponentInstanceOfType(Foo.class);
        assertEquals(1, foo.size());
    }
}
