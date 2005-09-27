/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.microcontainer.impl;

import junit.framework.TestCase;
import org.microcontainer.Kernel;
import org.microcontainer.DeploymentException;
import org.microcontainer.McaDeployer;
import org.microcontainer.DeploymentScriptHandler;
import org.microcontainer.ClassLoaderFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.nanocontainer.testmodel.Wilma;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipException;

/**
 * @author Paul Hammant
 * @author Mike Ward
 * @version $Revision$
 */

public class DefaultKernelTestCase extends TestCase { // LSD: extends PicoTCKTestCase of some sort I'd hope

    private Kernel kernel;

    protected void setUp() throws Exception {
        super.setUp();
		kernel = TestFixture.createKernel();
    }

    public void testDeploymentOfMcaFileYieldsAccesibleComponent() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, DeploymentException {
        kernel.deploy(new File("test.mca"));
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
        assertNotNull(o);
        Method m = o.getClass().getMethod("testMe", new Class[0]);
        assertEquals("hello", m.invoke(o, new Object[0]));
    }

    public void testDeploymentOfMcaFileYieldsAccesibleContainer() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, DeploymentException {
        kernel.deploy(new File("test.mca"));
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
        assertNotNull(o);
        Method m = o.getClass().getMethod("testMe", new Class[0]);
        assertEquals("hello", m.invoke(o, new Object[0]));
    }

    public void testDeploymentOfMcaFileFromURL() throws Exception {
        //kernel.handleDeployForMcaFile(new URL("http://cvs.picocontainer.codehaus.org/java/microcontainer/src/remotecomp.mca"));
		File testMca = new File("test.mca");
		kernel.deploy(new URL("jar:file:" + testMca.getCanonicalPath() + "!/"));
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
        assertNotNull(o);
        Method m = o.getClass().getMethod("testMe", new Class[0]);
        assertEquals("hello", m.invoke(o, new Object[0]));
        Method isRunningMethod = o.getClass().getMethod("isRunning", new Class[0]);
        assertEquals(Boolean.TRUE, isRunningMethod.invoke(o, new Object[0]));
    }

    public void testDeferredDeploymentOfMcaFile() throws Exception {
        kernel.deferredDeploy(new File("test.mca"));
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
		Method isRunningMethod = o.getClass().getMethod("isRunning", new Class[0]);
		assertEquals(Boolean.FALSE, isRunningMethod.invoke(o, new Object[0]));
        kernel.start("test");
        o = kernel.getComponent("test/org.microcontainer.test.TestComp");
        assertEquals(Boolean.TRUE, isRunningMethod.invoke(o, new Object[0]));
        Method testMethod = o.getClass().getMethod("testMe", new Class[0]);
        assertEquals("hello", testMethod.invoke(o, new Object[0]));
    }

    public void testDeployedMcaComponentsAreInDifferentClassloaderToKernel() throws DeploymentException{
        kernel.deploy(new File("test.mca"));
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
        assertNotNull(o);
		Class interfaceClass = o.getClass().getInterfaces()[0];
        // the interface should be two removed from each other.
        assertEquals(kernel.getClass().getClassLoader(), interfaceClass.getClassLoader().getParent().getParent());

        // LSD: what kind of number is that, "two"?
        // You're testing that the kernel is two classloaders
        // above the component, which is not
        // testDeployedMcasComponentsAreInDiffClassloaderToKernel()
    }

    public void testAPIisPromotedToDifferentClassLoaderHierachy() throws Exception {
        kernel.deploy(new File("test.mca"));
        Object o = kernel.getComponent("test/org.microcontainer.testapi.TestPromotable");
		Class interfaceClass =  o.getClass().getInterfaces()[0];

		// the interface's class loader should be one removed from the kernel's class loader
        assertEquals(kernel.getClass().getClassLoader(), interfaceClass.getClassLoader().getParent());
        Method m = o.getClass().getMethod("unHideImplClassLoader", new Class[0]);
        ClassLoader implClassLoader = (ClassLoader) m.invoke(o, new Class[0]);
        // these should be four removed from each other (cause of DRCAClassLoader from nano).
        assertEquals(kernel.getClass().getClassLoader(), implClassLoader.getParent().getParent().getParent().getParent());
        // LSD: those numbers again...you want to expose the classloader architecture
        // to the client...that'll make it difficult to change...
    }

