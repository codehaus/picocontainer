/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import junit.framework.TestCase;
import org.picocontainer.PicoConfigurationException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;
import org.picocontainer.extras.DefaultLifecyclePicoContainer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class XmlAssemblyNanoContainerTestCase extends TestCase {

    protected void setUp() throws Exception {
        MockMonitor.monitorRecorder = "";
        MockMonitor.allComps = new ArrayList();
    }

    public void testInstantiateXml() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoConfigurationException {

        // A and C have no no dependancies. B Depends on A.

        NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                "<container>" +
                "      <component classname='org.nanocontainer.Xxx$A'/>" +
                "      <container>" +
                "          <component classname='org.nanocontainer.Xxx$B'/>" +
                "      </container>" +
                "      <component classname='org.nanocontainer.Xxx$C'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", Xxx.componentRecorder);
        assertEquals("Should match the expression", "*A*B+A_started+B_started+B_stopped+A_stopped+B_disposed+A_disposed", MockMonitor.monitorRecorder);
    }

    public void testInstantiateXmlWithImpossibleComponentDependanciesConsideringTheHierarchy()
            throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoConfigurationException {

        // A and C have no no dependancies. B Depends on A.

        try {
            new XmlAssemblyNanoContainer(new StringReader("" +
                        "<container>" +
                        "      <container>" +
                        "          <component classname='org.nanocontainer.Xxx$A'/>" +
                        "      </container>" +
                        "      <component classname='org.nanocontainer.Xxx$B'/>" +
                        "      <component classname='org.nanocontainer.Xxx$C'/>" +
                        "</container>"), new MockMonitor());
            fail("Should not have been able to instansiate component tree due to visibility/parent reasons.");
        } catch (NoSatisfiableConstructorsException e) {
            //expected
        }
    }


    public void testInstantiateWithBespokeXmlFrontEnd() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoConfigurationException {

        BespokeXmlFrontEnd.used = false;

        NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                "<container xmlfrontend='org.nanocontainer.BespokeXmlFrontEnd'>" +
                "      <component classname='org.nanocontainer.Xxx$A'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertTrue("Bespoke Front End (a test class) should have been used",BespokeXmlFrontEnd.used);
    }

    public void testInstantiateWithBogusXmlFrontEnd() throws SAXException, ParserConfigurationException, IOException, PicoConfigurationException {

        try {
            new XmlAssemblyNanoContainer(new StringReader("" +
                    "<container xmlfrontend='YeeeeeHaaaaa'>" +
                    "      <component classname='org.nanocontainer.Xxx$A'/>" +
                    "</container>"), new MockMonitor());
            fail("Should have barfed with ClassNotFoundException");
        } catch (ClassNotFoundException e) {
        }

    }

    public void testInstantiateWithBespokeContainer() throws SAXException, ParserConfigurationException, IOException, ClassNotFoundException, PicoConfigurationException {

        OverriddenDefaultLifecyclePicoContainer.used = false;

        NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                "<container container='"+OverriddenDefaultLifecyclePicoContainer.class.getName()+"'>" +
                "      <component classname='org.nanocontainer.Xxx$A'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertTrue("Bespoke Container (a test class) should have been used",OverriddenDefaultLifecyclePicoContainer.used);
        assertEquals("Should match the expression", "*A+A_started+A_stopped+A_disposed", MockMonitor.monitorRecorder);

    }

    public void testInstantiateWithBogusContainer() throws SAXException, ParserConfigurationException, IOException, PicoConfigurationException {

        try {
            new XmlAssemblyNanoContainer(new StringReader("" +
                    "<container container='YeeeHaaaaa'>" +
                    "      <component classname='org.nanocontainer.Xxx$A'/>" +
                    "</container>"), new MockMonitor());
            fail("Should have barfed with ClassNotFoundException");
        } catch (ClassNotFoundException e) {
        }

    }


    public static class OverriddenDefaultLifecyclePicoContainer extends DefaultLifecyclePicoContainer {

        public static boolean used;

        public OverriddenDefaultLifecyclePicoContainer() {
            used = true;
        }
    }


}
