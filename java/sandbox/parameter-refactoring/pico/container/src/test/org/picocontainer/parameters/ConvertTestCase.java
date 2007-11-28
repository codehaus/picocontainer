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

import java.util.Collection;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import junit.framework.TestCase;

/**
 * test that converter works properly
 * @author Konstantin Pribluda
 */
public class ConvertTestCase extends TestCase {
	
	public void testNotStringIsNotResolved() {
		Convert convert = new Convert(new Lookup() {

			public Collection<ComponentAdapter> lookup(PicoContainer container) {

				return null;
			}}, Integer.class);
	}
}
