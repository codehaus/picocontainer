/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.nanocontainer.proxytoys;

import com.thoughtworks.proxy.factory.CglibProxyFactory;

import junit.framework.TestCase;

import org.nanocontainer.proxytoys.tck.CompatibleTouchable;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.lang.reflect.Proxy;


/**
 * @author J&ouml;rg Schaible
 */
public class AssimilatingComponentAdapterTest extends AbstractComponentAdapterTestCase {

    /**
     * Test if an instance can be assimilated.
     */
    public void testInstanceIsBorged() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        final ComponentAdapter componentAdapter = new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(
                CompatibleTouchable.class, CompatibleTouchable.class));
        mpc.registerComponent(new AssimilatingComponentAdapter(Touchable.class, componentAdapter));
        final CompatibleTouchable compatibleTouchable = (CompatibleTouchable) componentAdapter
                .getComponentInstance(mpc);
        final Touchable touchable = (Touchable) mpc.getComponentInstanceOfType(Touchable.class);
        assertFalse(compatibleTouchable.wasTouched());
        touchable.touch();
        assertTrue(compatibleTouchable.wasTouched());
        assertTrue(Proxy.isProxyClass(touchable.getClass()));
    }
    
    /**
     * Test if the component key is preserved if it is not a class type.
     */
    public void testComponentKeyIsPreserved() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        final ComponentAdapter componentAdapter = new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(
                "Touchy", CompatibleTouchable.class));
        mpc.registerComponent(new AssimilatingComponentAdapter(Touchable.class, componentAdapter));
        final CompatibleTouchable compatibleTouchable = (CompatibleTouchable) componentAdapter
                .getComponentInstance(mpc);
        final Touchable touchable = (Touchable) mpc.getComponentInstance("Touchy");
        assertFalse(compatibleTouchable.wasTouched());
        touchable.touch();
        assertTrue(compatibleTouchable.wasTouched());
        assertTrue(Proxy.isProxyClass(touchable.getClass()));
    }

    /**
     * Test if proxy generation is omitted, if types are compatible.
     */
    public void testAvoidUnnecessaryProxy() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponent(new AssimilatingComponentAdapter(TestCase.class, new InstanceComponentAdapter(TestCase.class, this)));
        final TestCase self = (TestCase) mpc.getComponentInstanceOfType(TestCase.class);
        assertFalse(Proxy.isProxyClass(self.getClass()));
        assertSame(this, self);
    }

    /**
     * Test if proxy generation is omitted, if types are compatible and that the component key is not changed.
     */
    public void testAvoidedProxyDoesNotChangeComponentKey() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponent(new AssimilatingComponentAdapter(TestCase.class, new InstanceComponentAdapter(getClass(), this)));
        final TestCase self = (TestCase) mpc.getComponentInstance(getClass());
        assertNotNull(self);
        assertSame(this, self);
    }
    
    /**
     * Test fail-fast for components without interface.
     */
    public void testComponentMustImplementInterface() {
        try {
            new AssimilatingComponentAdapter(SimpleTouchable.class, new InstanceComponentAdapter(TestCase.class, this));
            fail("PicoIntrospectionException expected");
        } catch (final PicoIntrospectionException e) {
            assertTrue(e.getMessage().endsWith(SimpleTouchable.class.getName()));
        }
    }

    // -------- TCK -----------

    protected Class getComponentAdapterType() {
        return AssimilatingComponentAdapter.class;
    }

    protected int getComponentAdapterNature() {
        return super.getComponentAdapterNature() & ~(RESOLVING | VERIFYING | INSTANTIATING);
    }

    private ComponentAdapter createComponentAdapterWithTouchable() {
        return new AssimilatingComponentAdapter(Touchable.class, new ConstructorInjectionComponentAdapter(
                CompatibleTouchable.class, CompatibleTouchable.class));
    }

    /**
     * {@inheritDoc}
     */
    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithTouchable();
    }

    /**
     * {@inheritDoc}
     */
    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithTouchable();
    }

    /**
     * {@inheritDoc}
     */
    protected ComponentAdapter prepDEF_visitable() {
        return createComponentAdapterWithTouchable();
    }

    /**
     * {@inheritDoc}
     */
    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return new AssimilatingComponentAdapter(Touchable.class, new InstanceComponentAdapter(
                CompatibleTouchable.class, new CompatibleTouchable()), new CglibProxyFactory());
    }

    /**
     * {@inheritDoc}
     */
    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithTouchable();
    }
}
