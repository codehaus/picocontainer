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

import org.apache.avalon.framework.service.ServiceException;

/**
 * An {@link PicoAvalonContractException} that is thrown when there's a problem related to the
 * {@link org.apache.avalon.framework.service Avalon-Framework Service contracts}.
 * 
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class PicoServiceException extends PicoAvalonContractException {
    /**
     * {@inheritDoc}
     * 
     * @param e the exception that caused this one.
     */ 
    public PicoServiceException(final ServiceException e) {
        super(e);
    }
}
