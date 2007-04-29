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
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.XppDriver;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.componentadapters.CachingAndConstructorComponentAdapterFactory;
import org.picocontainer.defaults.AbstractPicoVisitor;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.componentadapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.CyclicDependencyException;
import org.picocontainer.componentadapters.DecoratingComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;
import org.picocontainer.defaults.SimpleReference;

import org.jmock.MockObjectTestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
public abstract class AbstractComponentAdapterTestCase extends MockObjectTestCase {

    public static int SERIALIZABLE = 1;
    public static int VERIFYING = 2;
    public static int INSTANTIATING = 4;
    public static int RESOLVING = 8;

    protected abstract Class getComponentAdapterType();

    protected int getComponentAdapterNature() {
        return SERIALIZABLE | VERIFYING | INSTANTIATING | RESOLVING;
    }

    protected ComponentAdapterFactory createDefaultComponentAdapterFactory() {
        return new CachingAndConstructorComponentAdapterFactory();
    }

    // ============================================
    // Default
    // ============================================

    /**
     * Prepare the test <em>verifyWithoutDependencyWorks</em>.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test for a component without dependencies. Registration in the pico is
     *         not necessary.
     */
    protected abstract ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer);

    final public void testDEF_verifyWithoutDependencyWorks() {
        final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
        final ComponentAdapter componentAdapter = prepDEF_verifyWithoutDependencyWorks(picoContainer);
        assertSame(getComponentAdapterType(), componentAdapter.getClass());
        componentAdapter.verify(picoContainer);
    }

    /**
     * Prepare the test <em>verifyDoesNotInstantiate</em>.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test for a component that may throw on instantiation. Registration in
     *         the pico is not necessary.
     */
    protected abstract ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer);

    final public void testDEF_verifyDoesNotInstantiate() {
        final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
        final ComponentAdapter componentAdapter = prepDEF_verifyDoesNotInstantiate(picoContainer);
        assertSame(getComponentAdapterType(), componentAdapter.getClass());
        final ComponentAdapter notInstantiatablecomponentAdapter = new NotInstantiatableComponentAdapter(
                componentAdapter);
        final PicoContainer wrappedPicoContainer = wrapComponentInstances(
                NotInstantiatableComponentAdapter.class, picoContainer, null);
        notInstantiatablecomponentAdapter.verify(wrappedPicoContainer);
    }

    /**
     * Prepare the test <em>visitable</em>.
     * 
     * @return a ComponentAdapter of the type to test. If the ComponentAdapter supports {@link Parameter}, you have to
     *         select a component, that have some.
     */
    protected abstract ComponentAdapter prepDEF_visitable();

    final public void testDEF_visitable() {
        final ComponentAdapter componentAdapter = prepDEF_visitable();
        final Class type = getComponentAdapterType();
        assertSame(type, componentAdapter.getClass());
        boolean hasParameters = supportsParameters(type);
        final RecordingVisitor visitor = new RecordingVisitor();
        visitor.traverse(componentAdapter);
        final List visitedElements = new ArrayList(visitor.getVisitedElements());
        assertSame(componentAdapter, visitedElements.get(0));
        if (hasParameters) {
            hasParameters = false;
            for (final Iterator iter = visitedElements.iterator(); iter.hasNext() && !hasParameters;) {
                hasParameters = Parameter.class.isAssignableFrom(iter.next().getClass());
            }
            assertTrue("ComponentAdapter " + type + " supports parameters, provide some", hasParameters);
        }
    }

    /**
     * Prepare the test <em>isAbleToTakeParameters</em>. Overload this function, if the ComponentAdapter to test
     * supports {@link Parameter}.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test. Select a component, that has some parameters. Registration in the
     *         pico is not necessary.
     */
    protected ComponentAdapter prepDEF_isAbleToTakeParameters(MutablePicoContainer picoContainer) {
        final Class type = getComponentAdapterType();
        boolean hasParameters = supportsParameters(type);
        if (hasParameters) {
            fail("You have to overwrite this method for a useful test");
        }
        return null;
    }

    final public void testDEF_isAbleToTakeParameters() {
        final Class type = getComponentAdapterType();
        boolean hasParameters = supportsParameters(type);
        if (hasParameters) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepDEF_isAbleToTakeParameters(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            final RecordingVisitor visitor = new RecordingVisitor();
            visitor.traverse(componentAdapter);
            final List visitedElements = visitor.getVisitedElements();
            if (hasParameters) {
                hasParameters = false;
                for (final Iterator iter = visitedElements.iterator(); iter.hasNext() && !hasParameters;) {
                    hasParameters = Parameter.class.isAssignableFrom(iter.next().getClass());
                }
                assertTrue("ComponentAdapter " + type + " supports parameters, provide some", hasParameters);
            }
            final Object instance = componentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instance);
        }
    }

    // ============================================
    // Serializable
    // ============================================

    /**
     * Prepare the test <em>isSerializable</em>. Overload this function, if the ComponentAdapter supports
     * serialization.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test. Registration in the pico is not necessary.
     */
    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testSER_isSerializable() throws IOException, ClassNotFoundException {
        if ((getComponentAdapterNature() & SERIALIZABLE) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepSER_isSerializable(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            final Object instance = componentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instance);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(componentAdapter);
            outputStream.close();
            final ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream
                    .toByteArray()));
            final ComponentAdapter serializedComponentAdapter = (ComponentAdapter)inputStream.readObject();
            inputStream.close();
            assertEquals(componentAdapter.getComponentKey(), serializedComponentAdapter.getComponentKey());
            final Object instanceAfterSerialization = serializedComponentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instanceAfterSerialization);
            assertSame(instance.getClass(), instanceAfterSerialization.getClass());
        }
    }

    /**
     * Prepare the test <em>isXStreamSerializable</em>. Overload this function, if the ComponentAdapter supports
     * serialization.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test. Registration in the pico is not necessary.
     */
    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testSER_isXStreamSerializableWithPureReflection() {
        if ((getComponentAdapterNature() & SERIALIZABLE) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepSER_isXStreamSerializable(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            final Object instance = componentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instance);
            final XStream xstream = new XStream(new PureJavaReflectionProvider(), new XppDriver());
            final String xml = xstream.toXML(componentAdapter);
            final ComponentAdapter serializedComponentAdapter = (ComponentAdapter)xstream.fromXML(xml);
            assertEquals(componentAdapter.getComponentKey(), serializedComponentAdapter.getComponentKey());
            final Object instanceAfterSerialization = serializedComponentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instanceAfterSerialization);
            assertSame(instance.getClass(), instanceAfterSerialization.getClass());
        }
    }

    final public void testSER_isXStreamSerializable() {
        if ((getComponentAdapterNature() & SERIALIZABLE) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepSER_isXStreamSerializable(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            final Object instance = componentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instance);
            final XStream xstream = new XStream(new XppDriver());
            final String xml = xstream.toXML(componentAdapter);
            final ComponentAdapter serializedComponentAdapter = (ComponentAdapter)xstream.fromXML(xml);
            assertEquals(componentAdapter.getComponentKey(), serializedComponentAdapter.getComponentKey());
            final Object instanceAfterSerialization = serializedComponentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instanceAfterSerialization);
            assertSame(instance.getClass(), instanceAfterSerialization.getClass());
        }
    }

    // ============================================
    // Verifying
    // ============================================

    /**
     * Prepare the test <em>verificationFailsWithUnsatisfiedDependency</em>. Overload this function, if the
     * ComponentAdapter's verification can fail e.g. due to an unresolved dependency.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test, that fails for the verification, e.g. because of a compoennt with
     *         missing dependencies. Registration in the pico is not necessary.
     */
    protected ComponentAdapter prepVER_verificationFails(MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testVER_verificationFails() {
        if ((getComponentAdapterNature() & VERIFYING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepVER_verificationFails(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            try {
                componentAdapter.verify(picoContainer);
                fail("PicoIntrospectionException expected");
            } catch (PicoIntrospectionException e) {
            } catch (Exception e) {
                fail("PicoIntrospectionException expected, but got " + e.getClass().getName());
            }
            try {
                componentAdapter.getComponentInstance(picoContainer);
                fail("PicoInitializationException or PicoIntrospectionException expected");
            } catch (PicoInitializationException e) {
            } catch (PicoIntrospectionException e) {
            } catch (Exception e) {
                fail("PicoInitializationException or PicoIntrospectionException expected, but got "
                        + e.getClass().getName());
            }
        }
    }

    // ============================================
    // Instantiating
    // ============================================

    /**
     * Prepare the test <em>createsNewInstances</em>. Overload this function, if the ComponentAdapter is
     * instantiating. It should create a new instance with every call.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test. Registration in the pico is not necessary.
     */
    protected ComponentAdapter prepINS_createsNewInstances(MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testINS_createsNewInstances() {
        if ((getComponentAdapterNature() & INSTANTIATING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepINS_createsNewInstances(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            final Object instance = componentAdapter.getComponentInstance(picoContainer);
            assertNotNull(instance);
            assertNotSame(instance, componentAdapter.getComponentInstance(picoContainer));
            assertSame(instance.getClass(), componentAdapter.getComponentInstance(picoContainer).getClass());
        }
    }

    /**
     * Prepare the test <em>errorIsRethrown</em>. Overload this function, if the ComponentAdapter is instantiating.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test with a component that fails with an {@link Error} at
     *         instantiation. Registration in the pico is not necessary.
     */
    protected ComponentAdapter prepINS_errorIsRethrown(MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testINS_errorIsRethrown() {
        if ((getComponentAdapterNature() & INSTANTIATING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepINS_errorIsRethrown(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            try {
                componentAdapter.getComponentInstance(picoContainer);
                fail("Thrown Error excpected");
            } catch (final Error e) {
                assertEquals("test", e.getMessage());
            }
        }
    }

    /**
     * Prepare the test <em>runtimeExceptionIsRethrown</em>. Overload this function, if the ComponentAdapter is
     * instantiating.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test with a component that fails with a {@link RuntimeException} at
     *         instantiation. Registration in the pico is not necessary.
     */
    protected ComponentAdapter prepINS_runtimeExceptionIsRethrown(MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testINS_runtimeExceptionIsRethrown() {
        if ((getComponentAdapterNature() & INSTANTIATING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepINS_runtimeExceptionIsRethrown(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            try {
                componentAdapter.getComponentInstance(picoContainer);
                fail("Thrown RuntimeException excpected");
            } catch (final RuntimeException e) {
                assertEquals("test", e.getMessage());
            }
        }
    }

    /**
     * Prepare the test <em>normalExceptionIsRethrownInsidePicoInvocationTargetInitializationException</em>. Overload
     * this function, if the ComponentAdapter is instantiating.
     * 
     * @param picoContainer container, may probably not be used.
     * @return a ComponentAdapter of the type to test with a component that fails with a
     *         {@link PicoInvocationTargetInitializationException} at instantiation. Registration in the pico is not
     *         necessary.
     */
    protected ComponentAdapter prepINS_normalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(
            MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testINS_normalExceptionIsRethrownInsidePicoInvocationTargetInitializationException() {
        if ((getComponentAdapterNature() & INSTANTIATING) > 0) {
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepINS_normalExceptionIsRethrownInsidePicoInvocationTargetInitializationException(picoContainer);
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

    /**
     * Prepare the test <em>dependenciesAreResolved</em>. Overload this function, if the ComponentAdapter is resolves
     * dependencies.
     * 
     * @param picoContainer container, used to register dependencies.
     * @return a ComponentAdapter of the type to test with a component that has dependencies. Registration in the pico
     *         is not necessary.
     */
    protected ComponentAdapter prepRES_dependenciesAreResolved(MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testRES_dependenciesAreResolved() {
        if ((getComponentAdapterNature() & RESOLVING) > 0) {
            final List dependencies = new LinkedList();
            final Object[] wrapperDependencies = new Object[]{dependencies};
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepRES_dependenciesAreResolved(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            assertFalse(picoContainer.getComponentAdapters().contains(componentAdapter));
            final PicoContainer wrappedPicoContainer = wrapComponentInstances(
                    CollectingComponentAdapter.class, picoContainer, wrapperDependencies);
            final Object instance = componentAdapter.getComponentInstance(wrappedPicoContainer);
            assertNotNull(instance);
            assertTrue(dependencies.size() > 0);
        }
    }

    /**
     * Prepare the test <em>failingVerificationWithCyclicDependencyException</em>. Overload this function, if the
     * ComponentAdapter is resolves dependencies.
     * 
     * @param picoContainer container, used to register dependencies.
     * @return a ComponentAdapter of the type to test with a component that has cyclic dependencies. You have to
     *         register the component itself in the pico.
     */
    protected ComponentAdapter prepRES_failingVerificationWithCyclicDependencyException(
            MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testRES_failingVerificationWithCyclicDependencyException() {
        if ((getComponentAdapterNature() & RESOLVING) > 0) {
            final Set cycleInstances = new HashSet();
            final ObjectReference cycleCheck = new SimpleReference();
            final Object[] wrapperDependencies = new Object[]{cycleInstances, cycleCheck};
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepRES_failingVerificationWithCyclicDependencyException(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            assertTrue(picoContainer.getComponentAdapters().contains(componentAdapter));
            final PicoContainer wrappedPicoContainer = wrapComponentInstances(
                    CycleDetectorComponentAdapter.class, picoContainer, wrapperDependencies);
            try {
                componentAdapter.verify(wrappedPicoContainer);
                fail("Thrown PicoVerificationException excpected");
            } catch (final CyclicDependencyException cycle) {
                final Class[] dependencies = cycle.getDependencies();
                assertSame(dependencies[0], dependencies[dependencies.length - 1]);
            }
        }
    }

    /**
     * Prepare the test <em>failingInstantiationWithCyclicDependencyException</em>. Overload this function, if the
     * ComponentAdapter is resolves dependencies.
     * 
     * @param picoContainer container, used to register dependencies.
     * @return a ComponentAdapter of the type to test with a component that has cyclic dependencies. You have to
     *         register the component itself in the pico.
     */
    protected ComponentAdapter prepRES_failingInstantiationWithCyclicDependencyException(
            MutablePicoContainer picoContainer) {
        throw new AssertionFailedError("You have to overwrite this method for a useful test");
    }

    final public void testRES_failingInstantiationWithCyclicDependencyException() {
        if ((getComponentAdapterNature() & RESOLVING) > 0) {
            final Set cycleInstances = new HashSet();
            final ObjectReference cycleCheck = new SimpleReference();
            final Object[] wrapperDependencies = new Object[]{cycleInstances, cycleCheck};
            final MutablePicoContainer picoContainer = new DefaultPicoContainer(createDefaultComponentAdapterFactory());
            final ComponentAdapter componentAdapter = prepRES_failingInstantiationWithCyclicDependencyException(picoContainer);
            assertSame(getComponentAdapterType(), componentAdapter.getClass());
            assertTrue(picoContainer.getComponentAdapters().contains(componentAdapter));
            final PicoContainer wrappedPicoContainer = wrapComponentInstances(
                    CycleDetectorComponentAdapter.class, picoContainer, wrapperDependencies);
            try {
                componentAdapter.getComponentInstance(wrappedPicoContainer);
                fail("Thrown CyclicDependencyException excpected");
            } catch (final CyclicDependencyException e) {
                final Class[] dependencies = e.getDependencies();
                assertSame(dependencies[0], dependencies[dependencies.length - 1]);
            }
        }
    }

    // ============================================
    // Model & Helpers
    // ============================================

    static class RecordingVisitor extends AbstractPicoVisitor {
        private final List visitedElements = new LinkedList();

        public void visitContainer(PicoContainer pico) {
            visitedElements.add(pico);
        }

        public void visitComponentAdapter(ComponentAdapter componentAdapter) {
            visitedElements.add(componentAdapter);
        }

        public void visitParameter(Parameter parameter) {
            visitedElements.add(parameter);
        }

        List getVisitedElements() {
            return visitedElements;
        }
    }

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

        public CycleDetectorComponentAdapter(
                final ComponentAdapter delegate, final Set set, final ObjectReference reference) {
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

    public static class RecordingLifecycleStrategy implements LifecycleStrategy {
        private StringBuffer recorder;
        
        public RecordingLifecycleStrategy(StringBuffer recorder) {
            this.recorder = recorder;
        }
    
        public void start(Object component) {
            recorder.append("<start");
        }
    
        public void stop(Object component) {
            recorder.append("<stop");
        }
    
        public void dispose(Object component) {
            recorder.append("<dispose");
        }
        
        public boolean hasLifecycle(Class type) {
            return true;
        }
        
        public String recording() {
            return recorder.toString();
        }
    }

    final protected PicoContainer wrapComponentInstances(
            final Class decoratingComponentAdapterClass, final PicoContainer picoContainer,
            final Object[] wrapperDependencies) {
        assertTrue(DecoratingComponentAdapter.class.isAssignableFrom(decoratingComponentAdapterClass));
        final MutablePicoContainer mutablePicoContainer = new DefaultPicoContainer();
        final int size = (wrapperDependencies != null ? wrapperDependencies.length : 0) + 1;
        final Collection allComponentAdapters = picoContainer.getComponentAdapters();
        for (final Iterator iter = allComponentAdapters.iterator(); iter.hasNext();) {
            final Parameter[] parameters = new Parameter[size];
            parameters[0] = new ConstantParameter(iter.next());
            for (int i = 1; i < parameters.length; i++) {
                parameters[i] = new ConstantParameter(wrapperDependencies[i - 1]);
            }
            final MutablePicoContainer instantiatingPicoContainer = new DefaultPicoContainer(
                    new ConstructorInjectionComponentAdapterFactory());
            instantiatingPicoContainer.registerComponent(
                    "decorator", decoratingComponentAdapterClass, parameters);
            mutablePicoContainer.registerComponent((ComponentAdapter)instantiatingPicoContainer
                    .getComponent("decorator"));
        }
        return mutablePicoContainer;
    }

    private boolean supportsParameters(final Class type) {
        boolean hasParameters = false;
        final Constructor[] constructors = type.getConstructors();
        for (int i = 0; i < constructors.length && !hasParameters; i++) {
            final Constructor constructor = constructors[i];
            final Class[] parameterTypes = constructor.getParameterTypes();
            for (int j = 0; j < parameterTypes.length; j++) {
                final Class parameterType = parameterTypes[j];
                if (Parameter.class.isAssignableFrom(parameterType)
                        || (parameterType.isArray() && Parameter.class.isAssignableFrom(parameterType
                                .getComponentType()))) {
                    hasParameters = true;
                    break;
                }
            }
        }
        return hasParameters;
    }
}