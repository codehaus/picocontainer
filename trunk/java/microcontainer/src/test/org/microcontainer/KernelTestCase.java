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

/**
 * @author Paul Hammant
 * @version $Revision$
 */

public class KernelTestCase extends TestCase {

    public void testDeploymentOfMarFile() {
        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // assertNotNull(o);
        // Method m = o.getClass().getMethod("testMe", new Object[0]);
        // assertEqual("hello", m.invoke(o, new Object[0]);
    }

    public void testDeferredDeplymentOfMarFile() {
        // kernel.deferredDeploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // assertNull(o);
        // kernel.start("test/org.megacontainer.test.TestComp");
        // o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // assertNotNull(o);
        // Method m = o.getClass().getMethod("testMe", new Object[0]);
        // assertEqual("hello", m.invoke(o, new Object[0]);
    }

    public void testDeployedMarsComponentsAreInDiffClassloaderToKernel() {
        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // assertNotNull(o);
        // these should be two removed from each other.
        // assertEqual(kernel.getClass().getClassLoader(), o.getClass().getClassLoader().getParent().getParent());
    }

    public void testAPIisPromoted() {
        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.testapi.TestPromotable");
        // these should be one removed from each other.
        // assertEqual(kernel.getClass().getClassLoader(), o.getClass().getClassLoader().getParent());
        // Method m = o.getClass().getMethod("unHideImplClassLoader", new Object[0]);
        // ClassLoader implClassLoader = (ClassLoader) m.invoke(o, new Object[0]
        // these should be two removed from each other.
        // assertEqual(kernel.getClass().getClassLoader(), implClassLoader.getParent().getParent());

    }

    public void testTwoDeployedMarsComponentAPIsAreInDiffClassloader() {
        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // kernel.deploy(new File("test2.mar"));
        // Object o2 = kernel.getComponent("test2/org.megacontainer.test2.Test2Comp");
        // assertNotNull(o);
        // assertNotNull(o2);
        // assertNotSame(o.getClass().getClassLoader(), o2.getClass().getClassLoader());
    }

    public void testTwoDeployedMarsComponentImplementationsAreInDifferntClassloader() {
        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // Method m = o.getClass().getMethod("unHideImplClassLoader", new Object[0]);
        // kernel.deploy(new File("test2.mar"));
        // Object o2 = kernel.getComponent("test2/org.megacontainer.test2.Test2Comp");

        // unHideImplClassLoader allows us to cater for the fact that the default behavior of Mega
        // is to hide implementations. The method would amount to a logical security flaw if
        // implemented in a real component.

        // Method m = o2.getClass().getMethod("unHideImplClassLoader", new Object[0]);
        // ClassLoader impl1ClassLoader = (ClassLoader) m.invoke(o, new Object[0]
        // ClassLoader impl2ClassLoader = (ClassLoader) m2.invoke(o2, new Object[0]
        // assertNotSame(impl1ClassLoader, impl2ClassLoader);
    }

    public void testKernelAndKernelImplAreIndifferentClassLoaders() {
        // might be quite hard
    }

    public void testMarWithUnsupportedNanoContainerScriptCannotBeDeployed() {
        // kernel.deploy(new File("bogus.mar"));
        // .bogus language not supported
    }

    public void testMarMissingJavaCannotBeDeployed() {
         // kernel.deploy(new File("incomplete.mar"));
         // .bogus language not supported
    }

    public void testMarFileAppCanBeStopped() {
        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // Method m = o.getClass().getMethod("isRunning", new Object[0]);
        // assertFalse(m.invoke(o, new Object[0]);
        // kernel.stop("test");
        // assertTrue(m.invoke(o, new Object[0]);
        // of course, any references to any comp will be usable prior to GC, even if the container has stopped.
    }

    public void testKernelImplIsInvisibleFromMarsSandbox() {

        // might not work as IDE is going to fight to keep these at VM classpath level

        // kernel.deploy(new File("test.mar"));
        // Object o = kernel.getComponent("test/org.megacontainer.test.TestComp");
        // Method m = o.getClass().getMethod("testKernelImplIsInvisibleFromMarsSandbox", new Object[0]);
        // m.invoke(o, new Object[0]); // asserts using Junit that certain classes do not exist in classpath
    }

}