	// todo mward implement this
	public void fixthis_testDeployedComponentCanAccessPromotedAPIsFromDifferentComponent() throws Exception {
		kernel.deploy(new File("test.mca"));
		kernel.deploy(new File("test2.mca"));

		// A class in test2 should be dependent on an api promoted by another component
	}

    public void testTwoDeployedMcaComponentAPIsAreInDifferentClassloadersButSharePromotedClassLoader() throws Exception {
		URL url = new URL("jar:file:test.mca!/");
        kernel.deploy("test", url);
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
		kernel.deploy("test2", url);
        Object o2 = kernel.getComponent("test2/org.microcontainer.test.TestComp");
        assertNotNull(o);
        assertNotNull(o2);
        // LSD: this, I like...
        assertNotSame(o.getClass().getClassLoader(), o2.getClass().getClassLoader());
        assertNotSame(o.getClass(),o2.getClass());
        assertSame(o.getClass().getName(),o2.getClass().getName()); // this does not contradict the test above :-)
        System.out.println("--> 0 1" + this.getClass().getClassLoader());
        System.out.println("--> 0 2" + DefaultKernel.class.getClassLoader());
        System.out.println("--> 1 1 " + o.getClass().getClassLoader());
        System.out.println("--> 1 2 " + o.getClass().getClassLoader().getParent());
        System.out.println("--> 1 3 " + o.getClass().getClassLoader().getParent().getParent());
        System.out.println("--> 1 4 " + o.getClass().getClassLoader().getParent().getParent().getParent());
        System.out.println("--> 1 5 " + o.getClass().getClassLoader().getParent().getParent().getParent().getParent());
        System.out.println("--> 2 1 " + o2.getClass().getClassLoader());
        System.out.println("--> 2 2 " + o2.getClass().getClassLoader().getParent());
        System.out.println("--> 2 3 " + o2.getClass().getClassLoader().getParent().getParent());
        System.out.println("--> 2 4 " + o2.getClass().getClassLoader().getParent().getParent().getParent());
        System.out.println("--> 2 5 " + o2.getClass().getClassLoader().getParent().getParent().getParent().getParent());

        ClassLoader cl1 = o.getClass().getClassLoader();
        while (!(cl1 instanceof DelegatingClassLoader)) {
            cl1 = cl1.getParent();
        }

        ClassLoader cl2 = o2.getClass().getClassLoader();
        while (!(cl2 instanceof DelegatingClassLoader)) {
            cl2 = cl2.getParent();
        }
        
        assertSame(cl1, cl2);
    }

