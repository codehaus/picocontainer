/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;
import org.picoextras.testmodel.WebServerConfig;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Ward Cunningham
 * @author Mike Royle
 * @version $Revision$
 */
public class JythonCompositionNanoContainerTestCase extends TestCase {

    protected void setUp() throws Exception {
        MockMonitor.monitorRecorder = "";
        MockMonitor.allComps = new ArrayList();
        Xxx.componentRecorder = "";
    }

    public void testInstantiateBasicRhinoScriptable() throws IOException, ClassNotFoundException, PicoCompositionException {

        NanoContainer nano = new JythonCompositionNanoContainer(new StringReader("" +
                "rootContainer.registerComponentImplementation('org.nanocontainer.Xxx$A')\n"
        ), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        assertEquals("Should match the expression", "<AA>!A", Xxx.componentRecorder);
    }

    public void testInstantiateWithChildContainer() throws IOException, ClassNotFoundException, PicoCompositionException {

        // A and C have no no dependancies. B Depends on A.

        NanoContainer nano = new JythonCompositionNanoContainer(new StringReader("" +
                "rootContainer.registerComponentImplementation('org.nanocontainer.Xxx$A')\n" +
                "childContainer = DefaultReflectionFrontEnd()\n" +
                "rootContainer.getPicoContainer().addChild(childContainer.getPicoContainer())\n" +
                "childContainer.registerComponentImplementation('org.nanocontainer.Xxx$B')\n" +
                "rootContainer.registerComponentImplementation('org.nanocontainer.Xxx$C')\n"
        ), new MockMonitor());
        nano.stopComponentsDepthFirst();
        nano.disposeComponentsDepthFirst();

        //TODO - this works differently under Maven to IDEA
        // needs attention!

        try {
            assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", Xxx.componentRecorder);
        } catch (ComparisonFailure e) {
            assertEquals("Should match the expression", "<C<A<BB>A>C>!B!A!C", Xxx.componentRecorder);
        }

        try {
            assertEquals("Should match the expression", "*A*B+A_started+B_started+B_stopped+A_stopped+B_disposed+A_disposed", MockMonitor.monitorRecorder);
        } catch (ComparisonFailure e) {
            assertEquals("Should match the expression", "*C*B+C_started+B_started+B_stopped+C_stopped+B_disposed+C_disposed", MockMonitor.monitorRecorder);
        }


    }

    public void testInstantiateWithImpossibleComponentDependanciesConsideringTheHierarchy() throws IOException, ClassNotFoundException, PicoCompositionException {

        // A and C have no no dependancies. B Depends on A.

        try {
            new JythonCompositionNanoContainer(new StringReader("" +
                    "rootContainer.registerComponentImplementation('org.nanocontainer.Xxx$B')\n" +
                    "childContainer = DefaultReflectionFrontEnd()\n" +
                    "rootContainer.getPicoContainer().addChild(childContainer.getPicoContainer())\n" +
                    "childContainer.registerComponentImplementation('org.nanocontainer.Xxx$A')\n" +
                    "rootContainer.registerComponentImplementation('org.nanocontainer.Xxx$C')\n"
            ), new MockMonitor());
            fail("Should not have been able to instansiate component tree due to visibility/parent reasons.");
        } catch (NoSatisfiableConstructorsException e) {
        }
    }

    public void testInstantiateWithInlineConfiguration() throws IOException, ClassNotFoundException, PicoCompositionException {

        NanoContainer nano = new JythonCompositionNanoContainer(new StringReader("" +
                "from org.nanocontainer.testmodel import WebServerConfigBean\n" +
                "wsc = WebServerConfigBean()\n" +
                "wsc.setHost('foobar.com')\n" +
                "wsc.setPort(4321)\n" +
                "rootContainer.getPicoContainer().registerComponentInstance(wsc)\n" +
                "rootContainer.registerComponentWithClassKey('org.picoextras.testmodel.WebServer','" + XmlCompositionNanoContainerTestCase.OverriddenWebServerImpl.class.getName() + "')\n"
        ), new MockMonitor());

        assertEquals("WebServerConfigBean and WebServerImpl expected", 2, nano.getRootContainer().getComponentInstances().size());
        WebServerConfig wsc = (WebServerConfig) nano.getRootContainer().getComponentInstance(WebServerConfig.class);
        assertEquals("foobar.com", wsc.getHost());
        assertEquals(4321, wsc.getPort());
    }

}
