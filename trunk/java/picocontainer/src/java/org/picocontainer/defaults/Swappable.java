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
 * Interface implemented by all proxy instances created by
 * {@link ImplementationHidingComponentAdapter).
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface Swappable {
    /**
     * Swaps the subject behind the proxy with a new instance.
     * (The underscores in the method name are to reduce the risk of method name clashes
     * with other interfaces).
     * @param newSubject the new subject the proxy will delegate to.
     * @return the old subject
     */
    Object hotswap(Object newSubject);
}
