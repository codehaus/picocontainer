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

/**
 * Aggregates multiple constraints together using boolean OR logic.
 * Constraints are short-circuited as in java.
 *
 * @author Nick Sieger
 * @version 1.1
 */
public final class Or extends AbstractConstraint {
    private final Constraint[] children;

    public Or(Constraint c1, Constraint c2) {
        children = new Constraint[2];
        children[0] = c1;
        children[1] = c2;
    }

    public Or(Constraint c1, Constraint c2, Constraint c3) {
        children = new Constraint[3];
        children[0] = c1;
        children[1] = c2;
        children[2] = c3;
    }

    public Or(Constraint[] cc) {
        children = new Constraint[cc.length];
        System.arraycopy(cc, 0, children, 0, cc.length);
    }

    public boolean evaluate(ComponentAdapter adapter) {
        for (Constraint aChildren : children) {
            if (aChildren.evaluate(adapter)) {
                return true;
            }
        }
        return false;
    }

    public void accept(PicoVisitor visitor) {
        super.accept(visitor);
        for (Constraint aChildren : children) {
            aChildren.accept(visitor);
        }
    }
}
