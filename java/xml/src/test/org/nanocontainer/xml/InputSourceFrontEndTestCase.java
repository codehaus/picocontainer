package org.nanocontainer.xml;

import junit.framework.TestCase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.picocontainer.PicoContainer;
import org.nanocontainer.testmodel.DefaultWebServerConfig;

import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.io.IOException;
import java.io.File;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InputSourceFrontEndTestCase extends TestCase {
    public void testCreateSimpleContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, EmptyXmlConfigurationException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <component classname='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "    <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>"));

        InputSourceFrontEnd inputSourceContainerFactory = new InputSourceFrontEnd();
        PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(inputSource);
        assertNotNull(picoContainer.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testPicoInPico() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, EmptyXmlConfigurationException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <component classname='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "    <container>" +
                "        <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "    </container>" +
                "</container>"));

        InputSourceFrontEnd inputSourceFrontEnd = new InputSourceFrontEnd();
        PicoContainer rootContainer = inputSourceFrontEnd.createPicoContainer(inputSource);
        assertNotNull(rootContainer.getComponentInstance(DefaultWebServerConfig.class));

        PicoContainer childContainer = (PicoContainer) rootContainer.getChildContainers().iterator().next();
        assertNotNull(childContainer.getComponentInstance("org.nanocontainer.testmodel.WebServer"));
    }

    public void testClassLoaderHierarchy() throws ParserConfigurationException, ClassNotFoundException, SAXException, IOException, EmptyXmlConfigurationException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "    <classpath>" +
                "        <element file='" + testCompJar.getAbsolutePath() + "'/>" +
                "    </classpath>" +
                "    <component key='foo' classname='TestComp'/>" +
                "    <container>" +
                "        <component key='bar' classname='TestComp'/>" +
                "    </container>" +
                "</container>"));

        InputSourceFrontEnd inputSourceFrontEnd = new InputSourceFrontEnd();
        PicoContainer rootContainer = inputSourceFrontEnd.createPicoContainer(inputSource);
        Object fooTestComp = rootContainer.getComponentInstance("foo");
        assertNotNull(fooTestComp);

        PicoContainer childContainer = (PicoContainer) rootContainer.getChildContainers().iterator().next();
        Object barTestComp = childContainer.getComponentInstance("bar");
        assertNotNull(barTestComp);

        assertNotSame(fooTestComp, barTestComp);
    }

    public void testInstantiateXmlWithMissingComponent() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader(
                    "<container>" +
                    "      <component classname='Foo'/>" +
                    "</container>"));
            PicoContainer rootContainer = new InputSourceFrontEnd().createPicoContainer(inputSource);
            fail("Should have thrown a ClassNotFoundException");
        } catch (ClassNotFoundException cnfe) {
        }

    }

    public void testInstantiateEmptyXml() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            InputSource inputSource = new InputSource(new StringReader(
                    "<container>" +
                    "</container>"));
            PicoContainer rootContainer = new InputSourceFrontEnd().createPicoContainer(inputSource);
            fail("Should have thrown a EmptyXmlConfigurationException");
        } catch (EmptyXmlConfigurationException cnfe) {
        }
    }
}
