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

import org.picocontainer.PicoRegistrationException;

/**
 * A registration exception caused trying to register the component with JMX
 *
 * @author Michael Ward
 * @version $Revision$
 */
public class JMXRegistrationException extends PicoRegistrationException {

	public JMXRegistrationException(final String message) {
		super(message);
	}

	public JMXRegistrationException(final Throwable cause) {
		super(cause);
	}

	public JMXRegistrationException(String message, Throwable cause) {
		super(message, cause);
	}
}
