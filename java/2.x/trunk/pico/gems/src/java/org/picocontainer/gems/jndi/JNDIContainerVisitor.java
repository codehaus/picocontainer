/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.jndi;

import java.util.Set;

import javax.management.MBeanServer;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.visitors.TraversalCheckingVisitor;

/**
 * traverse pico container and expose components to JNDI on 
 * sight of JNDIExposed
 * @author k.pribluda
 */
public class JNDIContainerVisitor extends TraversalCheckingVisitor {

	private PicoContainer container;
	
	/**
	 * in case component adapter is JNDIExposed, poke it gently and
	 * it will create component and register it to JNDI if not already 
	 * done. 
	 */
	@Override
	public void visitComponentAdapter(ComponentAdapter componentAdapter)
	{
		super.visitComponentAdapter(componentAdapter);

		if(componentAdapter instanceof JNDIExposed) {
			componentAdapter.getComponentInstance(container);
		}
	}

	/**
     * Provides the PicoContainer, that can resolve the components to register as MBean.
     * @see org.picocontainer.PicoVisitor#visitContainer(org.picocontainer.PicoContainer)
     */
    public void visitContainer(final PicoContainer pico) {
        super.visitContainer(pico);
        container = pico;
    }

    /**
     * Entry point for the visitor traversal.
     * @return Returns a {@link Set} with all ObjectInstance instances retrieved from the {@link MBeanServer} for the
     *         registered MBeans.
     * @see org.picocontainer.visitors.AbstractPicoVisitor#traverse(java.lang.Object)
     */
    public Object traverse(final Object node) {
        super.traverse(node);
        container = null;
        return null;
    }

}
