/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Disposable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.adapters.InstanceComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.Map;

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

    public void testDefaultLifecycleStrategy() {
        LifecycleComponent component = new LifecycleComponent();
        InstanceComponentAdapter componentAdapter =
            new InstanceComponentAdapter(LifecycleComponent.class, component);
        PicoContainer pico = new DefaultPicoContainer();
        componentAdapter.start(pico);
        componentAdapter.stop(pico);
        componentAdapter.dispose(pico);
        assertEquals("start>stop>dispose>", component.buffer.toString());
        componentAdapter.start(component);
        componentAdapter.stop(component);
        componentAdapter.dispose(component);
        assertEquals("start>stop>dispose>start>stop>dispose>", component.buffer.toString());
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

    public void testCustomLifecycleCanBeInjected() {
        NullLifecycle component = new NullLifecycle();
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        InstanceComponentAdapter componentAdapter = new InstanceComponentAdapter(NullLifecycle.class, component, strategy);
        PicoContainer pico = new DefaultPicoContainer();
        componentAdapter.start(pico);
        componentAdapter.stop(pico);
        componentAdapter.dispose(pico);
        assertEquals("<start<stop<dispose", strategy.recording());
        componentAdapter.start(component);
        componentAdapter.stop(component);
        componentAdapter.dispose(component);
        assertEquals("<start<stop<dispose<start<stop<dispose", strategy.recording());
    }

    public void testComponentAdapterCanIgnoreLifecycle() {
        final Touchable touchable = new SimpleTouchable();
        InstanceComponentAdapter componentAdapter = new InstanceComponentAdapter(Touchable.class, touchable);
        PicoContainer pico = new DefaultPicoContainer();
        componentAdapter.start(pico);
        componentAdapter.stop(pico);
        componentAdapter.dispose(pico);
        componentAdapter.start(touchable);
        componentAdapter.stop(touchable);
        componentAdapter.dispose(touchable);
    }

    public void testGuardAgainstNullInstance() {
        try {
            new InstanceComponentAdapter(Map.class, null);
            fail("should have barfed");
        } catch (NullPointerException e) {
            assertEquals("componentInstance cannot be null", e.getMessage());
        }
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
