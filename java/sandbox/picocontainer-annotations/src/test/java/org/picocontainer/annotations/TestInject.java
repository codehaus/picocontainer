/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.annotations;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestInject {
	
	@Test
	public void testCanRegisterComponents() throws Exception {
		AnnotablePicoContainer pico = new DefaultAnnotablePicoContainer();
		pico.registerComponentImplementation(Mouse.class);
		pico.registerComponentImplementation(Cat.class);
		CatSoul cat = pico.getComponent(Cat.class);
		assertNotNull(cat.getMouse());
	}
	
	@Test
	public void testCanInjectComponents() throws Exception {
		AnnotablePicoContainer pico = new DefaultAnnotablePicoContainer();
		pico.registerComponentImplementation(Cat.class);
		CatSoul cat = pico.getComponent(Cat.class);
		assertNotNull(cat.getMouse());
	}	
	
	@Test
	public void testInjectContainer() throws Exception {
		AnnotablePicoContainer pico = new DefaultAnnotablePicoContainer();
		pico.registerComponentImplementation(Mouse.class);
		pico.registerComponentImplementation(Cat.class);
		Cat cat = pico.getComponent(Cat.class);
		assertNotNull(cat.getMouse());
		assertEquals(pico, cat.getContainer());
	}	

}
