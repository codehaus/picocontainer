/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * @author Mauro Talevi
 * @version $Revision: $
 */
public class CachingComponentAdapterTestCase extends MockObjectTestCase {

    public void testComponentIsNotStartedWhenCachedAndCanBeStarted() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterSupportingLifecycleStrategy(true, false, false, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.getComponentInstance(pico);
        adapter.start(pico);
    }

    public void testComponentCanBeStartedAgainAfterBeingStopped() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, false, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        Object instanceAfterFirstStart = adapter.getComponentInstance(pico);
        adapter.stop(pico);
        adapter.start(pico);
        Object instanceAfterSecondStart = adapter.getComponentInstance(pico);
        assertSame(instanceAfterFirstStart, instanceAfterSecondStart);
    }
    
    public void testComponentCannotBeStartedIfDisposed() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterSupportingLifecycleStrategy(false, false, true, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.dispose(pico);
        try {
            adapter.start(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Already disposed", e.getMessage());
        }
    }

    public void testComponentCannotBeStartedIfAlreadyStarted() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterSupportingLifecycleStrategy(true, false, false, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        try {
            adapter.start(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Already started", e.getMessage());
        }
    }
    
    public void testComponentCannotBeStoppeddIfDisposed() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterSupportingLifecycleStrategy(false, false, true, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.dispose(pico);
        try {
            adapter.stop(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Already disposed", e.getMessage());
        }
    }

    public void testComponentCannotBeStoppedIfNotStarted() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, false, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        try {
            adapter.stop(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Not started", e.getMessage());
        }
    }

    public void testComponentCannotBeDisposedIfAlreadyDisposed() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, true, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
        try {
            adapter.dispose(pico);
            fail("IllegalStateException expected");
        } catch (Exception e) {
            assertEquals("Already disposed", e.getMessage());
        }
    }

    public void testComponentIsStoppedAndDisposedIfStartedWhenFlushed() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterSupportingLifecycleStrategy(true, true, true, true));
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.flush();
    }

    public void testComponentIsNotStoppedAndDisposedWhenFlushedIfNotStarted() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterSupportingLifecycleStrategy(false, false, false, false));
        adapter.flush();
    }

    public void testComponentIsNotStoppedAndDisposedWhenFlushedIfDelegateDoesNotSupportLifecycle() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterNotSupportingLifecycleStrategy());
        adapter.flush();
    }
    
    public void testLifecycleIsIgnoredIfDelegateDoesNotSupportIt() {
        CachingComponentAdapter adapter = new CachingComponentAdapter(
                mockComponentAdapterNotSupportingLifecycleStrategy());
        PicoContainer pico = new DefaultPicoContainer();
        adapter.start(pico);
        adapter.stop(pico);
        adapter.dispose(pico);
    }
    
    private ComponentAdapter mockComponentAdapterNotSupportingLifecycleStrategy() {
        Mock mock = mock(ComponentAdapter.class);
        return (ComponentAdapter)mock.proxy();
    }
    
    private ComponentAdapter mockComponentAdapterSupportingLifecycleStrategy(
            boolean start, boolean stop, boolean dispose, boolean getInstance) {
        Mock mock = mock(ComponentAdapterSupportingLifecycleStrategy.class);
        if ( start ){
            mock.expects(atLeastOnce()).method("start").with(isA(Touchable.class));
        }
        if ( stop ) {
            mock.expects(once()).method("stop").with(isA(Touchable.class));
        }
        if ( dispose ) {
            mock.expects(once()).method("dispose").with(isA(Touchable.class));
        }
        if ( getInstance ) {
            mock.expects(once()).method("getComponentInstance").with(
                    isA(PicoContainer.class)).will(
                    returnValue(new SimpleTouchable()));
        }
        return (ComponentAdapter) mock.proxy();
    }
    
    static interface ComponentAdapterSupportingLifecycleStrategy extends
            ComponentAdapter, LifecycleStrategy {
    }
}