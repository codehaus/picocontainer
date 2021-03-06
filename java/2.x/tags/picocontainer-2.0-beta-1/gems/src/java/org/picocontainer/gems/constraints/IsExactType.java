/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.ComponentAdapter;

/**
 * Constraint that only accepts an adapter whose implementation is the same
 * class instance as the type represented by this object.
 *
 * @author Nick Sieger
 * @version 1.1
 */
public final class IsExactType extends AbstractConstraint {
    private final Class type;

    /**
     * Creates a new <code>IsExactType</code> instance.
     *
     * @param c the <code>Class</code> to match
     */
    public IsExactType(Class c) {
        this.type = c;
    }

    public boolean evaluate(ComponentAdapter adapter) {
        return type == adapter.getComponentImplementation();
    }

}
