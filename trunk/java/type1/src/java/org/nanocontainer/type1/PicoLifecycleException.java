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

/**
 * An {@link PicoType1ContractException} that is thrown when there's a problem related to the
 * {@link org.apache.avalon.framework.activity Avalon-Framework activity contracts}.
 * 
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class PicoLifecycleException extends PicoType1ContractException {
    /**
     * {@inheritDoc}
     * 
     * @param s {@inheritDoc} 
     */ 
    public PicoLifecycleException(final String s) {
        super(s);
    }
}
