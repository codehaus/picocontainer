/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

/**
 * This interface is implemented by delegate proxies which support replacing the delegate object
 * behind that proxy with a different instance. It is implemented by all proxy instances created by
 * {@link ImplementationHidingComponentAdapter}. Please note that implementations of this method
 * are not guaranteed to be safe for concurrent use.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface Swappable {
    /**
     * Swaps the delegate behind the proxy with a new instance.
     * 
     * @param newDelegate the new delegate to which the proxy will defer calls.
     * @return the old delegate which has been replaced.
     */
    Object hotswap(Object newDelegate);
}
