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

import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ImplementationHidingComponentAdapterFactory;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.nanocontainer.script.AbstractScriptedComposingLifecycleContainerBuilderTestCase;


import org.picoextras.testmodel.DefaultWebServerConfig;
import org.picoextras.testmodel.ThingThatTakesParamsInConstructor;
import org.picoextras.testmodel.WebServer;
import org.picoextras.testmodel.WebServerConfig;
import org.picoextras.testmodel.WebServerConfigComp;
import org.picoextras.testmodel.WebServerImpl;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;
/**
 * test case for container creation off xml via xstream
 */
public class XStreamContainerBuilderTestCase extends AbstractScriptedComposingLifecycleContainerBuilderTestCase {
	
	
	public void testContainerBuilding() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoAssemblyException {
        Reader script = new StringReader("<container>" +
                "    <instance key='foo'>" +
                "    	<string>foo bar</string>" +
                "    </instance>" +
                "    <instance key='bar'>" +
                "    	<int>239</int>" +
                "    </instance>" +
                "    <instance>" +
                "    	<org.picoextras.testmodel.DefaultWebServerConfig>" +
				" 			<port>555</port>" + 
                "    	</org.picoextras.testmodel.DefaultWebServerConfig>" +
                "    </instance>" +
				"	 <implementation class='org.picoextras.testmodel.WebServerImpl'>" + 
				"		<dependency class='org.picoextras.testmodel.DefaultWebServerConfig'/>" + 
				"	 </implementation>" + 
				"	 <implementation key='konstantin needs beer' class='org.picoextras.testmodel.ThingThatTakesParamsInConstructor'>" + 
				"		<constant>" + 
				"			<string>it's really late</string>" + 
				"		</constant>" + 
				"		<constant>" + 
				"			<int>239</int>" + 
				"		</constant>" + 
				"	 </implementation>" + 
                "</container>");

        PicoContainer pico = buildContainer(new XStreamContainerBuilder(script, getClass().getClassLoader()));
        assertEquals(5, pico.getComponentInstances().size());
        assertEquals("foo bar",pico.getComponentInstance("foo"));
        assertEquals(new Integer(239),pico.getComponentInstance("bar"));
        assertEquals(555,((DefaultWebServerConfig)pico.getComponentInstance(DefaultWebServerConfig.class)).getPort());
		
		assertNotNull(pico.getComponentInstanceOfType(WebServerImpl.class));
		assertNotNull(pico.getComponentInstanceOfType(ThingThatTakesParamsInConstructor.class));
		assertSame(pico.getComponentInstance("konstantin needs beer"),pico.getComponentInstanceOfType(ThingThatTakesParamsInConstructor.class));
		assertEquals("it's really late239",((ThingThatTakesParamsInConstructor)pico.getComponentInstance("konstantin needs beer")).getValue());
	}
	
	
}

