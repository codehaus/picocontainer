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
        return super.getComponentAdapterNature() & ~(RESOLVING | VERIFYING | INSTANTIATING);
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestVerifyWithoutDependencyWorks(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestVerifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return new InstanceComponentAdapter("foo", "bar");
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestVerifyDoesNotInstantiate(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestVerifyDoesNotInstantiate(
            MutablePicoContainer picoContainer) {
        return new InstanceComponentAdapter("Key", new Integer(4711));
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestVisitable()
     */
    protected ComponentAdapter prepareTestVisitable() {
        return new InstanceComponentAdapter("Key", new Integer(4711));
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareSerializable(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareSerializable(MutablePicoContainer picoContainer) {
        return new InstanceComponentAdapter("Key", new Integer(4711));
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestFailingVerificationWithUnsatisfiedDependency(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestFailingVerificationWithUnsatisfiedDependency(MutablePicoContainer picoContainer) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestComponentAdapterCreatesNewInstances(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestComponentAdapterCreatesNewInstances(
            MutablePicoContainer picoContainer) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestErrorIsRethrown(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestErrorIsRethrown(MutablePicoContainer picoContainer) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestRuntimeExceptionIsRethrown(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestRuntimeExceptionIsRethrown(MutablePicoContainer picoContainer) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestNormalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestNormalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(MutablePicoContainer picoContainer) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestFailingVerificationWithCyclicDependencyException(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestFailingVerificationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepareTestFailingInstantiationWithCyclicDependencyException(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepareTestFailingInstantiationWithCyclicDependencyException(MutablePicoContainer picoContainer) {
        return null;
    }
}
