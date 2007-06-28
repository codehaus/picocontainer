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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Visitor to verify {@link PicoContainer}instances. The visitor walks down the logical
 * container hierarchy.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.1
 */
public class VerifyingVisitor
        extends AbstractPicoVisitor {

    private final List nestedVerificationExceptions;
    private final Set verifiedComponentAdapters;
    private final PicoVisitor componentAdapterCollector;
    private PicoContainer currentPico;

    public VerifyingVisitor() {
        nestedVerificationExceptions = new ArrayList();
        verifiedComponentAdapters = new HashSet();
        componentAdapterCollector = new ComponentAdapterCollector();
    }

    /**
     * Traverse through all components of the {@link PicoContainer} hierarchy and verify the
     * components.
     * 
     * @throws PicoVerificationException if some components could not be verified.
     * @see org.picocontainer.PicoVisitor#traverse(java.lang.Object)
     */
    public Object traverse(Object node) throws PicoVerificationException {
        nestedVerificationExceptions.clear();
        verifiedComponentAdapters.clear();
        try {
            super.traverse(node);
            if (!nestedVerificationExceptions.isEmpty()) {
                throw new PicoVerificationException(new ArrayList(nestedVerificationExceptions));
            }
        } finally {
            nestedVerificationExceptions.clear();
            verifiedComponentAdapters.clear();
        }
        return Void.TYPE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.PicoVisitor#visitContainer(org.picocontainer.PicoContainer)
     */
    public void visitContainer(PicoContainer pico) {
        checkTraversal();
        currentPico = pico;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.PicoVisitor#visitComponentAdapter(org.picocontainer.ComponentAdapter)
     */
    public void visitComponentAdapter(ComponentAdapter componentAdapter) {
        checkTraversal();
        if (!verifiedComponentAdapters.contains(componentAdapter)) {
            try {
                componentAdapter.verify(currentPico);
            } catch (RuntimeException e) {
                nestedVerificationExceptions.add(e);
            }
            componentAdapter.accept(componentAdapterCollector);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.PicoVisitor#visitParameter(org.picocontainer.Parameter)
     */
    public void visitParameter(Parameter parameter) {
        checkTraversal();
    }

    private class ComponentAdapterCollector
            implements PicoVisitor {
        ///CLOVER:OFF
        public Object traverse(Object node) {
            return null;
        }

        public void visitContainer(PicoContainer pico) {
        }

        ///CLOVER:ON

        public void visitComponentAdapter(ComponentAdapter componentAdapter) {
            verifiedComponentAdapters.add(componentAdapter);
        }

        public void visitParameter(Parameter parameter) {
        }
    }
}
