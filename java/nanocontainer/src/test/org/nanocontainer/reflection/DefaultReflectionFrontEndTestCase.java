/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.picoextras.reflection;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picoextras.testmodel.ThingThatTakesParamsInConstructor;
import org.picoextras.testmodel.WebServerImpl;

import java.io.File;
import java.net.MalformedURLException;

public class DefaultReflectionFrontEndTestCase extends TestCase {

    public void testBasic() throws PicoRegistrationException, PicoInitializationException, ClassNotFoundException {
        ReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.DefaultWebServerConfig");
        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.WebServer", "org.picoextras.testmodel.WebServerImpl");
    }

    public void testProvision() throws PicoException, PicoInitializationException, ClassNotFoundException {
        ReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.DefaultWebServerConfig");
        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.WebServerImpl");

        assertNotNull("WebServerImpl should exist", reflectionFrontEnd.getPicoContainer().getComponentInstance(WebServerImpl.class));
        assertTrue("WebServerImpl should exist", reflectionFrontEnd.getPicoContainer().getComponentInstance(WebServerImpl.class) instanceof WebServerImpl);
    }

    public void testNoGenerationRegistration() throws PicoRegistrationException, PicoIntrospectionException {
        ReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
        try {
            reflectionFrontEnd.registerComponentImplementation("Ping");
            fail("should have failed");
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

    public void testParametersCanBePassedInStringForm() throws ClassNotFoundException, PicoException, PicoInitializationException {
        ReflectionContainerAdapter reflectionFrontEnd = new DefaultReflectionContainerAdapter();
        String className = ThingThatTakesParamsInConstructor.class.getName();

        reflectionFrontEnd.registerComponentImplementation(
                "thing",
                className,
                new String[]{
                    "java.lang.String",
                    "java.lang.Integer"
                },
                new String[]{
                    "hello",
                    "22"
                }
        );

        ThingThatTakesParamsInConstructor thing =
                (ThingThatTakesParamsInConstructor) reflectionFrontEnd.getPicoContainer().getComponentInstance("thing");
        assertNotNull("component not present", thing);
        assertEquals("hello22", thing.getValue());
    }

    public void testChildFrontEndCanRelyOnParentFrontEnd() throws MalformedURLException, ClassNotFoundException {
        String testcompJarFileName = System.getProperty("testcomp.jar");

        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/DEV/nano/reflection/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        File testCompJar2 = new File(testCompJar.getParentFile(), "TestComp2.jar");
        assertTrue(testCompJar.isFile());
        assertTrue(testCompJar2.isFile());

        // Set up parent
        ReflectionContainerAdapter parentFrontEnd = new DefaultReflectionContainerAdapter();
        parentFrontEnd.addClassLoaderURL(testCompJar.toURL());
        parentFrontEnd.registerComponentImplementation("parentTestComp", "TestComp");

        PicoContainer parentFrontEndPico = parentFrontEnd.getPicoContainer();
        Object parentTestComp = parentFrontEndPico.getComponentInstance("parentTestComp");
        assertEquals("TestComp", parentTestComp.getClass().getName());

        // Set up child
        ReflectionContainerAdapter childFrontEnd = new DefaultReflectionContainerAdapter(parentFrontEnd);
        childFrontEnd.addClassLoaderURL(testCompJar2.toURL());
        childFrontEnd.registerComponentImplementation("childTestComp", "TestComp2");

        PicoContainer childFrontEndPico = childFrontEnd.getPicoContainer();
        Object childTestComp = childFrontEndPico.getComponentInstance("childTestComp");
        assertEquals("TestComp2", childTestComp.getClass().getName());

        assertNotSame(parentTestComp, childTestComp);
        assertEquals("parentTestComp classloader should be parent of childTestComp classloader",
                parentTestComp.getClass().getClassLoader(),
                childTestComp.getClass().getClassLoader().getParent());

        assertSame(parentFrontEndPico, childFrontEndPico.getParent());
    }

    public static class AnotherFooComp {

    }

    public void testClassLoaderJugglingIsPossible() throws MalformedURLException, ClassNotFoundException {
        ReflectionContainerAdapter parentFrontEnd = new DefaultReflectionContainerAdapter();

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:\\DEV\\nano\\reflection\\src\\test-comp\\TestComp.jar";
        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        parentFrontEnd.registerComponentImplementation("foo", "org.picoextras.testmodel.DefaultWebServerConfig");

        Object fooWebServerConfig = parentFrontEnd.getPicoContainer().getComponentInstance("foo");
        assertEquals("org.picoextras.testmodel.DefaultWebServerConfig", fooWebServerConfig.getClass().getName());

        ReflectionContainerAdapter childFrontEnd = new DefaultReflectionContainerAdapter(parentFrontEnd);
        childFrontEnd.addClassLoaderURL(testCompJar.toURL());
        childFrontEnd.registerComponentImplementation("bar", "TestComp");

        Object barTestComp = childFrontEnd.getPicoContainer().getComponentInstance("bar");
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
