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
import org.xml.sax.InputSource;

import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;

import picocontainer.PicoStartException;
import picocontainer.PicoRegistrationException;

public class DomRegistrationNanoContainerTestCase extends TestCase {

    public void testBasic() throws PicoRegistrationException, ParserConfigurationException, PicoStartException, ClassNotFoundException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<conponents>" +
                "      <component class=\"nanocontainer.testmodel.DefaultWebServerConfig\"/>" +
                "      <component type=\"nanocontainer.testmodel.WebServer\" class=\"nanocontainer.testmodel.WebServerImpl\"/>" +
                "</conponents>")));
        nc.start();
    }

    public void testAlternate() throws PicoRegistrationException, ParserConfigurationException, PicoStartException, ClassNotFoundException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<conponents>" +
                "      <component class=\"nanocontainer.testmodel.ResourceBundleWebServerConfig\"/>" +
                "      <component type=\"nanocontainer.testmodel.WebServer\" class=\"nanocontainer.testmodel.WebServerImpl\"/>" +
                "</conponents>")));
        nc.start();
    }


    public void testRegistrationMismatch() throws PicoRegistrationException, ParserConfigurationException {
        DomRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        try {
            nc.registerComponent("Foo");
            fail("should have failed");
        } catch (ClassNotFoundException e) {
            // expected
        }

    }

}
