/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                    		 *
 *****************************************************************************/

package org.nanocontainer.jmx;

import org.picocontainer.PicoVerificationException;

import java.util.List;
import java.util.ArrayList;

/**
 * An MBean's associated MBeanInfo is not registered with the container
 *
 * @author Michael Ward
 * @version $Revision$
 */
public class MBeanInfoMissingException extends PicoVerificationException {

	public MBeanInfoMissingException() {
		this(new ArrayList());
	}

	public MBeanInfoMissingException(Object nested) {
		this();
		getNestedExceptions().add(nested);
	}

	public MBeanInfoMissingException(List nestedExceptions) {
		super(nestedExceptions);
	}
}
