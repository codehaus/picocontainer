/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picoextras.script.xml;

import org.picoextras.script.PicoCompositionException;

/**
 * @author Jeppe Cramon
 */
public class XmlFrontEndException extends PicoCompositionException {

    protected XmlFrontEndException() {
        super();
    }

	/**
	 * @param cause
	 */
	public XmlFrontEndException(Throwable cause) {
		super(cause);
	}

    public XmlFrontEndException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlFrontEndException(String message) {
        super(message);
    }

}
