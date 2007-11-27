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

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import junit.framework.TestCase;

/**
 * test by key resolver
 * @author Konstantin Pribluda
 *
 */
public class ByKeyTestCase extends TestCase {
	
	
	public void testByKeyResolution() {
		MutablePicoContainer parent = new DefaultPicoContainer();
		MutablePicoContainer child = new DefaultPicoContainer(parent);
		
		parent.addComponent(new Integer(239));

		
		ByKey byInt = new ByKey(Integer.class);
		ByKey invalid = new ByKey("dlkfjsd fkldsj");
		

		assertEquals(1,byInt.lookup(child).size());
		assertEquals(1,byInt.lookup(parent).size());
		assertEquals(0,invalid.lookup(parent).size());
		assertEquals(0,invalid.lookup(child).size());
		
		assertEquals(Integer.class,byInt.lookup(child).iterator().next().getComponentKey());
	}
}
