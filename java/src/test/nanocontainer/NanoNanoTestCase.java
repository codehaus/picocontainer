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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;

import java.io.StringReader;

import picocontainer.PicoStartException;
import picocontainer.PicoRegistrationException;

public class NanoNanoTestCase extends TestCase {

    public void testStringRegistrationNanoContainerImpl()
            throws PicoRegistrationException, PicoStartException,
            ClassNotFoundException {

        StringRegistrationNanoContainer nc = new StringRegistrationNanoContainerImpl.Default();
        nc.registerComponent("picocontainer.PicoContainerImpl$Default");
        nc.registerComponent("nanocontainer.StringRegistrationNanoContainerImpl");

        nc.start();

        assertTrue("Should have a StringRegistrationNanoContainerImpl", nc.hasComponent(StringRegistrationNanoContainerImpl.class));

        tryUsingStringRegistrationNanoContainer((StringRegistrationNanoContainer) nc.getComponent(StringRegistrationNanoContainerImpl.class));

    }

    private void tryUsingStringRegistrationNanoContainer(StringRegistrationNanoContainer nc) throws PicoRegistrationException, ClassNotFoundException, PicoStartException {

        nc.registerComponent("nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("nanocontainer.testmodel.WebServer", "nanocontainer.testmodel.WebServerImpl");
        nc.start();
    }

    public void testDomRegistrationNanoContainerImpl()
            throws PicoRegistrationException, PicoStartException,
            ParserConfigurationException, ClassNotFoundException {

        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<conponents>" +
                "      <component class=\"picocontainer.PicoContainerImpl$Default\"/>" +
                "      <component class=\"nanocontainer.DomRegistrationNanoContainer$WithParentContainer\"/>" +
                "</conponents>")));

        nc.start();

        assertTrue("Should have a DomRegistrationNanoContainer.WithParentContainer", nc.hasComponent(DomRegistrationNanoContainer.WithParentContainer.class));

        tryUsingInputSourceRegistrationNanoContainer((InputSourceRegistrationNanoContainer) nc.getComponent(DomRegistrationNanoContainer.WithParentContainer.class));

    }

    private void tryUsingInputSourceRegistrationNanoContainer(InputSourceRegistrationNanoContainer nc2) throws PicoRegistrationException, ClassNotFoundException, PicoStartException {
        nc2.registerComponents(new InputSource(new StringReader(
                "<conponents>" +
                "      <component class=\"nanocontainer.testmodel.DefaultWebServerConfig\"/>" +
                "      <component type=\"nanocontainer.testmodel.WebServer\" class=\"nanocontainer.testmodel.WebServerImpl\"/>" +
                "</conponents>")));

        nc2.start();
    }

}
