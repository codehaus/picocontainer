/*****************************************************************************
 * Copyright (C) 2004 PicoContainer Organization. All rights reserved.       *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.PicoException;
import org.picocontainer.PicoVisitor;


/**
 * Exception for a PicoVisitor, that is dependent on a defined starting point of the traversal.
 * If the traversal is not initiated with a call of {@link PicoVisitor#traverse}
 * 
 * @author joehni
 * @since 1.1
 */
public class PicoVisitorTraversalException
        extends PicoException {

    /**
     * Construct the PicoVisitorTraversalException.
     * 
     * @param visitor The visitor casing the exception.
     */
    public PicoVisitorTraversalException(PicoVisitor visitor) {
        super("Traversal for PicoVisitor of type " + visitor.getClass().getName() + " must start with the visitor's traverse method");
    }
}
