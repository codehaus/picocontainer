/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.picoextras.testmodel.DefaultWebServerConfig;
import org.picoextras.testmodel.WebServerConfig;
import org.picoextras.testmodel.WebServerImpl;
import org.picoextras.script.PicoCompositionException;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;
import org.picocontainer.extras.DefaultLifecyclePicoContainer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class XmlCompositionNanoContainerTestCase extends TestCase {

    protected void setUp() throws Exception {
        MockMonitor.monitorRecorder = "";
        MockMonitor.allComps = new ArrayList();
    }

    public void testInstantiateBasicTree() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoCompositionException {

        // A and C have no no dependancies. B Depends on A.

        NanoContainer nano = new XmlCompositionNanoContainer(new StringReader("" +
                "<container>" +
                "      <component class='org.nanocontainer.Xxx$A'/>" +
                "      <container>" +
                "          <component class='org.nanocontainer.Xxx$B'/>" +
                "      </container>" +
                "      <component class='org.nanocontainer.Xxx$C'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", Xxx.componentRecorder);
        assertEquals("Should match the expression", "*A*B+A_started+B_started+B_stopped+A_stopped+B_disposed+A_disposed", MockMonitor.monitorRecorder);
    }

    public void testInstantiateXmlWithImpossibleComponentDependanciesConsideringTheHierarchy()
            throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoCompositionException {

        // A and C have no no dependancies. B Depends on A.

        try {
            new XmlCompositionNanoContainer(new StringReader("" +
                        "<container>" +
                        "      <container>" +
                        "          <component class='org.nanocontainer.Xxx$A'/>" +
                        "      </container>" +
                        "      <component class='org.nanocontainer.Xxx$B'/>" +
                        "      <component class='org.nanocontainer.Xxx$C'/>" +
                        "</container>"), new MockMonitor());
            fail("Should not have been able to instansiate component tree due to visibility/parent reasons.");
        } catch (NoSatisfiableConstructorsException e) {
            //expected
        }
    }


    public void testInstantiateWithBespokeXmlFrontEnd() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoCompositionException {

        BespokeXmlFrontEnd.used = false;

        NanoContainer nano = new XmlCompositionNanoContainer(new StringReader("" +
                "<container xmlfrontend='org.nanocontainer.BespokeXmlFrontEnd'>" +
                "      <component class='org.nanocontainer.Xxx$A'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertTrue("Bespoke Front End (a test class) should have been used",BespokeXmlFrontEnd.used);
    }

    public void testInstantiateWithBespokeComponentAdaptor() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoCompositionException {

        OverriddenComponentAdapterFactory.used = false;

        NanoContainer nano = null;
            nano = new XmlCompositionNanoContainer(new StringReader("" +
                        "<container componentadaptor='" + OverriddenComponentAdapterFactory.class.getName() + "'>" +
                        "    <component typekey='org.picoextras.testmodel.WebServerConfig' class='org.picoextras.testmodel.DefaultWebServerConfig'/>" +
                        "</container>"), new MockMonitor());

        Object wsc = nano.getRootContainer().getComponentInstance(WebServerConfig.class);

        assertTrue(wsc instanceof WebServerConfig);
        assertTrue(OverriddenComponentAdapterFactory.used);

    }

    public static class OverriddenComponentAdapterFactory extends DefaultComponentAdapterFactory {
        public static boolean used = false;
        public OverriddenComponentAdapterFactory() {
            used = true;
        }
    }

    public void testInstantiateWithXStreamComponentConfiguration() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoCompositionException {

        NanoContainer nano = null;
            nano = new XmlCompositionNanoContainer(new StringReader("" +
                        "<container>" +
                        "    <pseudocomponent factory='org.picoextras.script.xml.XStreamXmlPseudoComponentFactory'>" +
                        "       <org.picoextras.testmodel.WebServerConfigStub>" +
                        "         <host>foobar.com</host> " +
                        "         <port>4321</port> " +
                        "       </org.picoextras.testmodel.WebServerConfigStub>" +
                        "    </pseudocomponent>" +
                        "    <component typekey='org.picoextras.testmodel.WebServer' " +
                        "               class='org.picoextras.testmodel.WebServerImpl'/>" +
                        "</container>"), new MockMonitor());

        assertEquals("WebServerConfigBean and WebServerImpl expected", 2, nano.getRootContainer().getComponentInstances().size());
        WebServerConfig wsc = (WebServerConfig) nano.getRootContainer().getComponentInstance(WebServerConfig.class);
        assertEquals("foobar.com",wsc.getHost());
        assertEquals(4321,wsc.getPort());
    }

    public void testInstantiateWithBeanComponentConfiguration() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoCompositionException {

        NanoContainer nano = null;
            nano = new XmlCompositionNanoContainer(new StringReader("" +
                        "<container>" +
                        "    <pseudocomponent factory='org.picoextras.script.xml.BeanXmlPseudoComponentFactory'>" +
                        "       <org.picoextras.testmodel.WebServerConfigBean>" +
                        "         <host>foobar.com</host> " +
                        "         <port>4321</port> " +
                        "       </org.picoextras.testmodel.WebServerConfigBean>" +
                        "    </pseudocomponent>" +
                        "    <component typekey='org.picoextras.testmodel.WebServer' " +
                        "               class='org.picoextras.testmodel.WebServerImpl'/>" +
                        "</container>"), new MockMonitor());

        assertEquals("WebServerConfigBean and WebServerImpl expected", 2, nano.getRootContainer().getComponentInstances().size());
        WebServerConfig wsc = (WebServerConfig) nano.getRootContainer().getComponentInstance(WebServerConfig.class);
        assertEquals("foobar.com",wsc.getHost());
        assertEquals(4321,wsc.getPort());
    }

    public void testInstantiateWithBogusXmlFrontEnd() throws SAXException, ParserConfigurationException, IOException, PicoCompositionException {

        try {
            new XmlCompositionNanoContainer(new StringReader("" +
                    "<container xmlfrontend='YeeeeeHaaaaa'>" +
                    "      <component classname='org.nanocontainer.Xxx$A'/>" +
                    "</container>"), new MockMonitor());
            fail("Should have barfed with PicoCompositionException");
        } catch (PicoCompositionException e) {
        }

    }

    public void testInstantiateWithBespokeContainer() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoCompositionException {

        OverriddenDefaultLifecyclePicoContainer.used = false;

        NanoContainer nano = new XmlCompositionNanoContainer(new StringReader("" +
                "<container container='"+OverriddenDefaultLifecyclePicoContainer.class.getName()+"'>" +
                "      <component class='org.nanocontainer.Xxx$A'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertTrue("Bespoke Container (a test class) should have been used",OverriddenDefaultLifecyclePicoContainer.used);
        assertEquals("Should match the expression", "*A+A_started+A_stopped+A_disposed", MockMonitor.monitorRecorder);

    }

    public void testInstantiateWithBogusContainer() throws SAXException, ParserConfigurationException, IOException, PicoCompositionException {

        try {
            new XmlCompositionNanoContainer(new StringReader("" +
                    "<container container='YeeeHaaaaa'>" +
                    "      <component classname='org.nanocontainer.Xxx$A'/>" +
                    "</container>"), new MockMonitor());
            fail("Should have barfed with PicoCompositionException");
        } catch (PicoCompositionException e) {
        }

    }

    public static class OverriddenDefaultLifecyclePicoContainer extends DefaultLifecyclePicoContainer {

        public static boolean used;

        public OverriddenDefaultLifecyclePicoContainer() {
            used = true;
        }
    }

    public static class OverriddenWebServerImpl extends WebServerImpl {
        public OverriddenWebServerImpl(WebServerConfig wsc) {
            super(wsc);
            Assert.assertTrue(wsc instanceof WebServerConfig);
            Assert.assertFalse(wsc instanceof DefaultWebServerConfig);
        }
    }

    public void testInstantiateWithHintedComponentResolution() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoCompositionException {

        NanoContainer nano = null;
        try {
            nano = new XmlCompositionNanoContainer(new StringReader("" +
                        "<container>" +
                        "    <component stringkey='one' class='java.util.ArrayList'/>" +
                        "    <component stringkey='two' class='java.util.Vector'/>" +
                        "    <component class='"+CollectionNeedingComponent.class.getName()+"'>" +
                        "      <hint stringkey='one'/>" +
                        "    </component>" +
                        "</container>"), new MockMonitor());
        } catch (AmbiguousComponentResolutionException e) {
            //TODO - This should work not barf.
        }

    }

    public static class CollectionNeedingComponent {
        public CollectionNeedingComponent(Collection col) {
            if (col == null) {
                throw new NullPointerException();
            }
        }
    }


}
