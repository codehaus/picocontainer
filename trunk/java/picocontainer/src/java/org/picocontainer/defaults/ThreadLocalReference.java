/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.defaults;

/**
 * Implementation of an {@link ObjectReference}as {@link ThreadLocal}.
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalReference
        implements ObjectReference {

    private ThreadLocal instance = new ThreadLocal();

    /**
     * @see org.picocontainer.defaults.ObjectReference#get()
     */
    public Object get() {
        return instance.get();
    }

    /**
     * @see org.picocontainer.defaults.ObjectReference#set(java.lang.Object)
     */
    public void set(Object item) {
        instance.set(item);
    }

}