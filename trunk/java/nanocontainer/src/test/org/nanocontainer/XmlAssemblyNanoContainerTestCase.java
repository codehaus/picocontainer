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
import java.util.ArrayList;
import java.util.List;

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

    public void testInstantiateXmlWithMissingComponent() throws Exception, SAXException, ParserConfigurationException, IOException {

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

        try {
            NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader("" +
                    "<container>" +
                    "</container>"), new MockMonitor());
            fail("Should have thrown a EmptyNanoContainerException");
        } catch (EmptyNanoContainerException cnfe) {
        }
    }

    public void testInstantiateWithBespokeClassLoader() throws Exception, SAXException, ParserConfigurationException, IOException {

        StringBuffer xml = new StringBuffer(
                "<container>" +
                "      <component classname='org.nanocontainer.Xxx$A'/>" +
                "      <container>" +
                "          <component classname='TestComp'/>" +
                "          <jarfile location='*test-comp/TestComp.jar'/>" +
                "          <!-- yet to implement " +
                "            <jarurl location='http://foobar.com/TestComp.jar'/>" +
                "          -->" +
                "      </container>" +
                "</container>");

        //TODO - need to make this adaptive. I.e. can run in IDEA, under Maven etc.
        // Tis currently hard coded to my (Paul) file system
        // aslak has some magic code...
        xml.replace(xml.indexOf("*"), xml.indexOf("*")+ 1, "d:/dev/nano/nanocontainer/");

        NanoContainer nano = new XmlAssemblyNanoContainer(new StringReader(xml.toString()), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        List comps = MockMonitor.allComps;
        assertEquals("There should be two components", comps.size(), 2);

        Class XxxAClass = comps.get(0).getClass();
        Class testCompClass = comps.get(1).getClass();

        assertFalse("Components should be in different classloaders",
                XxxAClass.getClassLoader() == testCompClass.getClassLoader());

        assertTrue("The parent classloader of 'TestComp' should be that of 'Xxx$A'",
                XxxAClass.getClassLoader() == testCompClass.getClassLoader().getParent());

    }

}
