/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer;

import junit.framework.TestCase;
import picocontainer.PicoStartException;
import nanocontainer.testmodel.WebServerImpl;

public class StringRegistrationNanoContainerTestCase extends TestCase {

    public void testBasic() throws NanoRegistrationException, PicoStartException, ClassNotFoundException {
        StringRegistrationNanoContainer nc = new StringRegistrationNanoContainerImpl.Default();
        nc.registerComponent("nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("nanocontainer.testmodel.WebServer", "nanocontainer.testmodel.WebServerImpl");
        nc.start();
    }

    public void testProvision() throws NanoRegistrationException, PicoStartException, ClassNotFoundException {
        StringRegistrationNanoContainerImpl nc = new StringRegistrationNanoContainerImpl.Default();
        nc.registerComponent("nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("nanocontainer.testmodel.WebServerImpl");
        nc.start();
        assertTrue("WebServerImpl should exist", nc.hasComponent(WebServerImpl.class));
        assertNotNull("WebServerImpl should exist", nc.getComponent(WebServerImpl.class));
        assertTrue("WebServerImpl should exist", nc.getComponent(WebServerImpl.class) instanceof WebServerImpl);
    }

    public void testNoGenerationRegistration() throws NanoRegistrationException {
        StringRegistrationNanoContainer nc = new StringRegistrationNanoContainerImpl.Default();
        try {
            nc.registerComponent("Foo");
            fail("should have failed");
        } catch (ClassNotFoundException e) {
            // expected
        }


    }

}
