/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.annotations;

import org.picocontainer.annotations.AnnotablePicoContainer;
import org.picocontainer.annotations.Inject;

public class Cat implements CatSoul {
	
	@Inject
	private Mouse mouse;

	@Inject
	private AnnotablePicoContainer container;
	
	public Mouse getMouse() {
		return mouse;
	}

	public AnnotablePicoContainer getContainer() {
		return container;
	}
	
}
