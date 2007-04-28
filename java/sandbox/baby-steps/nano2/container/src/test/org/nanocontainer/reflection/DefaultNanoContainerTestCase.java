/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.reflection;

import junit.framework.TestCase;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.ClassName;
import org.picocontainer.PicoClassNotFoundException;
import org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor;
import org.nanocontainer.testmodel.WebServerImpl;
import org.picocontainer.*;
import org.picocontainer.alternatives.AbstractDelegatingMutablePicoContainer;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;

public class DefaultNanoContainerTestCase extends TestCase {

    public void testBasic() throws PicoRegistrationException, PicoInitializationException {
        NanoContainer nanoContainer = new DefaultNanoContainer();
        nanoContainer.registerComponent(new ClassName("org.nanocontainer.testmodel.DefaultWebServerConfig"));
        nanoContainer.registerComponent("org.nanocontainer.testmodel.WebServer", new ClassName("org.nanocontainer.testmodel.WebServerImpl"));
    }

    public void testProvision() throws PicoException, PicoInitializationException, ClassNotFoundException {
        NanoContainer nanoContainer = new DefaultNanoContainer();
        nanoContainer.registerComponent(new ClassName("org.nanocontainer.testmodel.DefaultWebServerConfig"));
        nanoContainer.registerComponent(new ClassName("org.nanocontainer.testmodel.WebServerImpl"));

        assertNotNull("WebServerImpl should exist", nanoContainer.getPico().getComponent(WebServerImpl.class));
        assertTrue("WebServerImpl should exist", nanoContainer.getPico().getComponent(WebServerImpl.class) instanceof WebServerImpl);
    }

    public void testNoGenerationRegistration() throws PicoRegistrationException, PicoIntrospectionException {
        NanoContainer nanoContainer = new DefaultNanoContainer();
        try {
            nanoContainer.registerComponent(new ClassName("Ping"));
            fail("should have failed");
        } catch (PicoClassNotFoundException e) {
            // expected
        }
    }

    public void testParametersCanBePassedInStringForm() throws ClassNotFoundException, PicoException, PicoInitializationException {
        NanoContainer nanoContainer = new DefaultNanoContainer();
        String className = ThingThatTakesParamsInConstructor.class.getName();

        nanoContainer.registerComponent("thing",
                className,
                new String[]{
                    "java.lang.String",
                    "java.lang.Integer"
                },
                new String[]{
                    "hello",
                    "22"
                });

        ThingThatTakesParamsInConstructor thing =
                (ThingThatTakesParamsInConstructor) nanoContainer.getPico().getComponent("thing");
        assertNotNull("component not present", thing);
        assertEquals("hello22", thing.getValue());
    }

    public void testThatTestCompIsNotNaturallyInTheClassPathForTesting() {

        // the following tests try to load the jar containing TestComp - it
        // won't do to have the class already available in the classpath

        DefaultNanoContainer dfca = new DefaultNanoContainer();
        try {
            dfca.registerComponent("foo", new ClassName("TestComp"));
            Object o = dfca.getPico().getComponent("foo");
            System.out.println("");
            fail("Should have failed. Class was loaded from " + o.getClass().getProtectionDomain().getCodeSource().getLocation());
        } catch (PicoClassNotFoundException expected) {
        }

    }

    public void testChildContainerAdapterCanRelyOnParentContainerAdapter() throws MalformedURLException, ClassNotFoundException {

        String testcompJarFileName = System.getProperty("testcomp.jar", "src/test-comp/TestComp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/OSS/PN/java/nanocontainer/src/test-comp/TestComp.jar";
        File testCompJar = new File(testcompJarFileName);
        assertTrue("The testcomp.jar system property should point to java/nanocontainer/src/test-comp/TestComp.jar", testCompJar.isFile());

        // Set up parent
        NanoContainer parentContainer = new DefaultNanoContainer();
        parentContainer.addClassLoaderURL(testCompJar.toURL());
        parentContainer.registerComponent("parentTestComp", new ClassName("TestComp"));
        parentContainer.registerComponent(new ClassName("java.lang.StringBuffer"));

        PicoContainer parentContainerAdapterPico = parentContainer.getPico();
        Object parentTestComp = parentContainerAdapterPico.getComponent("parentTestComp");
        assertEquals("TestComp", parentTestComp.getClass().getName());

        // Set up child
        NanoContainer childContainer = new DefaultNanoContainer(parentContainer);
        File testCompJar2 = new File(testCompJar.getParentFile(), "TestComp2.jar");
        childContainer.addClassLoaderURL(testCompJar2.toURL());
        childContainer.registerComponent("childTestComp", new ClassName("TestComp2"));

        PicoContainer childContainerAdapterPico = childContainer.getPico();
        Object childTestComp = childContainerAdapterPico.getComponent("childTestComp");

        assertEquals("TestComp2", childTestComp.getClass().getName());

        assertNotSame(parentTestComp, childTestComp);

        final ClassLoader parentCompClassLoader = parentTestComp.getClass().getClassLoader();
        final ClassLoader childCompClassLoader = childTestComp.getClass().getClassLoader();
        if(parentCompClassLoader != childCompClassLoader.getParent()) {
            printClassLoader(parentCompClassLoader);
            printClassLoader(childCompClassLoader);
            fail("parentTestComp classloader should be parent of childTestComp classloader");
        }
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parentContainerAdapterPico, childContainerAdapterPico.getParent());
    }

