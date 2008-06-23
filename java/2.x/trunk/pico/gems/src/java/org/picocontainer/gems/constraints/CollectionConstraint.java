/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVisitor;
import org.picocontainer.parameters.CollectionComponentParameter;

/**
 * Constraint that collects/aggregates dependencies to as many components
 * that satisfy the given constraint.
 *
 * @author Nick Sieger
 * @author J&ouml;rg Schaible
 */
public final class CollectionConstraint extends CollectionComponentParameter implements Constraint {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2054071316780187223L;
	protected final Constraint constraint;

    public CollectionConstraint(final Constraint constraint) {
        this(constraint, false);
    }

    public CollectionConstraint(final Constraint constraint, final boolean emptyCollection) {
        super(Object.class, emptyCollection);
        this.constraint = constraint;
    }

    @Override
	public boolean evaluate(final ComponentAdapter adapter) {
        return constraint.evaluate(adapter);
    }

    @Override
	public void accept(final PicoVisitor visitor) {
        super.accept(visitor);
        constraint.accept(visitor);
    }
}
