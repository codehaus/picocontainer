/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mike Hogan                                               *
 *****************************************************************************/

package org.nanocontainer;

import junit.framework.TestCase;
import org.xml.sax.InputSource;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;

public class ConfiguringNanoContainerTestCase extends TestCase {

    public void testCanDealWithNoConfiguration()
            throws ParserConfigurationException, PicoRegistrationException, PicoInitializationException, ClassNotFoundException {
        final String xml =
                "<components>" +
                "      <component class=\"org.nanocontainer.MockComponentImpl\"/>" +
                "</components>";
        final InputSourceRegistrationNanoContainer container = configureContainer(xml);
        final MockComponent component;
        Object o = container.getComponents().toArray()[0];
        component = (MockComponent) o;
        assertNotNull(component);
    }

    public void testCanDealWithNonConfiguringXML()
            throws ParserConfigurationException, PicoRegistrationException, PicoInitializationException, ClassNotFoundException {
        final String xml =
                "<components>" +
                "      <component class=\"org.nanocontainer.MockComponentImpl\">" +
                "            <nonconfiguring/>" +
                "      </component>" +
                "</components>";
        final InputSourceRegistrationNanoContainer container = configureContainer(xml);
        assertNotNull(container.getComponents().toArray()[0]);
    }

    public void testCanConfigureWithSingleString()
            throws ParserConfigurationException, PicoRegistrationException,
            PicoInitializationException, ClassNotFoundException {
        final String xml =
                "<components>" +
                "      <component class=\"org.nanocontainer.MockComponentImpl\">" +
                "            <server>server_name</server>" +
                "      </component>" +
                "</components>";
        final InputSourceRegistrationNanoContainer container = configureContainer(xml);
        final MockComponent component = (MockComponent) container.getComponents().toArray()[0];
        assertEquals("server_name", component.getServer());
    }

    public void testCanConfigureWithSingleStringAndTypeSpecification()
            throws ParserConfigurationException, PicoRegistrationException, PicoInitializationException, ClassNotFoundException {
        final String xml =
                "<components>" +
                "      <component type=\"org.nanocontainer.MockComponent\" class=\"org.nanocontainer.MockComponentImpl\">" +
                "            <server>server_name</server>" +
                "      </component>" +
                "</components>";
        final InputSourceRegistrationNanoContainer container = configureContainer(xml);
        final MockComponent component = (MockComponent) container.getComponents().toArray()[0];
        assertEquals("server_name", component.getServer());
    }

    public void testCanConfigureWithSingleInteger()
            throws ParserConfigurationException, PicoRegistrationException, PicoInitializationException, ClassNotFoundException {
        final String xml =
                "<components>" +
                "      <component class=\"org.nanocontainer.MockComponentImpl\">" +
                "            <port>12345</port>" +
                "      </component>" +
                "</components>";
        final InputSourceRegistrationNanoContainer container = configureContainer(xml);
        final MockComponent component = (MockComponent) container.getComponents().toArray()[0];
        assertEquals(12345, component.getPort());
    }

    public void testCanConfigureWithMultipleIntegers()
            throws ParserConfigurationException, PicoRegistrationException, PicoInitializationException, ClassNotFoundException {
        final String xml =
                "<components>" +
                "      <component class=\"org.nanocontainer.MockComponentImpl\">" +
                "            <registers>" +
                "                  <register>1</register>" +
                "                  <register>2</register>" +
                "                  <register>3</register>" +
                "            </registers>" +
                "      </component>" +
                "</components>";
        final InputSourceRegistrationNanoContainer container = configureContainer(xml);
        final MockComponent component = (MockComponent) container.getComponents().toArray()[0];
        assertEquals(3, component.getNumRegisters());
        assertTrue(component.hasRegister(1));
        assertTrue(component.hasRegister(2));
        assertTrue(component.hasRegister(3));
    }

    private InputSourceRegistrationNanoContainer configureContainer(final String xml)
            throws ParserConfigurationException, PicoRegistrationException, ClassNotFoundException, PicoInitializationException {
        final InputSourceRegistrationNanoContainer container = new ConfiguringNanoContainerImpl.Default();
        container.registerComponents(new InputSource(new StringReader(
                xml)));
        container.instantiateComponents();
        return container;
    }

}