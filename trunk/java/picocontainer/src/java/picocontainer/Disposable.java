/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

/**
 * Disposable is an entirely optional interface.  A Pico component
 * may choose to implement this or not or any other interface that
 * roughly forces Disposable behavior.
 *
 * Dispoable may be a useful concept for heavy (charcterize that
 * how you will) components and called at the end of their life.
 * The lifecycle design of Pico requires a LifecycleManager that
 * reconises the type of lifecycle design the component writer
 * is using.
 */
public interface Disposable  {

    /**
     * Dispose of a component
     *
     * @throws Exception if the component could not be disposed of.
     */
    void dispose() throws Exception;

}
