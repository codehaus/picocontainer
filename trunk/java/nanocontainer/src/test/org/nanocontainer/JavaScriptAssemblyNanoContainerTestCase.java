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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @version $Revision$
 */
public class JavaScriptAssemblyNanoContainerTestCase extends TestCase {

    protected void setUp() throws Exception {
        MockMonitor.monitorRecorder = "";
        MockMonitor.allComps = new ArrayList();
    }

    public void testFoo() {
    }

    //TODO nearly works !
    public void donot_testInstantiateXml() throws IOException, ClassNotFoundException, PicoConfigurationException {

        // A and C have no no dependancies. B Depends on A.

        NanoContainer nano = new JavaScriptAssemblyNanoContainer(new StringReader("" +
                "fe = new RhinoFrontEnd();" +
                "  fe.addComponent('org.nanocontainer.Xxx$A');" +
                "  fe2 = fe.subContainer();" +
                "    fe2.addComponent('org.nanocontainer.Xxx$B');" +
                "  fe.addComponent('org.nanocontainer.Xxx$C');" +
                "/* TODO get root container out ? here or inside RhinoFrontEnd? */"
                ), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", Xxx.componentRecorder);
        assertEquals("Should match the expression", "*A*B+A_started+B_started+B_stopped+A_stopped+B_disposed+A_disposed", MockMonitor.monitorRecorder);
    }

}
