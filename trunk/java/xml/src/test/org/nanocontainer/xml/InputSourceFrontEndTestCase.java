package org.nanocontainer.xml;

import junit.framework.TestCase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.picocontainer.PicoContainer;
import org.nanocontainer.testmodel.DefaultWebServerConfig;

import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.io.IOException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InputSourceFrontEndTestCase extends TestCase {
    public void testCreateSimpleContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "      <component classname='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "      <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>"));

        InputSourceFrontEnd inputSourceContainerFactory = new InputSourceFrontEnd();
        PicoContainer picoContainer = inputSourceContainerFactory.createPicoContainer(inputSource);
        assertNotNull(picoContainer.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testCreateHierarchicalContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
        InputSource inputSource = new InputSource(new StringReader(
                "<container>" +
                "      <component classname='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "      <container>" +
                "          <component key='org.nanocontainer.testmodel.WebServer' classname='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "      </container>" +
                "</container>"));

        InputSourceFrontEnd inputSourceContainerFactory = new InputSourceFrontEnd();
        PicoContainer rootContainer = inputSourceContainerFactory.createPicoContainer(inputSource);
        assertNotNull(rootContainer.getComponentInstance(DefaultWebServerConfig.class));

//        PicoContainer childContainer = rootContainer.getChildren().iterator().next();
    }
}
