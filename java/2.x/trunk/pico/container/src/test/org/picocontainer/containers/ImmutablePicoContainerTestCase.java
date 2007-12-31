/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.containers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.NameBinding;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.tck.MockFactory;


/**
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
@RunWith(JMock.class)
public class ImmutablePicoContainerTestCase {

	private Mockery mockery = MockFactory.mockeryWithCountingNamingScheme();
	
    @Test public void testImmutingOfNullBarfs() {
        try {
            new ImmutablePicoContainer(null);
            fail("Should have barfed");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test public void testVisitingOfImmutableContainerWorks() {
        final DefaultPicoContainer pico = new DefaultPicoContainer();
        Object foo = new Object();
        final ComponentAdapter componentAdapter = pico.addComponent(foo).getComponentAdapter(foo.getClass(), (NameBinding) null);

        final PicoVisitor fooVisitor = mockery.mock(PicoVisitor.class);
        mockery.checking(new Expectations() {{
            one(fooVisitor).visitContainer(with(same(pico)));
        	one(fooVisitor).visitComponentAdapter(with(same(componentAdapter)));
        }});
        PicoContainer ipc = new ImmutablePicoContainer(pico);
        ipc.accept(fooVisitor);
    }

    @Test public void testProxyEquals() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        PicoContainer ipc = new ImmutablePicoContainer(pico);
        assertEquals(ipc, ipc);
        assertEquals(ipc, new ImmutablePicoContainer(pico));
    }

    @Test public void testHashCodeIsSame() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        assertEquals(pico.hashCode(), new ImmutablePicoContainer(pico).hashCode());
    }
    
    @Test public void testDoesNotEqualsToNull() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        PicoContainer ipc = new ImmutablePicoContainer(pico);
        assertFalse(ipc.equals(null));
    }
}
