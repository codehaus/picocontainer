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
 * A utility class for data validation.
 * 
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
class Check {
    /**
     * Ensure that the provided argument is not null. If it is null, an exception will be thrown, for which the
     * argumentName will be used to provide further details about which argument was not allowed to be null. 
     * 
     * @param argumentName the name of the argument to be used in the exception detail message.
     * @param argument the actual argument.
     * @throws NullArgumentException if the provided argument is null.
     */ 
    static void argumentNotNull(final String argumentName, final Object argument) throws NullArgumentException {
        if (argument == null) {
            throw new NullArgumentException(argumentName + " may not be null!");
        }
    }
}
