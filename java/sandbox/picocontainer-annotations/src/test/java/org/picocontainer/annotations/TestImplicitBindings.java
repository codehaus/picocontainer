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

public class TestImplicitBindings {

	@Test
	public void testImplicitBinding()  throws Exception {
		AnnotablePicoContainer pico = new DefaultAnnotablePicoContainer();
		CatSoul cat = pico.getComponent(CatSoul.class);
		assertNotNull(cat);
		assertNotNull(cat.getMouse());	
	}	
}
