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
import org.xml.sax.SAXException;
import org.picocontainer.extras.DefaultLifecyclePicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoConfigurationException;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;

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

    public void testInstantiateXml() throws Exception, SAXException, ParserConfigurationException, IOException {

        NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                "<container>" +
                "      <component classname='org.nanocontainer.Xxx$A'/>" +
                "      <container>" +
                "          <component classname='org.nanocontainer.Xxx$C'/>" +
                "      </container>" +
                "      <component classname='org.nanocontainer.Xxx$B'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("Should match the expression", "<A<B<CC>B>A>!C!B!A", Xxx.componentRecorder);
        assertEquals("Should match the expression", "*A*C+A_started+C_started+C_stopped+A_stopped+C_disposed+A_disposed", MockMonitor.monitorRecorder);
    }

    public void testInstantiateWithBespokeXmlFrontEnd() throws Exception, SAXException, ParserConfigurationException, IOException {

        BespokeXmlFrontEnd.used = false;

        NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                "<container xmlfrontend='org.nanocontainer.BespokeXmlFrontEnd'>" +
                "      <component classname='org.nanocontainer.Xxx$A'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertTrue("Bespoke Front End (a test class) should have been used",BespokeXmlFrontEnd.used);
    }

    public void testInstantiateWithBogusXmlFrontEnd() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                    "<container xmlfrontend='YeeeeeHaaaaa'>" +
                    "      <component classname='org.nanocontainer.Xxx$A'/>" +
                    "</container>"), new MockMonitor());
            fail("Should have barfed with ClassNotFoundException");
        } catch (ClassNotFoundException e) {
        }

    }

    public void testInstantiateWithBespokeContainer() throws Exception, SAXException, ParserConfigurationException, IOException {

        OverriddenDefaultLifecyclePicoContainer.used = false;

        NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                "<container container='"+OverriddenDefaultLifecyclePicoContainer.class.getName()+"'>" +
                "      <component classname='org.nanocontainer.Xxx$A'/>" +
                "</container>"), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertTrue("Bespoke Container (a test class) should have been used",OverriddenDefaultLifecyclePicoContainer.used);
    }

    public void testInstantiateWithBogusContainer() throws Exception, SAXException, ParserConfigurationException, IOException {

        try {
            NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
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
