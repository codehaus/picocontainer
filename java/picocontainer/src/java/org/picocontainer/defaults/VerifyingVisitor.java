/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Visitor to verify {@link PicoContainer} instances. The visitor walks down the
 * logical container hierarchy.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.1
 */
public class VerifyingVisitor
        implements PicoVisitor {

    /**
     * {@inheritDoc}
     * @see org.picocontainer.PicoVisitor#visitContainer(org.picocontainer.PicoContainer)
     */
    public void visitContainer(PicoContainer pico) {
        final List nestedVerificationExceptions = new ArrayList();
        for (Iterator iterator = pico.getComponentAdapters().iterator(); iterator.hasNext();) {
            final ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            try {
                componentAdapter.verify(pico);
            } catch (PicoVerificationException e) {
                nestedVerificationExceptions.add(e.getNestedExceptions());
            }
        }

        if (!nestedVerificationExceptions.isEmpty()) {
            throw new PicoVerificationException(nestedVerificationExceptions);
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.PicoVisitor#visitComponentAdapter(org.picocontainer.ComponentAdapter)
     */
    public void visitComponentAdapter(ComponentAdapter componentAdapter) {
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.PicoVisitor#visitParameter(org.picocontainer.Parameter)
     */
    public void visitParameter(Parameter parameter) {
    }

    /**
     * @return <code>false</code>
     * @see org.picocontainer.PicoVisitor#isReverseTraversal()
     */
    public boolean isReverseTraversal() {
        return false;
    }

}
