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
 * @author Daniel Bodart
 * @version $Revision$
 */
public class GroovyAssemblyNanoContainerTestCase extends TestCase {

    public void testInstantiateGroovy() throws Exception, SAXException, ParserConfigurationException, IOException {

        MockMonitor.monitorRecorder = "";

//        NanoContainer nano = new GroovyAssemblyNanoContainer(new StringReader("" +
//                "class Dummy {\n" +
//                "   void container ( \n" +
//                "      component( 'org.nanocontainer.Xxx$A' )\n" +
//                "      container( { |\n" +
//                "         component( 'org.nanocontainer.Xxx$C' )\n" +
//                "      })\n" +
//                "      component( 'org.nanocontainer.Xxx$B' )\n" +
//                "   )\n" +
//                "}\n"),
//                new MockMonitor());
//        nano.stopComponentsDepthFirst();
//        nano.disposeComponentsDepthFirst();
//
//        assertEquals("Should match the expression", "<A<B<CC>B>A>!C!B!A", Xxx.componentRecorder);
//        assertEquals("Should match the expression", "*A*C+A_started+C_started+C_stopped+A_stopped+C_disposed+A_disposed", MockMonitor.monitorRecorder);
    }
}
