/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan and Mauro Talevi                          *
 *****************************************************************************/
package org.picoextras.jmx;

import org.picocontainer.PicoRegistrationException;

/**
 * A registration exception caused trying to register the component with JMX
 * 
 * @author James Strachan
 * @author Mauro Talevi
 * @author Jeppe Cramon
 * @version $Revision$
 */
public class NanoMXRegistrationException extends PicoRegistrationException {
	public NanoMXRegistrationException() {
		super();
	}

	public NanoMXRegistrationException(String message) {
		super(message);
	}


	public NanoMXRegistrationException(String message, Exception cause) {
		super(message, cause);
	}

    public NanoMXRegistrationException(Throwable cause) {
        super(cause);
    }
}
