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

public class KernelTestCase extends TestCase { // LSD: extends PicoTCKTestCase of some sort I'd hope
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

    public void testDeployedMarsComponentsAreInDifferentClassloaderToKernel() {
        kernel.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        assertNotNull(o);
        // these should be two removed from each other.
        assertEquals(kernel.getClass().getClassLoader(), o.getClass().getClassLoader().getParent().getParent());

        // LSD: what kind of number is that, "two"?
        // You're testing that the kernel is two classloaders
        // above the component, which is not
        // testDeployedMarsComponentsAreInDiffClassloaderToKernel()
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
        // LSD: those numbers again...you want to expose the classloader architecture
        // to the client...that'll make it difficult to change...
    }

    public void testTwoDeployedMarsComponentAPIsAreInDifferentClassloader() {
        kernel.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        kernel.deploy(new File("test2.mar"));
        Object o2 = kernel.getComponent("test2/org.megacontainer.test2.Test2Comp");
        assertNotNull(o);
        assertNotNull(o2);
        // LSD: this, I like...
        assertNotSame(o.getClass().getClassLoader(), o2.getClass().getClassLoader());
    }

    public void testTwoDeployedMarsComponentImplementationsAreInDifferentClassloader() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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

    public void testMarWithGroovyScriptErrorResultsInException() {
        // gracefully handle misconfiguration...
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

    public void testExportedComponentsCanInteract()
    {
        // thomas' use case goes here
    }

    public void testHiddenStuffIsActuallyHidden()
    {
        // the comps are there, what's the test supposed to be?
    }

    public void testExportComponentsUsingAltRMI()
    {
        // what mechanism? Support one by default?
    }

    public void testBasicTreeNavigationUsingBasicXPath()
    {
        // not just opaque string handling pleez
    }

    public void testBasicTreeNavigationUsingComplexXPath()
    {
        // what about getting an array of components satisfying constraints?
        // XPath does that, no?
    }

    public void testMultipleKernelsPeaceFullyCoexistInAnEmbeddedEnvironment() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // was an issue with phoenix at times...ie these guys don't claim server sockets...
        Kernel kernel2 = new DefaultKernel();

        testDeploymentOfMarFile();

        kernel2.deploy(new File("test.mar"));
        Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        assertNotNull(o);
        Method m = o.getClass().getMethod("testMe", new Class[0]);
        assertEquals("hello", m.invoke(o, new Object[0]));
    }

}
