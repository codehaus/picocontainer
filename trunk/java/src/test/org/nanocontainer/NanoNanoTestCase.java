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
import org.xml.sax.InputSource;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.nanocontainer.xml.InputSourceRegistrationNanoContainer;
import org.nanocontainer.xml.DomRegistrationNanoContainer;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.WebServer;

import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;

public class NanoNanoTestCase extends TestCase {

    public void testStringRegistrationNanoContainerImpl()
            throws PicoRegistrationException, PicoInitializationException,
            ClassNotFoundException {

        StringRegistrationNanoContainer nc = new DefaultStringRegistrationNanoContainer.Default();

        nc.registerComponent("org.nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("org.nanocontainer.testmodel.WebServer", "org.nanocontainer.testmodel.WebServerImpl");

        assertTrue(nc.hasComponent(DefaultWebServerConfig.class));
        assertTrue(nc.hasComponent(WebServer.class));

    }

    public void testDomRegistrationNanoContainerImpl()
            throws PicoRegistrationException, PicoInitializationException,
            ParserConfigurationException, ClassNotFoundException {

        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<conponents>" +
                "      <component class=\"org.nanocontainer.testmodel.DefaultWebServerConfig\"/>" +
                "      <component type=\"org.nanocontainer.testmodel.WebServer\" class=\"org.nanocontainer.testmodel.WebServerImpl\"/>" +
                "</conponents>")));

        assertTrue(nc.hasComponent(DefaultWebServerConfig.class));
        assertTrue(nc.hasComponent(WebServer.class));
    }
}
