/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.testmodel;

import java.util.List;

/**
 * @author Nick Sieger
 */
public final class DependsOnList {
    private final List list;

    public DependsOnList(List l) {
        this.list = l;
    }

    public List getDependencies() {
        return list;
    }
}
