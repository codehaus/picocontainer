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
import org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor;
import org.nanocontainer.testmodel.WebServer;
import org.xml.sax.InputSource;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.lifecycle.Startable;
import org.picocontainer.lifecycle.LifecyclePicoAdaptor;
import org.picocontainer.lifecycle.DefaultLifecyclePicoAdaptor;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;

public class DomRegistrationNanoContainerTestCase extends TestCase {

    public void testBasic() throws PicoRegistrationException, ParserConfigurationException, PicoInitializationException, ClassNotFoundException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        registerInstantiateAndCheckComponent(nc);
    }

    // Same test as above, but with components defined in an external XML file.
    public void testFromFile() throws PicoRegistrationException, ParserConfigurationException, PicoInitializationException, ClassNotFoundException, FileNotFoundException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        File xmlFile = getFileForXMLComponentRegistration();
        nc.registerComponents(new InputSource(new FileReader(xmlFile)));
        nc.instantiateComponents();
        assertTrue(nc.hasComponent(WebServer.class));
    }

    // This is more a demo than a test.  Compare ResourceBundleWebServerConfig to DefaultWebServerConfig.
    public void testResourceBundleConfig() throws PicoRegistrationException, ParserConfigurationException, PicoInitializationException, ClassNotFoundException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<components>" +
                "      <component class=\"org.nanocontainer.testmodel.ResourceBundleWebServerConfig\"/>" +
                "      <component type=\"org.nanocontainer.testmodel.WebServer\" class=\"org.nanocontainer.testmodel.WebServerImpl\"/>" +
                "</components>")));
        nc.instantiateComponents();
        assertTrue(nc.hasComponent(WebServer.class));
    }

    public void testParametersCanBePassedToComponent() throws PicoRegistrationException, ParserConfigurationException, ClassNotFoundException, PicoInitializationException {
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

    public void testDigesterConfig() throws PicoRegistrationException, ParserConfigurationException, PicoInitializationException, ClassNotFoundException {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<components>" +
                "      <component class=\"org.nanocontainer.testmodel.DigesterWebServerConfig\"/>" +
                "      <component type=\"org.nanocontainer.testmodel.WebServer\" class=\"org.nanocontainer.testmodel.WebServerImpl\"/>" +
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

    public static class Startme implements Startable {
        public boolean started;
        public Startme() {
        }
        public void start() throws Exception {
            started = true;
        }

    }

    public void testLifecycleCompatability() throws Exception {
        InputSourceRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        nc.registerComponents(new InputSource(new StringReader(
                "<components>" +
                "      <component class=\"org.nanocontainer.DomRegistrationNanoContainerTestCase$Startme\"/>" +
                "</components>")));
        nc.instantiateComponents();
        assertTrue(nc.hasComponent(Startme.class));
        LifecyclePicoAdaptor lifecycleAdaptor = new DefaultLifecyclePicoAdaptor(nc);
        lifecycleAdaptor.start();
        Startme sm = (Startme) nc.getComponent(Startme.class);
        assertTrue("Should have been started", sm.started);
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

    public void testWithCustomDocumentBuilder() throws ParserConfigurationException, ClassNotFoundException,
            PicoRegistrationException, PicoInitializationException
    {

        DomRegistrationNanoContainer nc = new DomRegistrationNanoContainer.WithCustomDocumentBuilder(DocumentBuilderFactory.newInstance().newDocumentBuilder());
        registerInstantiateAndCheckComponent(nc);

    }

    public void testWithParentContainer() throws ParserConfigurationException, ClassNotFoundException,
            PicoRegistrationException, PicoInitializationException
    {

        DomRegistrationNanoContainer nc = new DomRegistrationNanoContainer.WithParentContainer(new DefaultPicoContainer.Default());
        registerInstantiateAndCheckComponent(nc);

    }

    public void testWithClassLoader() throws ParserConfigurationException, ClassNotFoundException,
            PicoRegistrationException, PicoInitializationException
    {

        DomRegistrationNanoContainer nc = new DomRegistrationNanoContainer.WithClassLoader(new URLClassLoader(new URL[0]));
        registerInstantiateAndCheckComponent(nc);

    }


    public void testIncorrectXMLThrowsException() throws ParserConfigurationException,
            ClassNotFoundException, PicoRegistrationException
    {
        DomRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        try
        {
            nc.registerComponents(new InputSource(new StringReader(
                    "<components>" +
                    "      <component class=\"org.nanocontainer.testmodel.DefaultWebServerConfig\"/>" +
                    "      <component type=\"org.nanocontainer.testmodel.WebServer\" " +
                    "                class=\"org.nanocontainer.testmodel.WebServerImpl\"/>" +
                    "</WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWww>")));
            fail("should have barfed");
        }
        catch (NanoTextRegistrationException e)
        {
            assertTrue("Should have contained end elem", e.getMessage().indexOf("</components>") > 0 );
        }
    }

    public void testIOExceptionThownForPoorRegistration() throws ParserConfigurationException,
            ClassNotFoundException, PicoRegistrationException
    {
        DomRegistrationNanoContainer nc = new DomRegistrationNanoContainer.Default();
        try
        {
            nc.registerComponents(new InputSource("foo"));
        }
        catch (NanoTextRegistrationException e)
        {
            assertTrue("Should have contained end elem", e.getMessage().indexOf("IOException:") >= 0 );
            assertTrue("Should have contained end elem", e.getMessage().indexOf("foo") > 0 );
        }
    }


    private void registerInstantiateAndCheckComponent(InputSourceRegistrationNanoContainer nc) throws PicoRegistrationException, ClassNotFoundException, PicoInitializationException
    {
        nc.registerComponents(new InputSource(new StringReader(
                "<components>" +
                "      <component class=\"org.nanocontainer.testmodel.DefaultWebServerConfig\"/>" +
                "      <component type=\"org.nanocontainer.testmodel.WebServer\" " +
                "                class=\"org.nanocontainer.testmodel.WebServerImpl\"/>" +
                "</components>")));
        nc.instantiateComponents();
        assertTrue(nc.hasComponent(WebServer.class));
    }

}
