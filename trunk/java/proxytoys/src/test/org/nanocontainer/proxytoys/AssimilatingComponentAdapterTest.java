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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;

import java.io.Serializable;
import java.lang.reflect.Proxy;


/**
 * @author J&ouml;rg Schaible
 */
public class AssimilatingComponentAdapterTest extends AbstractComponentAdapterTestCase {

    public static interface Foo {
        int size();
    }

    public static class Bar implements Serializable {
        public int size() {
            return 1;
        }
    }

    public void testInstanceIsBorged() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponent(new AssimilatingComponentAdapter(Foo.class, new Bar()));
        final Foo foo = (Foo) mpc.getComponentInstanceOfType(Foo.class);
        assertEquals(1, foo.size());
        assertTrue(Proxy.isProxyClass(foo.getClass()));
    }

    public void testAvoidUnnecessaryProxy() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.registerComponent(new AssimilatingComponentAdapter(TestCase.class, this));
        final TestCase self = (TestCase) mpc.getComponentInstanceOfType(TestCase.class);
        assertFalse(Proxy.isProxyClass(self.getClass()));
        assertSame(this, self);
    }

    // -------- TCK -----------
    
    protected Class getComponentAdapterType() {
        return AssimilatingComponentAdapter.class;
    }

    protected int getComponentAdapterNature() {
        return super.getComponentAdapterNature() & ~(RESOLVING | VERIFYING | INSTANTIATING);
    }

    private ComponentAdapter createComponentAdapterFooBar() {
        return new AssimilatingComponentAdapter(Foo.class, new Bar());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepDEF_verifyWithoutDependencyWorks(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return createComponentAdapterFooBar();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepDEF_verifyDoesNotInstantiate(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        return createComponentAdapterFooBar();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepDEF_visitable()
     */
    protected ComponentAdapter prepDEF_visitable() {
        return createComponentAdapterFooBar();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepSER_isSerializable(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return new AssimilatingComponentAdapter(Foo.class, new Bar(), new CglibProxyFactory());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.tck.AbstractComponentAdapterTestCase#prepSER_isXStreamSerializable(org.picocontainer.MutablePicoContainer)
     */
    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return createComponentAdapterFooBar();
    }
}
