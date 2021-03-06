/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.gems;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Unit test for ThreadLocalComponentAdapter.
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalComponentAdapterTest extends AbstractComponentAdapterTestCase {

    protected Class getComponentAdapterType() {
        return ThreadLocalComponentAdapter.class;
    }

    protected int getComponentAdapterNature() {
        return super.getComponentAdapterNature() & ~(RESOLVING | VERIFYING | INSTANTIATING);
    }

    private ComponentAdapter createComponentAdapterWithSimpleTouchable() {
        return new ThreadLocalComponentAdapter(new ConstructorInjectionComponentAdapter(
                Touchable.class, SimpleTouchable.class, null));
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithSimpleTouchable();
    }

    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithSimpleTouchable();
    }

    protected ComponentAdapter prepDEF_visitable() {
        return createComponentAdapterWithSimpleTouchable();
    }

    protected ComponentAdapter prepDEF_isAbleToTakeParameters(MutablePicoContainer picoContainer) {
        return new ThreadLocalComponentAdapter(new ConstructorInjectionComponentAdapter(
                List.class, ArrayList.class, new Parameter[]{new ConstantParameter(new Integer(10))}));
    }

    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithSimpleTouchable();
    }

    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithSimpleTouchable();
    }

    /**
     * Helper class testing ThreadLocal cache.
     */
    public static class Runner implements Runnable {

        private final Touchable m_touchable;
        private final List m_list;
        private final Set m_set;

        /**
         * Constructs a Runner.
         * @param touchable The instance
         * @param list The list to which all instances are added
         * @param set The set to which all instances are added
         */
        public Runner(final Touchable touchable, final List list, final Set set) {
            m_touchable = touchable;
            m_list = list;
            m_set = set;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            final Thread thread = Thread.currentThread();
            while (!Thread.interrupted()) {
                m_set.add(m_touchable);
                m_list.add(m_touchable);
                try {
                    synchronized (thread) {
                        thread.wait();
                    }
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            }
        }
    }

    /**
     * Test usage from multiple threads.
     * @throws InterruptedException
     */
    public final void testInstancesUsedFromMultipleThreads() throws InterruptedException {
        final Set set = Collections.synchronizedSet(new HashSet());
        final List list = Collections.synchronizedList(new ArrayList());
        final ComponentAdapter componentAdapter = new ThreadLocalComponentAdapter(new ConstructorInjectionComponentAdapter(
                Touchable.class, SimpleTouchable.class, null));
        final Touchable touchable = (Touchable)componentAdapter.getComponentInstance(null);

        final Thread[] threads = {
                new Thread(new Runner(touchable, list, set), "junit-1"), new Thread(new Runner(touchable, list, set), "junit-2"),
                new Thread(new Runner(touchable, list, set), "junit-3"),};
        for (int i = threads.length; i-- > 0;) {
            threads[i].start();
        }
        Thread.sleep(300);
        for (int i = threads.length; i-- > 0;) {
            synchronized (threads[i]) {
                threads[i].notify();
            }
        }
        Thread.sleep(300);
        for (int i = threads.length; i-- > 0;) {
            threads[i].interrupt();
        }
        Thread.sleep(300);
        assertEquals(6, list.size());
        assertEquals(3, set.size());
    }

    /**
     * Test instances from different containers.
     */
    public final void testInstancesAreNotSharedBetweenContainers() {
        final MutablePicoContainer picoA = new DefaultPicoContainer();
        final MutablePicoContainer picoB = new DefaultPicoContainer();
        picoA.registerComponent(new ThreadLocalComponentAdapter(new ConstructorInjectionComponentAdapter(
                List.class, ArrayList.class, null)));
        picoB.registerComponent(new ThreadLocalComponentAdapter(new ConstructorInjectionComponentAdapter(
                List.class, ArrayList.class, null)));
        final List hello1 = (List)picoA.getComponentInstance(List.class);
        final List hello2 = (List)picoA.getComponentInstance(List.class);
        hello1.add("foo");
        assertEquals(hello1, hello2);
        final List hello3 = (List)picoB.getComponentInstance(List.class);
        assertEquals(0, hello3.size());
    }

    /**
     * Test fail-fast for components without interface.
     */
    public void testComponentMustImplementInterface() {
        try {
            new ThreadLocalComponentAdapter(new ConstructorInjectionComponentAdapter(Object.class, Object.class, null));
            fail("PicoIntrospectionException expected");
        } catch (final PicoIntrospectionException e) {
            assertTrue(e.getMessage().endsWith("It does not implement any interfaces."));
        }
    }

    public static interface TargetInvocationExceptionTester {
        public void throwsCheckedException() throws ClassNotFoundException;

        public void throwsRuntimeException();

        public void throwsError();
    }

    public static class ThrowingComponent implements TargetInvocationExceptionTester {
        public void throwsCheckedException() throws ClassNotFoundException {
            throw new ClassNotFoundException("junit");
        }

        public void throwsRuntimeException() {
            throw new RuntimeException("junit");
        }

        public void throwsError() {
            throw new Error("junit");
        }
    }

    public void testExceptionHandling() {
        final ComponentAdapter componentAdapter = new ThreadLocalComponentAdapter(new ConstructorInjectionComponentAdapter(
                TargetInvocationExceptionTester.class, ThrowingComponent.class, null));
        final TargetInvocationExceptionTester tester = (TargetInvocationExceptionTester)componentAdapter.getComponentInstance(null);
        try {
            tester.throwsCheckedException();
            fail("ClassNotFoundException expected");
        } catch (final ClassNotFoundException e) {
            assertEquals("junit", e.getMessage());
        }
        try {
            tester.throwsRuntimeException();
            fail("RuntimeException expected");
        } catch (final RuntimeException e) {
            assertEquals("junit", e.getMessage());
        }
        try {
            tester.throwsError();
            fail("Error expected");
        } catch (final Error e) {
            assertEquals("junit", e.getMessage());
        }
    }

    /**
     * Test ComponentAdapter using simple keys.
     */
    public final void testSimpleKeys() {
        final ComponentAdapter componentAdapter = new ThreadLocalComponentAdapter(new ConstructorInjectionComponentAdapter(
                "List", ArrayList.class, null));
        final List hello = (List)componentAdapter.getComponentInstance(null);
        assertNotNull(hello);
    }
}
