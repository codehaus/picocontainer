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
import org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor;
import org.nanocontainer.testmodel.WebServerImpl;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;

import java.io.File;
import java.net.MalformedURLException;

public class DefaultReflectionContainerAdapterTestCase extends TestCase {

    public void testBasic() throws PicoRegistrationException, PicoInitializationException, ClassNotFoundException {
        ReflectionContainerAdapter reflectionContainerAdapter = new DefaultReflectionContainerAdapter();
        reflectionContainerAdapter.registerComponentImplementation("org.nanocontainer.testmodel.DefaultWebServerConfig");
        reflectionContainerAdapter.registerComponentImplementation("org.nanocontainer.testmodel.WebServer", "org.nanocontainer.testmodel.WebServerImpl");
    }

    public void testProvision() throws PicoException, PicoInitializationException, ClassNotFoundException {
        ReflectionContainerAdapter reflectionContainerAdapter = new DefaultReflectionContainerAdapter();
        reflectionContainerAdapter.registerComponentImplementation("org.nanocontainer.testmodel.DefaultWebServerConfig");
        reflectionContainerAdapter.registerComponentImplementation("org.nanocontainer.testmodel.WebServerImpl");

        assertNotNull("WebServerImpl should exist", reflectionContainerAdapter.getPicoContainer().getComponentInstance(WebServerImpl.class));
        assertTrue("WebServerImpl should exist", reflectionContainerAdapter.getPicoContainer().getComponentInstance(WebServerImpl.class) instanceof WebServerImpl);
    }

    public void testNoGenerationRegistration() throws PicoRegistrationException, PicoIntrospectionException {
        ReflectionContainerAdapter reflectionContainerAdapter = new DefaultReflectionContainerAdapter();
        try {
            reflectionContainerAdapter.registerComponentImplementation("Ping");
            fail("should have failed");
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

    public void testParametersCanBePassedInStringForm() throws ClassNotFoundException, PicoException, PicoInitializationException {
        ReflectionContainerAdapter reflectionContainerAdapter = new DefaultReflectionContainerAdapter();
        String className = ThingThatTakesParamsInConstructor.class.getName();

        reflectionContainerAdapter.registerComponentImplementation("thing",
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
                (ThingThatTakesParamsInConstructor) reflectionContainerAdapter.getPicoContainer().getComponentInstance("thing");
        assertNotNull("component not present", thing);
        assertEquals("hello22", thing.getValue());
    }

    public void testThatTestCompIsNotNaturallyInTheClassPathForTesting() {

        try {
            DefaultReflectionContainerAdapter dfca = new DefaultReflectionContainerAdapter();
            dfca.registerComponentImplementation("foo", "TestComp");
            Object o = dfca.getPicoContainer().getComponentInstance("foo");
            fail("Not expected");
        } catch (ClassNotFoundException expected) {
        } catch (Exception e) {
            fail("Not expected");
        }

    }

    public void testChildContainerAdapterCanRelyOnParentContainerAdapter() throws MalformedURLException, ClassNotFoundException {

        String testcompJarFileName = System.getProperty("testcomp.jar");

        // Paul's path to TestComp. PLEASE do not take out.
        testcompJarFileName = "D:\\OSS\\PN\\java\\nanocontainer\\src\\test-comp\\TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to java/nanocontainer/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        File testCompJar2 = new File(testCompJar.getParentFile(), "TestComp2.jar");
        assertTrue(testCompJar.isFile());
        assertTrue(testCompJar2.isFile());

        // Set up parent
        ReflectionContainerAdapter parentContainerAdapter = new DefaultReflectionContainerAdapter();
        parentContainerAdapter.addClassLoaderURL(testCompJar.toURL());

        parentContainerAdapter.registerComponentImplementation("parentTestComp", "TestComp");

        parentContainerAdapter.registerComponentImplementation("java.lang.StringBuffer");


        PicoContainer parentContainerAdapterPico = parentContainerAdapter.getPicoContainer();
        Object parentTestComp = parentContainerAdapterPico.getComponentInstance("parentTestComp");
        assertEquals("TestComp", parentTestComp.getClass().getName());

        // Set up child
        ReflectionContainerAdapter childContainerAdapter = new DefaultReflectionContainerAdapter(parentContainerAdapter);
        childContainerAdapter.addClassLoaderURL(testCompJar2.toURL());
        childContainerAdapter.registerComponentImplementation("childTestComp", "TestComp2");

        PicoContainer childContainerAdapterPico = childContainerAdapter.getPicoContainer();
        Object childTestComp = childContainerAdapterPico.getComponentInstance("childTestComp");

        assertEquals("TestComp2", childTestComp.getClass().getName());

        assertNotSame(parentTestComp, childTestComp);

        assertSame("parentTestComp classloader should be parent of childTestComp classloader",
                parentTestComp.getClass().getClassLoader(),
                childTestComp.getClass().getClassLoader().getParent());

        assertSame(parentContainerAdapterPico, childContainerAdapterPico.getParent());
    }

    public static class AnotherFooComp {

    }

    public void testClassLoaderJugglingIsPossible() throws MalformedURLException, ClassNotFoundException {
        ReflectionContainerAdapter parentContainerAdapter = new DefaultReflectionContainerAdapter();

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/OSS/PN/java/nanocontainer/src/test-comp/TestComp.jar";
        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        parentContainerAdapter.registerComponentImplementation("foo", "org.nanocontainer.testmodel.DefaultWebServerConfig");

        Object fooWebServerConfig = parentContainerAdapter.getPicoContainer().getComponentInstance("foo");
        assertEquals("org.nanocontainer.testmodel.DefaultWebServerConfig", fooWebServerConfig.getClass().getName());

        ReflectionContainerAdapter childContainerAdapter = new DefaultReflectionContainerAdapter(parentContainerAdapter);
        childContainerAdapter.addClassLoaderURL(testCompJar.toURL());
        childContainerAdapter.registerComponentImplementation("bar", "TestComp");

        Object barTestComp = childContainerAdapter.getPicoContainer().getComponentInstance("bar");
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

}
