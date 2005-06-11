/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.ioc.avalon;

/**
 * An {@link PicoAvalonContractException} that is thrown when there's a problem related to the
 * {@link org.apache.avalon.framework.configuration Avalon-Framework Configuration contracts}.
 * 
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class PicoConfigurationException extends PicoAvalonContractException {
    /**
     * {@inheritDoc}
     * 
     * @param e the exception that caused this one
     */ 
    public PicoConfigurationException(final Throwable e) {
        super(e);
    }
}
