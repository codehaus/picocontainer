/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.picocontainer.groovy;

/**
 * Exception thrown due to invalid GroovyMarkup when creating pico containers
 * and components
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
public class PicoBuilderException extends RuntimeException {

    public PicoBuilderException(String message) {
        super(message);
    }

    public PicoBuilderException(String message, Exception e) {
        super(message, e);
    }
}
