/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.type1;

import org.picocontainer.PicoIntrospectionException;

/**
 * A subclass of {@link PicoIntrospectionException} that is the superclass of the errors thrown from
 * {@link org.nanocontainer.type1 this package}.
 * 
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class PicoType1ContractException extends PicoIntrospectionException {
    /**
     * {@inheritDoc}
     * 
     * @param e {@inheritDoc}
     */ 
    public PicoType1ContractException(final Throwable e) {
        super(e);
    }
    /**
     * {@inheritDoc}
     * 
     * @param s {@inheritDoc}
     */ 
    public PicoType1ContractException(final String s) {
        super(s);
    }
}
