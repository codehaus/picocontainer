/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.tck;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.CyclicDependencyException;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;
import org.picocontainer.defaults.SimpleReference;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Test suite for a ComponentAdapter implementation.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.1
 */
public abstract class AbstractComponentAdapterTestCase
        extends TestCase {

    public static int SERIALIZABLE = 1;
    public static int PARAMETRIZABLE = 2;
    public static int VERIFYING = 4;
    public static int INSTANTIATING = 8;
    public static int RESOLVING = 16;
    
    protected abstract Class getComponentAdapterType();
    
    protected int getComponentAdapterNature() {
        return SERIALIZABLE | PARAMETRIZABLE | VERIFYING | INSTANTIATING | RESOLVING;
    }
    
    protected ComponentAdapterFactory createDefaultComponentAdapterFactory() {
        return new DefaultComponentAdapterFactory();
    }
    
    // ============================================
    // Default
    // ============================================
    
    protected abstract ComponentAdapter prepareTestVerifyWithoutDependencyWorks(MutablePicoContainer picoContainer);
    
    final public void testVerifyWithoutDependencyWorks() {
        final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
        final ComponentAdapter componentAdapter = prepareTestVerifyWithoutDependencyWorks(picoContainer);
        componentAdapter.verify(picoContainer);
    }

    protected abstract ComponentAdapter prepareTestVerifyDoesNotInstantiate(MutablePicoContainer picoContainer);
    
    final public void testVerifyDoesNotInstantiate() {
        final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
        final ComponentAdapter componentAdapter = prepareTestVerifyDoesNotInstantiate(picoContainer);
        assertSame(getComponentAdapterType(), componentAdapter.getClass());
        final ComponentAdapter notInstantiatablecomponentAdapter = new NotInstantiatableComponentAdapter(componentAdapter);
        final PicoContainer wrappedPicoContainer = wrapComponentInstances(NotInstantiatableComponentAdapter.class, picoContainer, null);
        notInstantiatablecomponentAdapter.verify(wrappedPicoContainer);
    }

    static class RecordingVisitor implements PicoVisitor {
        final List visitedElements = new LinkedList();
        public void visitContainer(PicoContainer pico) {
            visitedElements.add(pico);
        }
        public void visitComponentAdapter(ComponentAdapter componentAdapter) {
            visitedElements.add(componentAdapter);
        }
        public void visitComponentInstance(Object o) {
            visitedElements.add(o);
        }
        List getVisitedElements() {
            return visitedElements;
        }
    }
    
    protected abstract ComponentAdapter prepareTestVisitable();
    
    final public void testVisitable() {
        final ComponentAdapter componentAdapter = prepareTestVisitable();
        assertSame(getComponentAdapterType(), componentAdapter.getClass());
        final RecordingVisitor visitor = new RecordingVisitor();
        componentAdapter.accept(visitor);
        final List visitedElements = visitor.getVisitedElements();
        assertTrue(visitedElements.contains(componentAdapter));
        customTestVisitable(visitedElements);
    }
    
    protected void customTestVisitable(List visitedElements) {
    }
    
    // ============================================
    // Serializable
    // ============================================
    
    protected abstract ComponentAdapter prepareTestSerializable(MutablePicoContainer picoContainer);
    
    final public void testSerializable() {
        if ((getComponentAdapterNature() & SERIALIZABLE) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestSerializable(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            final Object instance = componentAdapter.getComponentInstance(picoContainer);
            final XStream xstream = new XStream(new DomDriver());
            final String xml = xstream.toXML(componentAdapter);
            final ComponentAdapter serializedComponentAdapter = (ComponentAdapter)xstream.fromXML(xml);
            assertEquals(componentAdapter.getComponentKey(), serializedComponentAdapter.getComponentKey());
            assertSame(instance.getClass(), serializedComponentAdapter.getComponentInstance(picoContainer).getClass());
            customTestSerializable(serializedComponentAdapter);
        }
    }
    
    protected void customTestSerializable(ComponentAdapter serializedComponentAdapter) {
    }
    
    // ============================================
    // Parametrizable
    // ============================================
    
    protected abstract ComponentAdapter prepareTestShouldBeAbleToTakeParameters(MutablePicoContainer picoContainer);
    
    final public void testShouldBeAbleToTakeParameters() {
        if ((getComponentAdapterNature() & PARAMETRIZABLE) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestShouldBeAbleToTakeParameters(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            final Object instance = componentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instance);
        }
    }

    // ============================================
    // Verifying
    // ============================================
    
    protected abstract ComponentAdapter prepareTestFailingVerificationWithUnsatisfiedDependency(MutablePicoContainer picoContainer);
    
    final public void testFailingVerificationWithUnsatisfiedDependency() {
        if ((getComponentAdapterNature() & VERIFYING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestFailingVerificationWithUnsatisfiedDependency(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            try {
                componentAdapter.verify(picoContainer);
                fail("PicoVerificationException expected");
            } catch (PicoVerificationException e) {
            } catch (Exception e) {
                fail("PicoVerificationException expected");
            }
            try {
                componentAdapter.getComponentInstance(picoContainer);
                fail("PicoInitializationException or PicoIntrospectionException expected");
            } catch (PicoInitializationException e) {
            } catch (Exception e) {
                fail("PicoInitializationException or PicoIntrospectionException expected");
            }
        }
    }

    // ============================================
    // Instantiating
    // ============================================

    protected abstract ComponentAdapter prepareTestComponentAdapterCreatesNewInstances(MutablePicoContainer picoContainer);

    public void testComponentAdapterCreatesNewInstances() {
        if ((getComponentAdapterNature() & INSTANTIATING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestComponentAdapterCreatesNewInstances(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            final Object instance = componentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instance);
            assertNotSame(instance,componentAdapter.getComponentInstance(picoContainer));
            assertSame(instance.getClass(), componentAdapter.getComponentInstance(picoContainer).getClass());
        }
    }

    protected abstract ComponentAdapter prepareTestErrorIsRethrown(MutablePicoContainer picoContainer);

    public void testErrorIsRethrown() {
        if ((getComponentAdapterNature() & INSTANTIATING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestErrorIsRethrown(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            try {
                componentAdapter.getComponentInstance(picoContainer);
                fail("Thrown Error excpected");
            } catch (final Error e) {
                assertEquals("test", e.getMessage());
            }
        }
    }

    protected abstract ComponentAdapter prepareTestRuntimeExceptionIsRethrown(MutablePicoContainer picoContainer);

    public void testRuntimeExceptionIsRethrown() {
        if ((getComponentAdapterNature() & INSTANTIATING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestRuntimeExceptionIsRethrown(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            try {
                componentAdapter.getComponentInstance(picoContainer);
                fail("Thrown RuntimeException excpected");
            } catch (final RuntimeException e) {
                assertEquals("test", e.getMessage());
            }
        }
    }

    protected abstract ComponentAdapter prepareTestNormalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(MutablePicoContainer picoContainer);

    public void testNormalExceptionIsRethrownInsidePicoInvocationTargetInitializationException() {
        if ((getComponentAdapterNature() & INSTANTIATING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestNormalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            try {
                componentAdapter.getComponentInstance(picoContainer);
                fail("Thrown PicoInvocationTargetInitializationException excpected");
            } catch (final PicoInvocationTargetInitializationException e) {
                assertTrue(e.getMessage().endsWith("test"));
                assertTrue(e.getCause() instanceof Exception);
            }
        }
    }

    // ============================================
    // Resolving
    // ============================================
    
    protected abstract ComponentAdapter prepareTestDependenciesAreResolved(MutablePicoContainer picoContainer);

    public void testDependenciesAreResolved() {
        if ((getComponentAdapterNature() & RESOLVING) > 0) {
            final List dependencies = new LinkedList();
            final Object[] wrapperDependencies = new Object[] { dependencies };
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestDependenciesAreResolved(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            assertFalse(picoContainer.getComponentAdapters().contains(componentAdapter));
            final PicoContainer wrappedPicoContainer = wrapComponentInstances(CollectingComponentAdapter.class, picoContainer, wrapperDependencies);
            final Object instance = componentAdapter.getComponentInstance(wrappedPicoContainer);
            assertNotNull(instance);
            assertTrue(dependencies.size() > 0);
        }
    }
    
    protected abstract ComponentAdapter prepareTestFailingVerificationWithCyclicDependencyException(MutablePicoContainer picoContainer);

    public void testFailingVerificationWithCyclicDependencyException() {
        if ((getComponentAdapterNature() & RESOLVING) > 0) {
            final Set cycleInstances = new HashSet();
            final ObjectReference cycleCheck = new SimpleReference();
            final Object[] wrapperDependencies = new Object[] { cycleInstances, cycleCheck };
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestFailingVerificationWithCyclicDependencyException(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            assertTrue(picoContainer.getComponentAdapters().contains(componentAdapter));
            final PicoContainer wrappedPicoContainer = wrapComponentInstances(CycleDetectorComponentAdapter.class, picoContainer, wrapperDependencies);
            try {
                componentAdapter.verify(wrappedPicoContainer);
                fail("Thrown PicoVerificationException excpected");
            } catch (final PicoVerificationException e) {
                final CyclicDependencyException cycle = (CyclicDependencyException)e.getNestedExceptions().get(0);
                final Class[] dependencies = cycle.getDependencies();
                assertSame(dependencies[0], dependencies[dependencies.length-1]);
            }
        }
    }
    
    protected abstract ComponentAdapter prepareTestFailingInstantiationWithCyclicDependencyException(MutablePicoContainer picoContainer);

    public void testFailingInstantiationWithCyclicDependencyException() {
        if ((getComponentAdapterNature() & RESOLVING) > 0) {
            final Set cycleInstances = new HashSet();
            final ObjectReference cycleCheck = new SimpleReference();
            final Object[] wrapperDependencies = new Object[] { cycleInstances, cycleCheck };
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepareTestFailingInstantiationWithCyclicDependencyException(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            assertTrue(picoContainer.getComponentAdapters().contains(componentAdapter));
            final PicoContainer wrappedPicoContainer = wrapComponentInstances(CycleDetectorComponentAdapter.class, picoContainer, wrapperDependencies);
            try {
                componentAdapter.getComponentInstance(wrappedPicoContainer);
                fail("Thrown CyclicDependencyException excpected");
            } catch (final CyclicDependencyException e) {
                final Class[] dependencies = e.getDependencies();
                assertSame(dependencies[0], dependencies[dependencies.length-1]);
            }
        }
    }
    
    // ============================================
    // Model
    // ============================================
    
    static public class NotInstantiatableComponentAdapter extends DecoratingComponentAdapter {
        public NotInstantiatableComponentAdapter(final ComponentAdapter delegate) {
            super(delegate);
        }
        public Object getComponentInstance(final PicoContainer container) {
            Assert.fail("Not instantiatable");
            return null;
        }
    }
    
    static public class CollectingComponentAdapter extends DecoratingComponentAdapter {
        final List list;
        public CollectingComponentAdapter(final ComponentAdapter delegate, final List list) {
            super(delegate);
            this.list = list;
        }
        public Object getComponentInstance(final PicoContainer container) {
            final Object result = super.getComponentInstance(container);
            list.add(result);
            return result;
        }
    }
    
    static public class CycleDetectorComponentAdapter extends DecoratingComponentAdapter {
        private final Set set;
        private final ObjectReference reference;
        public CycleDetectorComponentAdapter(final ComponentAdapter delegate, final Set set, final ObjectReference reference) {
            super(delegate);
            this.set = set;
            this.reference = reference;
        }
        public Object getComponentInstance(final PicoContainer container) {
            if (set.contains(this)) {
                reference.set(this);
            } else {
                set.add(this);
            }
            return super.getComponentInstance(container);
        }
    }
    
    protected PicoContainer wrapComponentInstances(final Class decoratingComponentAdapterClass, final PicoContainer picoContainer, final Object[] wrapperDependencies) {
        assertTrue(DecoratingComponentAdapter.class.isAssignableFrom(decoratingComponentAdapterClass));
        final MutablePicoContainer mutablePicoContainer = new DefaultPicoContainer();
        final int size = (wrapperDependencies != null ? wrapperDependencies.length : 0) + 1;
        final Collection allComponentAdapters = picoContainer.getComponentAdapters();
        for (final Iterator iter = allComponentAdapters.iterator(); iter.hasNext();) {
            final Parameter[] parameters = new Parameter[size];
            parameters[0] = new ConstantParameter(iter.next());
            for (int i = 1; i < parameters.length; i++) {
                parameters[i] = new ConstantParameter(wrapperDependencies[i-1]);
            }
            final MutablePicoContainer instantiatingPicoContainer = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory());
            instantiatingPicoContainer.registerComponentImplementation("decorator", decoratingComponentAdapterClass, parameters);
            mutablePicoContainer.registerComponent((ComponentAdapter)instantiatingPicoContainer.getComponentInstance("decorator"));
        }
        return mutablePicoContainer;
    }
}
