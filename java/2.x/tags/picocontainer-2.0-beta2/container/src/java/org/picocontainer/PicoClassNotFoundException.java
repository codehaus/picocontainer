/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

public class PicoClassNotFoundException extends PicoException {

    public PicoClassNotFoundException(final String className, final ClassNotFoundException cnfe) {
        super("Class '" + className + "' not found", cnfe);  
    }
}
