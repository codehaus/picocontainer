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
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
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
                "  <component-implementation class='java.lang.StringBuffer'/>" +
                "  <component-implementation class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "  <component-implementation key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>");

        XMLContainerBuilder builder = new XMLContainerBuilder(script, getClass().getClassLoader());
        PicoContainer pico = buildContainer(builder, null, "SOME_SCOPE");
        assertEquals(3, pico.getComponentInstances().size());
        assertNotNull(pico.getComponentInstance(DefaultWebServerConfig.class));
    }

    public void testAPicocontainerCanHostAChild() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-implementation class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "  <component-implementation class='java.lang.StringBuffer'/>" +
                "  <container>" +
                "    <component-implementation key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "  </container>" +
                "</container>");

        XMLContainerBuilder builder = new XMLContainerBuilder(script, getClass().getClassLoader());
        PicoContainer pico = buildContainer(builder, null, "SOME_SCOPE");
        assertNotNull(pico.getComponentInstance(DefaultWebServerConfig.class));

        StringBuffer sb = (StringBuffer) pico.getComponentInstance(StringBuffer.class);
        assertTrue(sb.indexOf("-WebServerImpl") != -1);
    }

    public void XXX_testClassLoaderHierarchy() throws ParserConfigurationException, ClassNotFoundException, SAXException, IOException, PicoCompositionException {
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
                "  <component-implementation key='foo' class='TestComp'/>" +
                "  <container>" +
                "    <classpath>" +
                "      <element file='" + testCompJar2.getCanonicalPath() + "'/>" +
                "      <element file='" + notStartableJar.getCanonicalPath() + "'/>" +
                "    </classpath>" +
                "    <component-implementation key='bar' class='TestComp2'/>" +
                "    <component-implementation key='phony' class='NotStartable'/>" +
                "  </container>" +
                "  <component-implementation class='java.lang.StringBuffer'/>" +
                "</container>");

        XMLContainerBuilder builder = new XMLContainerBuilder(script, getClass().getClassLoader());
        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(builder, null, "SOME_SCOPE");

        Object fooTestComp = pico.getComponentInstance("foo");
        assertNotNull("Container should have a 'foo' component", fooTestComp);

        StringBuffer sb = (StringBuffer) pico.getComponentInstance(StringBuffer.class);
        assertTrue("Container should have instantiated a 'TestComp2' component because it is Startable", sb.indexOf("-TestComp2") != -1);
        // We are using the DefaultLifecycleManager, which only instantiates Startable components, and not non-Startable components.
        assertTrue("Container should NOT have instantiated a 'NotStartable' component because it is NOT Startable", sb.indexOf("-NotStartable") == -1);
    }

    public void testUnknownclassThrowsPicoCompositionException() throws SAXException, ParserConfigurationException, IOException {
        try {
            Reader script = new StringReader("" +
                    "<container>" +
                    "  <component-implementation class='Foo'/>" +
                    "</container>");
            buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
            fail("Expected PicoCompositionException");
        } catch (PicoCompositionException e) {
            assertTrue("ClassNotFoundException", e.getCause() instanceof ClassNotFoundException);
        }
    }

    public void testEmptyScriptThrowsEmptyCompositionException() throws Exception, SAXException, ParserConfigurationException, IOException {
        try {
            Reader script = new StringReader("<container/>");
            buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
            fail("Expected EmptyCompositionException");
        } catch (EmptyCompositionException expected) {
        }
    }

    public void testComponentInstanceWithFactoryCanBeUsed() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-instance factory='org.nanocontainer.script.xml.XMLContainerBuilderTestCase$TestFactory'>" +
                "    <config-or-whatever/>" +
                "  </component-instance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        Object instance = pico.getComponentInstances().get(0);
        assertNotNull(instance);
        assertTrue(instance instanceof String);
        assertEquals("Hello", instance.toString());
    }

    public void testComponentInstanceWithDefaultFactory() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-instance>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </component-instance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        Object instance = pico.getComponentInstances().get(0);
        assertNotNull(instance);
        assertTrue(instance instanceof TestBean);
        assertEquals(10, ((TestBean)instance).getFoo());
        assertEquals("hello", ((TestBean)instance).getBar());
    }

    public void testComponentInstanceWithBeanFactory() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-instance factory='org.nanocontainer.script.xml.BeanComponentInstanceFactory'>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </component-instance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        Object instance = pico.getComponentInstances().get(0);
        assertNotNull(instance);
        assertTrue(instance instanceof TestBean);
        assertEquals(10, ((TestBean)instance).getFoo());
        assertEquals("hello", ((TestBean)instance).getBar());
    }
    
    public void testComponentInstanceWithKey() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-instance key='aKey'>" +
                "    <string>Ciao</string>" +
                "  </component-instance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        Object instance = pico.getComponentInstance("aKey");
        assertNotNull(instance);
        assertTrue(instance instanceof String);
        assertEquals("Ciao", instance.toString());
    }

    public void testComponentInstanceWithFactoryAndKey() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-instance factory='org.nanocontainer.script.xml.XMLContainerBuilderTestCase$TestFactory'" +
                "						key='aKey'>" +
                "    <config-or-whatever/>" +
                "  </component-instance>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        Object instance = pico.getComponentInstance("aKey");
        assertNotNull(instance);
        assertTrue(instance instanceof String);
        assertEquals("Hello", instance.toString());
    }
    
    public static class TestFactory implements XMLComponentInstanceFactory {
        public Object makeInstance(PicoContainer pico, Element elem) throws ClassNotFoundException {
            return "Hello";
        }
    }

    public void testInstantiationOfComponentsWithParams() throws IOException, SAXException, ClassNotFoundException, ParserConfigurationException {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-implementation class='org.nanocontainer.testmodel.WebServerConfigComp'>" +
                "    <parameter><string>localhost</string></parameter>" +
                "    <parameter><int>8080</int></parameter>" +
                "  </component-implementation>" +
                "  <component-implementation key='org.nanocontainer.testmodel.WebServer' class='org.nanocontainer.testmodel.WebServerImpl'/>" +
                "</container>");
        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        assertNotNull(pico.getComponentInstance(WebServerConfigComp.class));
        WebServerConfigComp config = (WebServerConfigComp) pico.getComponentInstanceOfType(WebServerConfigComp.class);
        assertEquals("localhost", config.getHost());
        assertEquals(8080, config.getPort());
    }
    
    public void testInstantiationOfComponentsWithParameterInstancesOfSameComponent() throws Exception {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-implementation class='org.nanocontainer.script.xml.TestBeanComposer'>" +
                " 		<parameter>" +
                "			<org.nanocontainer.script.xml.TestBean>" +
                "				<foo>10</foo>" +
                "				<bar>hello1</bar>" +
                "			</org.nanocontainer.script.xml.TestBean>" +
                "		</parameter>" +
                " 		<parameter>" +
                "			<org.nanocontainer.script.xml.TestBean>" +
                "				<foo>10</foo>" +
                "				<bar>hello2</bar>" +
                "			</org.nanocontainer.script.xml.TestBean>" +
                "		</parameter>" +
                "  </component-implementation>" +
                "</container>");
        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        assertNotNull(pico.getComponentInstance(TestBeanComposer.class));
        TestBeanComposer composer = (TestBeanComposer)pico.getComponentInstance(TestBeanComposer.class);
        assertEquals("bean1", "hello1", composer.getBean1().getBar());
        assertEquals("bean2", "hello2", composer.getBean2().getBar());
    }

    public void testInstantiationOfComponentsWithParameterInstancesOfSameComponentAndBeanFactory() throws Exception {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-implementation class='org.nanocontainer.script.xml.TestBeanComposer'>" +
                " 		<parameter factory='org.nanocontainer.script.xml.BeanComponentInstanceFactory'>" +
                "			<org.nanocontainer.script.xml.TestBean>" +
                "				<foo>10</foo>" +
                "				<bar>hello1</bar>" +
                "			</org.nanocontainer.script.xml.TestBean>" +
                "		</parameter>" +
                " 		<parameter factory='org.nanocontainer.script.xml.BeanComponentInstanceFactory'>" +
                "			<org.nanocontainer.script.xml.TestBean>" +
                "				<foo>10</foo>" +
                "				<bar>hello2</bar>" +
                "			</org.nanocontainer.script.xml.TestBean>" +
                "		</parameter>" +
                "  </component-implementation>" +
                "</container>");
        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        assertNotNull(pico.getComponentInstance(TestBeanComposer.class));
        TestBeanComposer composer = (TestBeanComposer)pico.getComponentInstance(TestBeanComposer.class);
        assertEquals("bean1", "hello1", composer.getBean1().getBar());
        assertEquals("bean2", "hello2", composer.getBean2().getBar());
    }
    
    public void testInstantiationOfComponentsWithParameterKeys() throws Exception {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-instance key='bean1'>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello1</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </component-instance>" +
                "  <component-instance key='bean2'>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello2</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </component-instance>" +
                "  <component-implementation class='org.nanocontainer.script.xml.TestBeanComposer'>" +
                " 		<parameter key='bean1'/>" +
                " 		<parameter key='bean2'/>" +
                "  </component-implementation>" +
                "</container>");
        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        assertNotNull(pico.getComponentInstance(TestBeanComposer.class));
        TestBeanComposer composer = (TestBeanComposer)pico.getComponentInstance(TestBeanComposer.class);
        assertEquals("bean1", "hello1", composer.getBean1().getBar());
        assertEquals("bean2", "hello2", composer.getBean2().getBar());
    }

    public void testInstantiationOfComponentsWithComponentAdapter() throws Exception {
        Reader script = new StringReader("" +
                "<container>" +
                "  <component-instance key='bean1'>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello1</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </component-instance>" +
                "  <component-instance key='bean2'>" +
                "	<org.nanocontainer.script.xml.TestBean>" +
                "		<foo>10</foo>" +
                "		<bar>hello2</bar>" +
                "	</org.nanocontainer.script.xml.TestBean>" +
                "  </component-instance>" +
                "  <component-adapter key='beanKey' class='org.nanocontainer.script.xml.TestBeanComposer'>" +
                " 		<parameter key='bean1'/>" +
                " 		<parameter key='bean2'/>" +
                "  </component-adapter>" +
                "</container>");
        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        assertNotNull(pico.getComponentInstance("beanKey"));
        TestBeanComposer composer = (TestBeanComposer)pico.getComponentInstance("beanKey");
        assertEquals("bean1", "hello1", composer.getBean1().getBar());
        assertEquals("bean2", "hello2", composer.getBean2().getBar());
    }
    
    // This is of little value given that nested adapters can't be specified in XML.
    public void testComponentAdapterClassCanBeSpecifiedInContainerElement() throws IOException, ParserConfigurationException, SAXException {
        Reader script = new StringReader("" +
                "<container component-adapter-factory='" + ConstructorInjectionComponentAdapterFactory.class.getName() + "'>" +
                "  <component-implementation class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
                "</container>");

        PicoContainer pico = buildContainer(new XMLContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        Object wsc1 = pico.getComponentInstanceOfType(WebServerConfig.class);
        Object wsc2 = pico.getComponentInstanceOfType(WebServerConfig.class);

        assertNotSame(wsc1, wsc2);
    }
}
