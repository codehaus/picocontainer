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
import org.picocontainer.*;
import org.picoextras.testmodel.DefaultWebServerConfig;
import org.picoextras.testmodel.ThingThatTakesParamsInConstructor;
import org.picoextras.testmodel.WebServerImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Vector;

public class DefaultReflectionFrontEndTestCase extends TestCase {

    public void testBasic() throws PicoRegistrationException, PicoInitializationException, ClassNotFoundException {
        ReflectionFrontEnd reflectionFrontEnd = new DefaultReflectionFrontEnd();
        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.DefaultWebServerConfig");
        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.WebServer", "org.picoextras.testmodel.WebServerImpl");
    }

    public void testProvision() throws PicoException, PicoInitializationException, ClassNotFoundException {
        ReflectionFrontEnd reflectionFrontEnd = new DefaultReflectionFrontEnd();
        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.DefaultWebServerConfig");
        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.WebServerImpl");

        assertTrue("WebServerImpl should exist", reflectionFrontEnd.getPicoContainer().hasComponent(WebServerImpl.class));
        assertNotNull("WebServerImpl should exist", reflectionFrontEnd.getPicoContainer().getComponentInstance(WebServerImpl.class));
        assertTrue("WebServerImpl should exist", reflectionFrontEnd.getPicoContainer().getComponentInstance(WebServerImpl.class) instanceof WebServerImpl);
    }

    public void testNoGenerationRegistration() throws PicoRegistrationException, PicoIntrospectionException {
        ReflectionFrontEnd reflectionFrontEnd = new DefaultReflectionFrontEnd();
        try {
            reflectionFrontEnd.registerComponentImplementation("Ping");
            fail("should have failed");
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

    public void testParametersCanBePassedInStringForm() throws ClassNotFoundException, PicoException, PicoInitializationException {
        ReflectionFrontEnd reflectionFrontEnd = new DefaultReflectionFrontEnd();
        String className = ThingThatTakesParamsInConstructor.class.getName();

        reflectionFrontEnd.registerComponentImplementation(className,
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
                (ThingThatTakesParamsInConstructor) reflectionFrontEnd.getPicoContainer().getComponentInstance(ThingThatTakesParamsInConstructor.class);
        assertNotNull("component not present", thing);
        assertEquals("hello22", thing.getValue());
    }

    public void testGetComponentTypes() throws ClassNotFoundException, PicoInitializationException, PicoRegistrationException {

        ReflectionFrontEnd reflectionFrontEnd = new DefaultReflectionFrontEnd();

        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.DefaultWebServerConfig");
        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.WebServerImpl");

        Collection keys = reflectionFrontEnd.getPicoContainer().getComponentKeys();
        assertEquals("There should be 2 keys", 2, keys.size());
        assertTrue("There should be a One key", keys.contains(DefaultWebServerConfig.class));
    }

    public void testStringContainerWithClassLoader() throws ClassNotFoundException, PicoException, PicoRegistrationException {

        ReflectionFrontEnd reflectionFrontEnd = new DefaultReflectionFrontEnd(DefaultWebServerConfig.class.getClassLoader());

        reflectionFrontEnd.registerComponentImplementation("org.picoextras.testmodel.DefaultWebServerConfig");

        Collection keys = reflectionFrontEnd.getPicoContainer().getComponentKeys();
        assertEquals("There should be 1 key", 1, keys.size());

        try {
            reflectionFrontEnd.getPicoContainer().getComponentInstance(Vector.class);
            //fail("should have barfed");
        } catch (ClassCastException e) {
            // ecpected
        }
    }

    public void testChildFrontEndCanRelyOnParentFrontEnd() throws MalformedURLException, ClassNotFoundException {
        ReflectionFrontEnd parentFrontEnd = new DefaultReflectionFrontEnd();

        parentFrontEnd.addClassLoaderURL(findResource("TestComp.jar"));

        parentFrontEnd.registerComponentImplementation("foo", "TestComp");

        PicoContainer parentFrontEndPico = parentFrontEnd.getPicoContainer();
        Object fooTestComp = parentFrontEndPico.getComponentInstance("foo");
        assertEquals("TestComp", fooTestComp.getClass().getName());

        ReflectionFrontEnd childFrontEnd = new DefaultReflectionFrontEnd(parentFrontEnd);
        childFrontEnd.addClassLoaderURL(findResource("TestComp2.jar"));

        childFrontEnd.registerComponentImplementation("bar", "TestComp2");

        PicoContainer childFrontEndPico = childFrontEnd.getPicoContainer();
        Object barTestComp = childFrontEndPico.getComponentInstance("bar");
        assertEquals("TestComp2", barTestComp.getClass().getName());

        assertNotSame(fooTestComp, barTestComp);
        assertEquals("foo classloader should be parent of bar",
                fooTestComp.getClass().getClassLoader(), barTestComp.getClass().getClassLoader().getParent());
        Collection childContainers = parentFrontEndPico.getChildContainers();
        assertTrue(childContainers.contains(childFrontEndPico));
    }

    private URL findResource(String resourcePath) {
        URL resource = getClass().getResource("/" + resourcePath);
        assertNotNull("add " + resourcePath + " to the class-path", resource);
        return resource;
    }

    public static class AnotherFooComp {

    }

    public void testClassLoaderJugglingIsPossible() throws MalformedURLException, ClassNotFoundException {
        ReflectionFrontEnd parentFrontEnd = new DefaultReflectionFrontEnd();

        parentFrontEnd.registerComponentImplementation("foo", "org.picoextras.testmodel.DefaultWebServerConfig");

        Object fooWebServerConfig = parentFrontEnd.getPicoContainer().getComponentInstance("foo");
        assertEquals("org.picoextras.testmodel.DefaultWebServerConfig", fooWebServerConfig.getClass().getName());

        ReflectionFrontEnd childFrontEnd = new DefaultReflectionFrontEnd(parentFrontEnd);
        childFrontEnd.addClassLoaderURL(findResource("TestComp.jar"));
        childFrontEnd.registerComponentImplementation("bar", "TestComp");

        Object barTestComp = childFrontEnd.getPicoContainer().getComponentInstance("bar");
        assertEquals("TestComp", barTestComp.getClass().getName());

        assertNotSame("components should not have same ClassLoader",
                fooWebServerConfig.getClass().getClassLoader(), barTestComp.getClass().getClassLoader());

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
