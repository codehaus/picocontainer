/*****************************************************************************
 * Copyright (C) MegaContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.megacontainer;

import junit.framework.TestCase;
import org.megacontainer.impl.DefaultKernel;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Paul Hammant
 * @version $Revision$
 */

public class KernelTestCase extends TestCase {
    private Kernel kernel;

    protected void setUp() throws Exception {
        super.setUp();

        kernel = new DefaultKernel();
    }

    public void testDeploymentOfMarFile() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        kernel.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        assertNotNull(o);
        Method m = o.getClass().getMethod("testMe", new Class[0]);
        assertEquals("hello", m.invoke(o, new Object[0]));
    }

    public void testDeferredDeplymentOfMarFile() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        kernel.deferredDeploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        assertNull(o);
        kernel.start("test/org.megacontainer.test.TestComp");
        o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        assertNotNull(o);
        Method m = o.getClass().getMethod("testMe", new Class[0]);
        assertEquals("hello", m.invoke(o, new Object[0]));
    }

    public void testDeployedMarsComponentsAreInDiffClassloaderToKernel() {
        kernel.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        assertNotNull(o);
        // these should be two removed from each other.
        assertEquals(kernel.getClass().getClassLoader(), o.getClass().getClassLoader().getParent().getParent());
    }

    public void testAPIisPromoted() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        kernel.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.testapi.TestPromotable");
        // these should be one removed from each other.
        assertEquals(kernel.getClass().getClassLoader(), o.getClass().getClassLoader().getParent());
        Method m = o.getClass().getMethod("unHideImplClassLoader", new Class[0]);
        ClassLoader implClassLoader = (ClassLoader) m.invoke(o, new Class[0]);
        // these should be two removed from each other.
        assertEquals(kernel.getClass().getClassLoader(), implClassLoader.getParent().getParent());

    }

    public void testTwoDeployedMarsComponentAPIsAreInDiffClassloader() {
        kernel.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        kernel.deploy(new File("test2.mar"));
        Object o2 = kernel.getComponent("test2/org.megacontainer.test2.Test2Comp");
        assertNotNull(o);
        assertNotNull(o2);
        assertNotSame(o.getClass().getClassLoader(), o2.getClass().getClassLoader());
    }

    public void testTwoDeployedMarsComponentImplementationsAreInDifferntClassloader() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        kernel.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        Method m = o.getClass().getMethod("unHideImplClassLoader", new Class[0]);
        kernel.deploy(new File("test2.mar"));
        Object o2 = kernel.getComponent("test2/org.megacontainer.test2.Test2Comp");

        // unHideImplClassLoader allows us to cater for the fact that the default behavior of Mega
        // is to hide implementations. The method would amount to a logical security flaw if
        // implemented in a real component.

        Method m2 = o2.getClass().getMethod("unHideImplClassLoader", new Class[0]);
        ClassLoader impl1ClassLoader = (ClassLoader) m.invoke(o, new Object[0]);
        ClassLoader impl2ClassLoader = (ClassLoader) m2.invoke(o2, new Object[0]);
        assertNotSame(impl1ClassLoader, impl2ClassLoader);
    }

    public void testKernelAndKernelImplAreIndifferentClassLoaders() {
        // might be quite hard
    }

    public void testMarWithUnsupportedNanoContainerScriptCannotBeDeployed() {
        kernel.deploy(new File("bogus.mar"));
        // .bogus language not supported
    }

    public void testMarMissingJavaCannotBeDeployed() {
        kernel.deploy(new File("incomplete.mar"));
        // .bogus language not supported
    }

    public void testMarFileAppCanBeStopped() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        kernel.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        Method m = o.getClass().getMethod("isRunning", new Class[0]);
        assertFalse(((Boolean) m.invoke(o, new Object[0])).booleanValue());
        kernel.stop("test");
        assertTrue(((Boolean) m.invoke(o, new Object[0])).booleanValue());
        // of course, any references to any comp will be usable prior to GC, even if the container has stopped.
    }

    public void testKernelImplIsInvisibleFromMarsSandbox() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        // might not work as IDE is going to fight to keep these at VM classpath level

        kernel.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        Method m = o.getClass().getMethod("testKernelImplIsInvisibleFromMarsSandbox", new Class[0]);
        m.invoke(o, new Object[0]); // asserts using Junit that certain classes do not exist in classpath
    }

}