    public void testTwoDeployedMcaComponentImplementationsAreInDifferentClassloader() throws Exception {
        URL url = new URL("jar:file:test.mca!/");
        kernel.deploy("test", url);
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
        Method m = o.getClass().getMethod("unHideImplClassLoader", new Class[0]);
        kernel.deploy("test2", url);
        Object o2 = kernel.getComponent("test2/org.microcontainer.test.TestComp");

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

    public void testMcaWithUnsupportedNanoContainerScriptCannotBeDeployed() {
        try {
            kernel.deploy(new File("bogus.mca"));
            fail("should have barfed as bogus.mca is not there");
        } catch (DeploymentException e) {
            // expected
        }
    }

    public void testMcaMissingJavaCannotBeDeployed() throws DeploymentException{
		try {
			kernel.deploy(new File("incomplete.mca"));
			fail("The file incomplete.mca should NOT be valid, deployment exception should be thrown");
		} catch (DeploymentException e) {
        	assertTrue(e.getCause() instanceof ZipException);
		}

		assertFalse(new File("work/incomplete").exists()); // ensure directory was NOT created
    }

    public void testDeploymentOfMcaFileResultsInAProperExceptionOnMissingFile() throws DeploymentException {
		try {
			kernel.deploy(new File("missing.mca"));
			fail("DeploymentException should have been thrown");
		} catch (DeploymentException e) {
			assertTrue(e.getCause() instanceof ZipException);
		}
	}

    public void dont_testDeploymentOfMcaFileResultsInAProperExceptionOnBadURL() throws MalformedURLException, DeploymentException {
        // test passes when connected to the net.
		try {
			kernel.deploy(new URL("http://cvs.picocontainer.codehaus.org/java/microcontainer/src/remotecomp.mca.badurl"));
			fail("DeploymentException should have been thrown");
		} catch (DeploymentException e) {
			assertTrue(e.getCause() instanceof FileNotFoundException);
		}
	}

    public void testMcaWithGroovyScriptErrorResultsInException() {
        // gracefully handle misconfiguration...
    }

    public void testMcaFileAppCanBeStopped() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, DeploymentException {
        kernel.deploy(new File("test.mca"));
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
        Method m = o.getClass().getMethod("isRunning", new Class[0]);
        assertTrue(((Boolean) m.invoke(o, new Object[0])).booleanValue());
        kernel.stop("test");
        assertFalse(((Boolean) m.invoke(o, new Object[0])).booleanValue());
        // of course, any references to any comp will be usable prior to GC, even if the container has stopped.
    }

    public void testKernelImplIsInvisibleFromMcaSandbox() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, DeploymentException {

        // might not work as IDE is going to fight to keep these at VM classpath level

        kernel.deploy(new File("test.mca"));
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
        Method m = o.getClass().getMethod("testKernelImplIsInvisibleFromMcaSandbox", new Class[0]);
        m.invoke(o, new Object[0]); // asserts using Junit that certain classes do not exist in classpath
    }

    public void testExportedComponentsCanInteract()
    {
        // thomas' use case goes here
    }

    public void testHiddenStuffIsActuallyHidden()
    {
        // the comps are there, what's the test supposed to be?
        // An assert statement being static can be anywhere in a invocation stack.
        // there could be a deployed .mca file that could try to access the classes from an adjacent
        // application's hidden classloader.
    }

    public void testExportComponentsUsingNanoContainerRpc()
    {
        // what mechanism? Support one by default?
    }

    public void testBasicTreeNavigationUsingBasicXPath()
    {
        // not just opaque string handling pleez
        // is this redundant ?
    }

    public void testBasicTreeNavigationUsingComplexXPath()
    {
        // what about getting an array of components satisfying constraints?
        // XPath does that, no?
    }

    public void testMultipleKernelsPeacefullyCoexistInAnEmbeddedEnvironment() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, DeploymentException
    {
        // was an issue with phoenix at times...ie these guys don't claim server sockets...
        Kernel kernel2 = TestFixture.createKernel();

        testDeploymentOfMcaFileYieldsAccesibleComponent();

        kernel2.deploy(new File("test.mca"));
        Object o = kernel.getComponent("test/org.microcontainer.test.TestComp");
        assertNotNull(o);
        Method m = o.getClass().getMethod("testMe", new Class[0]);
        assertEquals("hello", m.invoke(o, new Object[0]));
    }

    public void testKernelCanRunInPicoContainer() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
		pico.registerComponentImplementation(Kernel.class, DefaultKernel.class);

		// registers Directories to names
		pico.registerComponentInstance(new DefaultConfiguration());

		// parameters for DefaultMcaDeployer
		pico.registerComponentImplementation(McaDeployer.class, DefaultMcaDeployer.class);

		// parameters for Groovy GroovyDeploymentScriptHandler
		pico.registerComponentImplementation(DeploymentScriptHandler.class, GroovyDeploymentScriptHandler.class);

		// register for DefaultClassLoader
		pico.registerComponentImplementation(ClassLoaderFactory.class, DefaultClassLoaderFactory.class);


		assertNotNull( pico.getComponentInstance(Kernel.class) );
        pico.start();
        pico.stop();
        pico.dispose();
    }

    public void testJMXPublication() throws Exception {
        kernel.deploy(new File("test.mca"));

		ObjectName objectName = new ObjectName("domain:wilma=default");
		PicoContainer root = kernel.getRootContainer("test");
		MBeanServer mBeanServer = (MBeanServer)root.getComponentInstance(MBeanServer.class);

		Wilma wilma = (Wilma)root.getComponentInstance("wilma");
		wilma.hello();

		MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);
		assertNotNull(mBeanInfo);

		Boolean called = (Boolean)mBeanServer.invoke(objectName, "helloCalled", null, null);
		assertTrue(called.booleanValue());
    }
}




