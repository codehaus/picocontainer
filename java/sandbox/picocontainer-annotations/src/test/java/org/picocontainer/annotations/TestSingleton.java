/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.annotations;

import org.junit.Test;
import org.picocontainer.annotations.DefaultAnnotablePicoContainer;
import org.picocontainer.annotations.AnnotablePicoContainer;

import static org.junit.Assert.*;

public class TestSingleton {

	@Test
	public void testCanRegisterSingleton()  throws Exception {
		AnnotablePicoContainer pico = new DefaultAnnotablePicoContainer();
		pico.registerComponentImplementation(Mouse.class);
		pico.registerComponentImplementation(Cat.class);
		pico.registerComponentImplementation(SingleCat.class);
		SingleCat scat1 = pico.getComponent(SingleCat.class);
		SingleCat scat2 = pico.getComponent(SingleCat.class);
		assertSame(scat1, scat2);
		CatSoul cat1 = pico.getComponent(Cat.class);
		CatSoul cat2 = pico.getComponent(Cat.class);
		assertNotSame(cat1, cat2);
		
	}
}
