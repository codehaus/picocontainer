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
import org.xml.sax.InputSource;

import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;

import picocontainer.PicoInstantiationException;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoIntrospectionException;
import nanocontainer.testmodel.WebServer;
import nanocontainer.testmodel.ThingThatTakesParamsInConstructor;

public class DomRegistrationNanoContainerTestCase extends TestCase {

    public void testBasic() throws PicoRegistrationException, ParserConfigurationException, PicoInstantiationException, ClassNotFoundException, PicoIntrospectionException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<components>" +
                "      <component class=\"nanocontainer.testmodel.DefaultWebServerConfig\"/>" +
                "      <component type=\"nanocontainer.testmodel.WebServer\" class=\"nanocontainer.testmodel.WebServerImpl\"/>" +
                "</components>")));
        nc.instantiateComponents();
        assertTrue(nc.hasComponent(WebServer.class));
    }

    // Same test asa bove, but with components defined in an external XML file.
    public void testFromFile() throws PicoRegistrationException, ParserConfigurationException, PicoInstantiationException, ClassNotFoundException, FileNotFoundException, PicoIntrospectionException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        File xmlFile = getFileForXMLComponentRegistration();
        nc.registerComponents(new InputSource(new FileReader(xmlFile)));
        nc.instantiateComponents();
        assertTrue(nc.hasComponent(WebServer.class));
    }

    // This is more a demo than a test.  Compare ResourceBundleWebServerConfig to DefaultWebServerConfig.
    public void testResourceBundleConfig() throws PicoRegistrationException, ParserConfigurationException, PicoInstantiationException, ClassNotFoundException, PicoIntrospectionException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<components>" +
                "      <component class=\"nanocontainer.testmodel.ResourceBundleWebServerConfig\"/>" +
                "      <component type=\"nanocontainer.testmodel.WebServer\" class=\"nanocontainer.testmodel.WebServerImpl\"/>" +
                "</components>")));
        nc.instantiateComponents();
        assertTrue(nc.hasComponent(WebServer.class));
    }

    public void testParametersCanBePassedToComponent() throws PicoRegistrationException, ParserConfigurationException, PicoInstantiationException, ClassNotFoundException, PicoIntrospectionException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<components>" +
                "      <component class=\"" + ThingThatTakesParamsInConstructor.class.getName() + "\">" +
                "            <param type=\"java.lang.String\">a string</param>" +
                "            <param type=\"java.lang.Integer\">99</param>" +
                "      </component>" +
                "</components>")));
        nc.instantiateComponents();
        ThingThatTakesParamsInConstructor thing = (ThingThatTakesParamsInConstructor) nc.getComponent(ThingThatTakesParamsInConstructor.class);
        assertEquals("a string99", thing.getValue());
    }

    public void testDigesterConfig() throws PicoRegistrationException, ParserConfigurationException, PicoInstantiationException, ClassNotFoundException, PicoIntrospectionException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<components>" +
                "      <component class=\"nanocontainer.testmodel.DigesterWebServerConfig\"/>" +
                "      <component type=\"nanocontainer.testmodel.WebServer\" class=\"nanocontainer.testmodel.WebServerImpl\"/>" +
                "</components>")));
        nc.instantiateComponents();
        assertTrue(nc.hasComponent(WebServer.class));
    }

    public void testRegistrationMismatch() throws PicoRegistrationException, ParserConfigurationException, PicoIntrospectionException {
        DomRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        try {
            nc.registerComponent("Foo");
            fail("should have failed");
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

    // This is a bit of a hack.
    // If run inside IDEA, there is a different file path
    // than that of a Maven invocation.  This method is
    // really not something you have to do for a real
    // deployment.
    private File getFileForXMLComponentRegistration() {
        File compilationRoot = FileUtils.getRoot(getClass());
        // ../../src/DomTest.xml
        File file = new File(compilationRoot.getParentFile().getParentFile(), "src/test/DomTest.xml");
        if (!file.exists()) {
            file = new File("src/test/DomTest.xml");
        }
        if (!file.exists()) {
            file = new File("../nano/src/test/DomTest.xml");
        }
        return file;
    }
}
