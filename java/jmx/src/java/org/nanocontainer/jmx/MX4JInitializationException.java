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

import org.picocontainer.PicoInitializationException;

/**
 * A registration exception caused trying to register the component with JMX
 * 
 * @author James Strachan
 * @author Mauro Talevi
 * @version $Revision$
 */
public class MX4JInitializationException extends PicoInitializationException {
    public MX4JInitializationException() {
    }

    public MX4JInitializationException(String message) {
        super(message);
    }

    public MX4JInitializationException(Throwable cause) {
        super(cause);
    }

    public MX4JInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
