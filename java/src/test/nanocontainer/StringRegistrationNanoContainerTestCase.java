/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer;

import junit.framework.TestCase;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoIntrospectionException;
import nanocontainer.testmodel.WebServerImpl;
import nanocontainer.testmodel.ThingThatTakesParamsInConstructor;

public class StringRegistrationNanoContainerTestCase extends TestCase {

    public void testBasic() throws PicoRegistrationException, PicoInstantiationException, ClassNotFoundException, PicoIntrospectionException {
        StringRegistrationNanoContainer nc = new StringRegistrationNanoContainerImpl.Default();
        nc.registerComponent("nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("nanocontainer.testmodel.WebServer", "nanocontainer.testmodel.WebServerImpl");
        nc.instantiateComponents();
    }

    public void testProvision() throws PicoRegistrationException, PicoInstantiationException, ClassNotFoundException, PicoIntrospectionException {
        StringRegistrationNanoContainerImpl nc = new StringRegistrationNanoContainerImpl.Default();
        nc.registerComponent("nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("nanocontainer.testmodel.WebServerImpl");
        nc.instantiateComponents();
        assertTrue("WebServerImpl should exist", nc.hasComponent(WebServerImpl.class));
        assertNotNull("WebServerImpl should exist", nc.getComponent(WebServerImpl.class));
        assertTrue("WebServerImpl should exist", nc.getComponent(WebServerImpl.class) instanceof WebServerImpl);
    }

    public void testNoGenerationRegistration() throws PicoRegistrationException, PicoIntrospectionException {
        StringRegistrationNanoContainer nc = new StringRegistrationNanoContainerImpl.Default();
        try {
            nc.registerComponent("Foo");
            fail("should have failed");
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

    public void testParametersCanBePassedInStringForm() throws ClassNotFoundException, PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {
        StringRegistrationNanoContainer nc = new StringRegistrationNanoContainerImpl.Default();
        String className = ThingThatTakesParamsInConstructor.class.getName();

        nc.registerComponent(className);
        nc.addParameterToComponent(className, "java.lang.String", "hello");
        nc.addParameterToComponent(className, "java.lang.Integer", "22");

        nc.instantiateComponents();

        ThingThatTakesParamsInConstructor thing = (ThingThatTakesParamsInConstructor) nc.getComponent(ThingThatTakesParamsInConstructor.class);
        assertNotNull("component not present", thing);
        assertEquals("hello22", thing.getValue());
    }

}
