/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.           *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                             *
 *****************************************************************************/

package org.microcontainer.jmx;

import org.picocontainer.MutablePicoContainer;

import java.util.List;

/**
 * Basically a bean which is passed when building
 *
 * @author Michael Ward
 * @version $Revision$
 */
public class JmxDefinition {

	private String key;
	private List operations;
	private MutablePicoContainer picoContainer;

	public JmxDefinition(MutablePicoContainer picoContainer) {
		this.picoContainer = picoContainer;
	}

	public MutablePicoContainer getPicoContainer() {
		return picoContainer;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List getOperations() {
		return operations;
	}

	public void setOperations(List operations) {
		this.operations = operations;
	}
}
