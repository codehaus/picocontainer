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

import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;

public class NanoNanoTestCase extends TestCase {

    public void testStringRegistrationNanoContainerImpl()
            throws PicoRegistrationException, PicoInitializationException,
            ClassNotFoundException {

        StringRegistrationNanoContainer nc = new StringRegistrationNanoContainerImpl.Default();
        nc.registerComponent("picocontainer.hierarchical.HierarchicalPicoContainer$Default");
        nc.registerComponent("nanocontainer.StringRegistrationNanoContainerImpl$WithParentContainer");

        nc.instantiateComponents();

        assertTrue("Should have a StringRegistrationNanoContainerImpl", nc.hasComponent(StringRegistrationNanoContainerImpl.WithParentContainer.class));

        tryUsingStringRegistrationNanoContainer((StringRegistrationNanoContainer) nc.getComponent(StringRegistrationNanoContainerImpl.WithParentContainer.class));

    }

    private void tryUsingStringRegistrationNanoContainer(StringRegistrationNanoContainer nc)
            throws PicoRegistrationException, ClassNotFoundException, PicoInitializationException {

        nc.registerComponent("org.nanocontainer.testmodel.DefaultWebServerConfig");
        nc.registerComponent("org.nanocontainer.testmodel.WebServer", "org.nanocontainer.testmodel.WebServerImpl");
        nc.instantiateComponents();
    }

    public void testDomRegistrationNanoContainerImpl()
            throws PicoRegistrationException, PicoInitializationException,
            ParserConfigurationException, ClassNotFoundException {

        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<conponents>" +
                "      <component class=\"picocontainer.hierarchical.HierarchicalPicoContainer$Default\"/>" +
                "      <component class=\"nanocontainer.DomRegistrationNanoContainer$WithParentContainer\"/>" +
                "</conponents>")));

        nc.instantiateComponents();

        assertTrue("Should have a DomRegistrationNanoContainer.WithParentContainer", nc.hasComponent(DomRegistrationNanoContainer.WithParentContainer.class));

        tryUsingInputSourceRegistrationNanoContainer((InputSourceRegistrationNanoContainer) nc.getComponent(DomRegistrationNanoContainer.WithParentContainer.class));

    }

    private void tryUsingInputSourceRegistrationNanoContainer(InputSourceRegistrationNanoContainer nc2)
            throws PicoRegistrationException, ClassNotFoundException, PicoInitializationException {
        nc2.registerComponents(new InputSource(new StringReader(
                "<conponents>" +
                "      <component class=\"org.nanocontainer.testmodel.DefaultWebServerConfig\"/>" +
                "      <component type=\"org.nanocontainer.testmodel.WebServer\" class=\"org.nanocontainer.testmodel.WebServerImpl\"/>" +
                "</conponents>")));

        nc2.instantiateComponents();
    }

}
