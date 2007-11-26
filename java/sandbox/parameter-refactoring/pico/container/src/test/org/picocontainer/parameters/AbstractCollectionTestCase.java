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

import java.util.Arrays;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.adapters.InstanceAdapter;

import junit.framework.TestCase;

public class AbstractCollectionTestCase extends TestCase {

	protected List<ComponentAdapter> adapters = Arrays.asList(new ComponentAdapter[] {
				new InstanceAdapter<String>("foo", "bar"),
				new InstanceAdapter<String>("baz", "bang") });

}
