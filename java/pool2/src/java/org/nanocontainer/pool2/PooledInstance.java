/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                                        *
 *****************************************************************************/
package org.nanocontainer.pool2;

/**
 * An interface automatically implemented by the Proxy instances returned
 * from an auto-releasing pool.  It is not necessary to implement this interface
 * in a custom class. Cast the instance to this interface if you want to release
 * the instance explicitly.
 *
 * @author J&ouml;rg Schaible
 */
public interface PooledInstance {
    /**
     * Return the managed instance of this proxy directly to the pool.
     */
    void returnInstanceToPool();
}
