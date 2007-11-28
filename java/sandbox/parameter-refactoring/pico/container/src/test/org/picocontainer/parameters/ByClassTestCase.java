/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.parameters;

import java.util.Iterator;
import java.util.Collection;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;


/**
 * test by-class lookup
 * @author Konstantin Pribluda
 *
 */
public class ByClassTestCase extends TestCase {

	public void testComponentResolution() {
		MutablePicoContainer parent = new DefaultPicoContainer();
		MutablePicoContainer container = new DefaultPicoContainer(parent);
		
		container.addComponent(25,25);
		container.addComponent(37,37);
		container.addComponent("foo bar");
		container.addComponent(Integer.class);
		
		parent.addComponent(239,239);
		parent.addComponent(717.28);
		
		ByClass byClass = new ByClass(Integer.class);

		// shall retrieve all the integers from both contaienrs
		assertEquals(3,byClass.lookup(container).size());
		
		// parent container shall be first, so map 
		// conversion will override keys 
		Iterator iter = byClass.lookup(container).iterator();
		assertEquals(239,((ComponentAdapter)iter.next()).getComponentKey());
		assertEquals(25,((ComponentAdapter)iter.next()).getComponentKey());
		assertEquals(37,((ComponentAdapter)iter.next()).getComponentKey());
	}
	
	/**
	 * registration order shall be preserved
	 */
	public void testThatByClassPreservesRegistrationOrder() {
		MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent("first", "first");
        pico.addComponent("second", "second");
        pico.addComponent("third", "third");
        pico.addComponent("fourth", "fourth");
        pico.addComponent("fifth", "fifth");

        final Collection<ComponentAdapter> strings = (new ByClass(String.class)).lookup(pico);
        Iterator<ComponentAdapter> iter = strings.iterator();
        assertEquals("first", iter.next().getComponentInstance(pico));
        assertEquals("second", iter.next().getComponentInstance(pico));
        assertEquals("third", iter.next().getComponentInstance(pico));
        assertEquals("fourth",iter.next().getComponentInstance(pico));
        assertEquals("fifth", iter.next().getComponentInstance(pico));

	}
}