    private void printClassLoader(ClassLoader classLoader) {
        while(classLoader != null) {
            System.out.println(classLoader);
            classLoader = classLoader.getParent();
        }
        System.out.println("--");
    }

    public static class AnotherFooComp {

    }

    public void testClassLoaderJugglingIsPossible() throws MalformedURLException, ClassNotFoundException {
        NanoContainer parentContainer = new DefaultNanoContainer();

        String testcompJarFileName = System.getProperty("testcomp.jar", "src/test-comp/TestComp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/OSS/PN/java/nanocontainer/src/test-comp/TestComp.jar";
        File testCompJar = new File(testcompJarFileName);
        assertTrue("The testcomp.jar system property should point to java/nanocontainer/src/test-comp/TestComp.jar", testCompJar.isFile());

        parentContainer.registerComponent("foo", new ClassName("org.nanocontainer.testmodel.DefaultWebServerConfig"));

        Object fooWebServerConfig = parentContainer.getPico().getComponent("foo");
        assertEquals("org.nanocontainer.testmodel.DefaultWebServerConfig", fooWebServerConfig.getClass().getName());

        NanoContainer childContainer = new DefaultNanoContainer(parentContainer);
        childContainer.addClassLoaderURL(testCompJar.toURL());
        childContainer.registerComponent("bar", new ClassName("TestComp"));

        Object barTestComp = childContainer.getPico().getComponent("bar");
        assertEquals("TestComp", barTestComp.getClass().getName());

        assertNotSame(fooWebServerConfig.getClass().getClassLoader(), barTestComp.getClass().getClassLoader());

        // This kludge is needed because IDEA, Eclipse and Maven have different numbers of
        // classloaders in their hierachies for junit invocation.
        ClassLoader fooCL = fooWebServerConfig.getClass().getClassLoader();
        ClassLoader barCL1 = barTestComp.getClass().getClassLoader().getParent();
        ClassLoader barCL2, barCL3;
        if (barCL1 != null && barCL1 != fooCL) {
            barCL2 = barCL1.getParent();
            if (barCL2 != null && barCL2 != fooCL) {
                barCL3 = barCL2.getParent();
                if (barCL3 != null && barCL3 != fooCL) {
                    fail("One of the parent classloaders of TestComp, should be that of DefaultWebServerConfig");
                }
            }
        }
    }

    public void TODO_testSecurityManagerCanPreventOperations() throws MalformedURLException, ClassNotFoundException {
        NanoContainer parentContainer = new DefaultNanoContainer();

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/OSS/PN/java/nanocontainer/src/test-comp/TestComp.jar";
        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        parentContainer.registerComponent("foo", new ClassName("org.nanocontainer.testmodel.DefaultWebServerConfig"));

        Object fooWebServerConfig = parentContainer.getPico().getComponent("foo");
        assertEquals("org.nanocontainer.testmodel.DefaultWebServerConfig", fooWebServerConfig.getClass().getName());

        NanoContainer childContainer = new DefaultNanoContainer(parentContainer);
        childContainer.addClassLoaderURL(testCompJar.toURL());
        //TODO childContainer.setPermission(some permission list, that includes the preventing of general file access);
        // Or shoud this be done in the ctor for DRCA ?
        // or should it a parameter in the addClassLoaderURL(..) method
        childContainer.registerComponent("bar", new ClassName("org.nanocontainer.testmodel.FileSystemUsing"));

        try {
            parentContainer.getPico().getComponent("bar");
            fail("Should have barfed");
        } catch (java.security.AccessControlException e) {
            // expected
        }
    }


    public void testChainOfDecoratingPicoContainersCanDoInterceptionOfMutablePicoContainerMethods() throws ClassNotFoundException {
        NanoContainer nanoContainer = new DefaultNanoContainer();
        MutablePicoContainer decorating = nanoContainer.addDecoratingPicoContainer(FooDecoratingPicoContainer.class);
        assertTrue(decorating instanceof FooDecoratingPicoContainer);
        MutablePicoContainer decorating2 = nanoContainer.addDecoratingPicoContainer(BarDecoratingPicoContainer.class);
        assertTrue(decorating2 instanceof BarDecoratingPicoContainer);
        nanoContainer.registerComponent(new ClassName("java.util.Vector"));
        // decorators are fairly dirty - they replace a very select implementation in this TestCase.
        assertNotNull(nanoContainer.getComponent("java.util.ArrayList"));
        assertNull(nanoContainer.getComponent("java.util.Vector"));
        assertNotNull(nanoContainer.getPico().getComponent(ArrayList.class));
        assertNull(nanoContainer.getPico().getComponent(Vector.class));
    }

    public static class FooDecoratingPicoContainer extends AbstractDelegatingMutablePicoContainer {
        public FooDecoratingPicoContainer(MutablePicoContainer delegate) {
            super(delegate);
        }
        public MutablePicoContainer makeChildContainer() {
            return null;
        }

        public ComponentAdapter registerComponent(Class compImpl) throws PicoRegistrationException {
            assertEquals(HashMap.class, compImpl);
            return super.registerComponent(ArrayList.class);
        }
    }

    public static class BarDecoratingPicoContainer extends AbstractDelegatingMutablePicoContainer {
        public BarDecoratingPicoContainer(MutablePicoContainer delegate) {
            super(delegate);
        }
        public MutablePicoContainer makeChildContainer() {
            return null;
        }
        public ComponentAdapter registerComponent(Class compImpl) throws PicoRegistrationException {
            assertEquals(Vector.class, compImpl);
            return super.registerComponent(HashMap.class);
        }
    }


}
