/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.script.xml;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Paul Hammant
 * @author Marcos Tarruella
 */
public class zBeanXMLPseudoComponentFactoryTestCase extends TestCase {

    public void testDeserialization() throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException {
        BeanXMLPseudoComponentFactory xsf = new BeanXMLPseudoComponentFactory();

        StringReader sr = new StringReader("" +
                "<org.picoextras.script.xml.TestPseudoComp>" +
                "<foo>10</foo>" +
                "<bar>hello</bar>" +
                "</org.picoextras.script.xml.TestPseudoComp>"
        );
        InputSource is = new InputSource(sr);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(is);

        Object o = xsf.makeInstance(doc.getDocumentElement());
        TestPseudoComp tsc = (TestPseudoComp) o;
        assertEquals("hello", tsc.getBar());
        assertEquals(10, tsc.getFoo());
    }
}
