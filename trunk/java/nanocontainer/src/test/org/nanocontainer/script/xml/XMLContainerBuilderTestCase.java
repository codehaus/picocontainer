/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.script.xml;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.WebServerConfig;
import org.nanocontainer.testmodel.WebServerConfigComp;
import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jeppe Cramon
 * @author Mauro Talevi
 * @version $Revision$
 */
public class XMLContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    //TODO some tests for XMLContainerBuilder that use a classloader that is retrieved at testtime. 
    // i.e. not a programatic consequence of this.getClass().getClassLoader()
    
    public void testCreateSimpleContainer() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <componentimplementation class='java.lang.StringBuffer'/>" +
                "  <componentimplementation class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "  <componentimplementation key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        assertEquals(3, pico.getComponentInstances().size());
        assertNotNull(pico.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testAPicocontainerCanHostAChild() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <componentimplementation class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "  <componentimplementation class='java.lang.StringBuffer'/>" +
                "  <container>" +
                "    <componentimplementation key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "  </container>" +
                "</container>");

        XMLContainerBuilder builder = new XMLContainerBuilder(script, getClass().getClassLoader());
        PicoContainer pico = buildContainer(builder, null);
        assertNotNull(pico.getComponentInstance(DefaultWebServerConfig.class));

        StringBuffer sb = (StringBuffer) pico.getComponentInstance(StringBuffer.class);
        assertTrue(sb.indexOf("-WebServerImpl") != -1);
    }

    public void testClassLoaderHierarchy() throws ParserConfigurationException, ClassNotFoundException, SAXException, IOException, PicoCompositionException {
        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/OSS/PN/java/nanocontainer/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to nanocontainer/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        File testCompJar2 = new File(testCompJar.getParentFile(), "TestComp2.jar");
        File notStartableJar = new File(testCompJar.getParentFile(), "NotStartable.jar");

        assertTrue(testCompJar.isFile());
        assertTrue(testCompJar2.isFile());

        Reader script = new StringReader("" +
                "<container>" +
                "  <classpath>" +
                "    <element file='" + testCompJar.getCanonicalPath() + "'/>" +
                "  </classpath>" +
                "  <componentimplementation key='foo' class='TestComp'/>" +
                "  <container>" +
                "    <classpath>" +
                "      <element file='" + testCompJar2.getCanonicalPath() + "'/>" +
                "      <element file='" + notStartableJar.getCanonicalPath() + "'/>" +
                "    </classpath>" +
                "    <componentimplementation key='bar' class='TestComp2'/>" +
                "    <componentimplementation key='phony' class='NotStartable'/>" +
                "  </container>" +
                "  <componentimplementation class='java.lang.StringBuffer'/>" +
                "</container>");

        XMLContainerBuilder builder = new XMLContainerBuilder(script, getClass().getClassLoader());
        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(builder, null);

        Object fooTestComp = pico.getComponentInstance("foo");
        assertNotNull("Container should have a 'foo' component", fooTestComp);

        StringBuffer sb = (StringBuffer) pico.getComponentInstance(StringBuffer.class);
        assertTrue("Container should have instantiated a 'TestComp2' component because it is Startable", sb.indexOf("-TestComp2") != -1);
        // We are using the DefaultLifecycleManager, which only instantiates Startable components, and not non-Startable components.
        assertTrue("Container should NOT have instantiated a 'NotStartable' component because it is NOT Startable", sb.indexOf("-NotStartable") == -1);
    }


    public void testUnknownclassThrowsAssemblyException() throws Exception, SAXException, ParserConfigurationException, IOException {
        try {
            Reader script = new StringReader("" +
                    "<container>" +
                    "  <componentimplementation class='Foo'/>" +
                    "</container>");
            buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
            fail("Should have thrown a ClassNotFoundException");
        } catch (RuntimeException cnfe) {
        }
    }

    public void testEmptyScriptThrowsEmptyCompositionException() throws Exception, SAXException, ParserConfigurationException, IOException {
        try {
            Reader script = new StringReader("<container/>");
            buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        } catch (EmptyCompositionException expected) {
        }
    }

    public void testComponentInstanceWithFactoryCanBeUsed() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <componentinstance factory='org.nanocontainer.script.xml.XMLContainerBuilderTestCase$TestFactory'>" +
                "    <config-or-whatever/>" +
                "  </componentinstance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        Object instance = pico.getComponentInstances().get(0);
        assertNotNull(instance);
        assertTrue(instance instanceof String);
        assertEquals("Hello", instance.toString());
    }

    public void testComponentInstanceWithDefaultFactory() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <componentinstance>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </componentinstance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        Object instance = pico.getComponentInstances().get(0);
        assertNotNull(instance);
        assertTrue(instance instanceof TestBean);
        assertEquals(10, ((TestBean)instance).getFoo());
        assertEquals("hello", ((TestBean)instance).getBar());
    }

    public void testComponentInstanceWithBeanFactory() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <componentinstance factory='org.nanocontainer.script.xml.BeanComponentInstanceFactory'>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </componentinstance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        Object instance = pico.getComponentInstances().get(0);
        assertNotNull(instance);
        assertTrue(instance instanceof TestBean);
        assertEquals(10, ((TestBean)instance).getFoo());
        assertEquals("hello", ((TestBean)instance).getBar());
    }
    
    public void testComponentInstanceWithKey() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <componentinstance key='aKey'>" +
                "    <string>Ciao</string>" +
                "  </componentinstance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        Object instance = pico.getComponentInstance("aKey");
        assertNotNull(instance);
        assertTrue(instance instanceof String);
        assertEquals("Ciao", instance.toString());
    }

    public void testComponentInstanceWithFactoryAndKey() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <componentinstance factory='org.nanocontainer.script.xml.XMLContainerBuilderTestCase$TestFactory'" +
                "						key='aKey'>" +
                "    <config-or-whatever/>" +
                "  </componentinstance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        Object instance = pico.getComponentInstance("aKey");
        assertNotNull(instance);
        assertTrue(instance instanceof String);
        assertEquals("Hello", instance.toString());
    }
    
    public static class TestFactory implements XMLComponentInstanceFactory {
        public Object makeInstance(Element elem) throws ClassNotFoundException {
            return "Hello";
        }
    }

    public void testInstantiationOfComponentsWithParams() throws IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <componentimplementation class='org.nanocontainer.testmodel.WebServerConfigComp'>" +
                "    <parameter class='java.lang.String'>localhost</parameter>" +
                "    <parameter class='int'>8080</parameter>" +
                "  </componentimplementation>" +
                "  <componentimplementation key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>");
        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        assertNotNull(pico.getComponentInstance(WebServerConfigComp.class));
        WebServerConfigComp config = (WebServerConfigComp) pico.getComponentInstanceOfType(WebServerConfigComp.class);
        assertEquals("localhost", config.getHost());
        assertEquals(8080, config.getPort());
    }

    
    // This is of little value given that nested adapters can't be specified in XML.
    public void testComponentAdapterClassCanBeSpecifiedInContainerElement() throws IOException, ParserConfigurationException, SAXException {
        Reader script = new StringReader("" +
                "<container componentadapterfactory='" + ConstructorInjectionComponentAdapterFactory.class.getName() + "'>" +
                "  <componentimplementation class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null);
        Object wsc1 = pico.getComponentInstanceOfType(WebServerConfig.class);
        Object wsc2 = pico.getComponentInstanceOfType(WebServerConfig.class);

        assertNotSame(wsc1, wsc2);
    }
}
