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

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class XmlAssemblyNanoContainerTestCase extends TestCase {

    public void testInstantiateXml() throws Exception, SAXException, ParserConfigurationException, IOException {

        MockMonitor.monitorRecorder = "";

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

    public void testInstantiateXmlWithMissingComponent() throws Exception, SAXException, ParserConfigurationException, IOException {

        MockMonitor.monitorRecorder = "";

        try {
            NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                    "<container>" +
                    "      <component classname='Foo'/>" +
                    "</container>"), new MockMonitor());
            fail("Should have thrown a ClassNotFoundException");
        } catch (ClassNotFoundException cnfe) {
        }

    }

    public void testInstantiateEmptyXml() throws Exception, SAXException, ParserConfigurationException, IOException {

        MockMonitor.monitorRecorder = "";

        try {
            NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                    "<container>" +
                    "</container>"), new MockMonitor());
            fail("Should have thrown a EmptyNanoContainerException");
        } catch (EmptyNanoContainerException cnfe) {
        }

    }

}
