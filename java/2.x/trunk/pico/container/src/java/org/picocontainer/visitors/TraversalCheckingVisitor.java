/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.visitors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.visitors.AbstractPicoVisitor;


/**
 * Concrete implementation of Visitor which simply checks traversals.
 * This can be a useful class for other Visitor implementations to extend, 
 * as it provides a default implementation in case you one is only interested
 * in one PicoVisitor type.  Example:
 *
 *<pre>
 * PicoContainer container = new DefaultPicoContainer();
 * PicoContainer child = container.makeChildContainer();
 *
 * final List allContainers = new ArrayList();
 *
 * PicoVisitor visitor = new TraversalCheckingVisitor() {
 *     public boolean visitContainer(PicoContainer pico) {
 *         super.visitContainer(pico);  //Calls checkTraversal for us.
 *         allContainers.add(pico);
 *         return true;
 *     }
 * }
 * </pre>
 *
 * @author Michael Rimov
 */
public class TraversalCheckingVisitor
        extends AbstractPicoVisitor {

	/** {@inheritDoc} **/
    public boolean visitContainer(PicoContainer pico) {
        checkTraversal();
        return CONTINUE_TRAVERSAL;
    }

	/** {@inheritDoc} **/
    public void visitComponentAdapter(ComponentAdapter<?> componentAdapter) {
        checkTraversal();
    }

	/** {@inheritDoc} **/
    public void visitComponentFactory(ComponentFactory componentFactory) {
        checkTraversal();
    }

	/** {@inheritDoc} **/
    public void visitParameter(Parameter parameter) {
        checkTraversal();
    }

}
