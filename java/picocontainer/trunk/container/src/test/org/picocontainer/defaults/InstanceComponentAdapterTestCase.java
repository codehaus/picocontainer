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
import org.picocontainer.Disposable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
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

    public void testComponentAdapterHandlesLifecycleWithDefaultLifecycleStrategy() {
        LifecycleComponent component = new LifecycleComponent();
        InstanceComponentAdapter componentAdapter = new InstanceComponentAdapter(LifecycleComponent.class, component);
        PicoContainer pico = new DefaultPicoContainer();
        componentAdapter.start(pico);
        componentAdapter.stop(pico);
        componentAdapter.dispose(pico);
        assertEquals("start>stop>dispose>", component.buffer.toString());
        assertNotNull(componentAdapter.currentLifecycleStrategy());
        assertTrue(componentAdapter.currentLifecycleStrategy() instanceof DefaultLifecycleStrategy);
    }

    public void testComponentAdapterHandlesLifecycleWithCustomLifecycleStrategy() {
        LifecycleComponent component = new LifecycleComponent();
        CustomLifecycleStrategy customLifecycleStrategy = new CustomLifecycleStrategy();
        InstanceComponentAdapter componentAdapter = new InstanceComponentAdapter(LifecycleComponent.class, component, customLifecycleStrategy);
        PicoContainer pico = new DefaultPicoContainer();
        componentAdapter.start(pico);
        componentAdapter.stop(pico);
        componentAdapter.dispose(pico);
        assertEquals("<start<stop<dispose", customLifecycleStrategy.buffer.toString());
        assertNotNull(componentAdapter.currentLifecycleStrategy());
        assertTrue(componentAdapter.currentLifecycleStrategy() instanceof CustomLifecycleStrategy);
    }
    
    private static class LifecycleComponent implements Startable, Disposable {
        StringBuffer buffer = new StringBuffer();
        
        public void start() {
            buffer.append("start>");
        }

        public void stop() {
            buffer.append("stop>");
        }

        public void dispose() {
            buffer.append("dispose>");
        }       
    }
    
    private static class CustomLifecycleStrategy implements LifecycleStrategy {
        StringBuffer buffer = new StringBuffer();

        public void start(Object component) {
            buffer.append("<start");
        }

        public void stop(Object component) {
            buffer.append("<stop");
        }

        public void dispose(Object component) {
            buffer.append("<dispose");
        }    
    }

    public void testComponentAdapterIgnoresLifecycle() {
        final Touchable touchable = new SimpleTouchable();
        InstanceComponentAdapter componentAdapter = new InstanceComponentAdapter(Touchable.class, touchable);
        PicoContainer pico = new DefaultPicoContainer();
        componentAdapter.start(pico);
        componentAdapter.stop(pico);
        componentAdapter.dispose(pico);
        assertNotNull(componentAdapter.currentLifecycleStrategy());
        assertTrue(componentAdapter.currentLifecycleStrategy() instanceof DefaultLifecycleStrategy);
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
