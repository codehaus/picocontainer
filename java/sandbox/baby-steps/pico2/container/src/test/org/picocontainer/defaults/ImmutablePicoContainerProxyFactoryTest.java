/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
public class ImmutablePicoContainerProxyFactoryTest extends MockObjectTestCase {

    public void testImmutingOfNullBarfs() {
        try {
            ImmutablePicoContainerProxyFactory.newProxyInstance(null);
            fail("Should have barfed");
        } catch (NullPointerException e) {
            // expected
        }
    }


    public void testVisitingOfImmutableContainerWorks() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        Object foo = new Object();
        ComponentAdapter componentAdapter = pico.registerComponent(foo);

        Mock fooVisitor = new Mock(PicoVisitor.class);
        fooVisitor.expects(once()).method("visitContainer").with(same(pico));
        fooVisitor.expects(once()).method("visitComponentAdapter").with(same(componentAdapter));

        PicoContainer ipc = ImmutablePicoContainerProxyFactory.newProxyInstance(pico);
        ipc.accept((PicoVisitor)fooVisitor.proxy());
    }

    public void testProxyEquals() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        PicoContainer ipc = ImmutablePicoContainerProxyFactory.newProxyInstance(pico);
        assertEquals(ipc, ipc);
        assertEquals(ipc, ImmutablePicoContainerProxyFactory.newProxyInstance(pico));
    }

    public void testHashCodeIsSame() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        PicoContainer ipc = ImmutablePicoContainerProxyFactory.newProxyInstance(pico);
        assertEquals(ipc.hashCode(), ImmutablePicoContainerProxyFactory.newProxyInstance(pico).hashCode());
    }
    
    public void testDoesNotEqualsToNull() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        PicoContainer ipc = ImmutablePicoContainerProxyFactory.newProxyInstance(pico);
        assertFalse(ipc.equals(null));
    }
}
