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
 * Constraint that accepts an adapter whose implementation is either the
 * same type or a subtype of the type(s) represented by this object.
 *
 * @author Nick Sieger
 */
@SuppressWarnings("serial")
public final class IsType extends AbstractConstraint {
	private final Class type;

    /**
     * Creates a new <code>IsType</code> instance.
     *
     * @param c the <code>Class</code> to match
     */
    public IsType(final Class c) {
        this.type = c;
    }

    @Override
	public boolean evaluate(final ComponentAdapter adapter) {
        return type.isAssignableFrom(adapter.getComponentImplementation());
    }

}
