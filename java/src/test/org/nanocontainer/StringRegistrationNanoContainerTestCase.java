/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer;

import junit.framework.TestCase;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor;
import org.nanocontainer.testmodel.WebServerImpl;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoException;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Vector;

public class StringRegistrationNanoContainerTestCase extends TestCase {

    public void testBasic() throws PicoRegistrationException, PicoInitializationException, ClassNotFoundException {
        StringRegistrationNanoContainer nc = new DefaultStringRegistrationNanoContainer();
        nc.registerComponent("org.nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("org.nanocontainer.testmodel.WebServer", "org.nanocontainer.testmodel.WebServerImpl");
//        nc.instantiateComponents();
    }

    public void testProvision() throws PicoException, PicoInitializationException, ClassNotFoundException {
        DefaultStringRegistrationNanoContainer nc = new DefaultStringRegistrationNanoContainer();
        nc.registerComponent("org.nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("org.nanocontainer.testmodel.WebServerImpl");
//        nc.instantiateComponents();
        assertTrue("WebServerImpl should exist", nc.hasComponent(WebServerImpl.class));
        assertNotNull("WebServerImpl should exist", nc.getComponentInstance(WebServerImpl.class));
        assertTrue("WebServerImpl should exist", nc.getComponentInstance(WebServerImpl.class) instanceof WebServerImpl);
    }

    public void testNoGenerationRegistration() throws PicoRegistrationException, PicoIntrospectionException {
        StringRegistrationNanoContainer nc = new DefaultStringRegistrationNanoContainer();
        try {
            nc.registerComponent("Ping");
            fail("should have failed");
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

    public void testParametersCanBePassedInStringForm() throws ClassNotFoundException, PicoException, PicoInitializationException {
        StringRegistrationNanoContainer nc = new DefaultStringRegistrationNanoContainer();
        String className = ThingThatTakesParamsInConstructor.class.getName();

        nc.registerComponent(
                className,
                className,
                new String[] {
                    "java.lang.String",
                    "java.lang.Integer"
                },
                new String[] {
                    "hello",
                    "22"
                }
        );

//        nc.instantiateComponents();

        ThingThatTakesParamsInConstructor thing = (ThingThatTakesParamsInConstructor) nc.getComponentInstance(ThingThatTakesParamsInConstructor.class);
        assertNotNull("component not present", thing);
        assertEquals("hello22", thing.getValue());
    }

    public void testGetComponentTypes() throws ClassNotFoundException, PicoInitializationException, PicoRegistrationException {

        StringRegistrationNanoContainer nc = new DefaultStringRegistrationNanoContainer();

        nc.registerComponent("org.nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("org.nanocontainer.testmodel.WebServerImpl");

//        nc.instantiateComponents();

        Collection types = nc.getComponentKeys();
        assertEquals("There should be 2 types", 2, types.size());
        assertTrue("There should be a One type", types.contains(DefaultWebServerConfig.class));
    }

    public void testStringContainerWithClassLoader() throws ClassNotFoundException, PicoException, PicoRegistrationException {

        StringRegistrationNanoContainer nc = new DefaultStringRegistrationNanoContainer(new URLClassLoader(new URL[0]));

        nc.registerComponent("org.nanocontainer.testmodel.DefaultWebServerConfig");

//        nc.instantiateComponents();

        Collection types = nc.getComponentKeys();
        assertEquals("There should be 1 types", 1, types.size());

        try
        {
            nc.getComponentInstance(Vector.class);
            //fail("should have barfed");
        }
        catch (ClassCastException e)
        {
            // ecpected
        }

    }


}
