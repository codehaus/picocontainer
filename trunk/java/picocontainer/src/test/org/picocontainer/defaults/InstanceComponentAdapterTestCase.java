/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * Test the InstanceComponentAdapter.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.1
 */
public class InstanceComponentAdapterTestCase
        extends AbstractComponentAdapterTestCase {

    public void testComponentAdapterReturnsSame() {
        final Touchable touchable = new SimpleTouchable();
        final ComponentAdapter componentAdapter = new InstanceComponentAdapter(Touchable.class, touchable);
        assertSame(touchable, componentAdapter.getComponentInstance(null));
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#getComponentAdapterType()
     */
    protected Class getComponentAdapterType() {
        return InstanceComponentAdapter.class;
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#getComponentAdapterNature()
     */
    protected int getComponentAdapterNature() {
        return super.getComponentAdapterNature() & ~(RESOLVING | VERIFYING | INSTANTIATING );
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepDEF_verifyWithoutDependencyWorks(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return new InstanceComponentAdapter("foo", "bar");
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepDEF_verifyDoesNotInstantiate(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(
            MutablePicoContainer picoContainer) {
        return new InstanceComponentAdapter("Key", new Integer(4711));
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepDEF_visitable()
     */
    protected ComponentAdapter prepDEF_visitable() {
        return new InstanceComponentAdapter("Key", new Integer(4711));
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepSER_isSerializable(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return new InstanceComponentAdapter("Key", new Integer(4711));
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepSER_isXStreamSerializable(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return new InstanceComponentAdapter("Key", new Integer(4711));
    }
}
