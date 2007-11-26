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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import junit.framework.TestCase;

/**
 * test parameter resolution by key class
 * @author Konstantin Prbluda
 */
public class ByKeyClassTestCase extends TestCase {

	public void testByKeyClassResolution() {
		
		MutablePicoContainer parent = new DefaultPicoContainer();
		MutablePicoContainer container = new DefaultPicoContainer(parent);
		
		
		parent.addComponent(25,new ArrayList());
		parent.addComponent("glum glam",new HashMap());
		container.addComponent(76,new HashSet());
		container.addComponent(83,new TreeSet());
		
		
		ByKeyClass byInt = new ByKeyClass(Integer.class);
		ByKeyClass byString = new ByKeyClass(String.class);
		
		// shall pick up correct key types
		assertEquals(3,byInt.lookup(container).size());
		assertEquals(1,byString.lookup(container).size());
		
		// shall cache result
		
		
	}
}
