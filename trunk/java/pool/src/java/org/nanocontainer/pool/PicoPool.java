/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 */

package org.picoextras.pool;

/**
 *  <p><code>PicoPool</code> is a simple interface for non-keyed pico
 * component pools
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $ Revision: 1.0 $
 */
public interface PicoPool
{
    /**
     * takes a component from the pool
     * @return the borrowed component
     */
    public abstract Object borrowComponent() throws PicoPoolException;
    /**
     * Makes a component in the pool avalible again once it has been borrowed
     * @param component the borrowed component
     */
    public abstract void returnComponent(Object component) throws PicoPoolException;
    /**
     *
     * @return the number of components in the pool
     */
    public abstract int getSize();

    /**
     *
     * @return the number of components in the pool
     */
    public abstract int getMaxSize();

    /**
     * @return the instance type of the pool
     */
    public abstract Class getImplementation();

    /**
     * Empties the pool
     */
    public abstract void clearPool();

}