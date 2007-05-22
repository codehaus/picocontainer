/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.picocontainer.gems.adapters;

import java.io.Serializable;

import org.picocontainer.defaults.ObjectReference;


/**
 * An {@link org.picocontainer.defaults.ObjectReference} based on a {@link ThreadLocal}.
 * 
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalReference extends ThreadLocal implements ObjectReference, Serializable {

    private static final long serialVersionUID = 1L;

    private static class Marker implements Serializable {
        private static final long serialVersionUID = 1L;
        private static final Marker INSTANCE = new Marker();

        private Object readResolve() {
            return new ThreadLocalReference();
        }
    }

    private Object writeReplace() {
        return Marker.INSTANCE;
    }
}
