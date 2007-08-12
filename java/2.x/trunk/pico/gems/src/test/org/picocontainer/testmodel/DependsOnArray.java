/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.testmodel;

/**
 * @author Nick Sieger
 */
public final class DependsOnArray {
    private final Object[] array;
    public DependsOnArray(Object[] a) {
        this.array = a;
    }

    public Object[] getDependencies() {
        return array;
    }
}
