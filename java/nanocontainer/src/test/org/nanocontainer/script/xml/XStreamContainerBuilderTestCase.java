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

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor;
import org.nanocontainer.testmodel.WebServerImpl;
import org.picocontainer.PicoContainer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class XStreamContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

	public void testContainerBuilding() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader( "" +
                "<container>" +
                "    <instance key='foo'>" +
                "    	<string>foo bar</string>" +
                "    </instance>" +
                "    <instance key='bar'>" +
                "    	<int>239</int>" +
                "    </instance>" +
                "    <instance>" +
                "    	<org.nanocontainer.testmodel.DefaultWebServerConfig>" +
				" 			<port>555</port>" +
                "    	</org.nanocontainer.testmodel.DefaultWebServerConfig>" +
                "    </instance>" +
				"	 <implementation class='org.nanocontainer.testmodel.WebServerImpl'>" +
				"		<dependency class='org.nanocontainer.testmodel.DefaultWebServerConfig'/>" +
				"	 </implementation>" +
				"	 <implementation key='konstantin needs beer' class='org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor'>" +
				"		<constant>" +
				"			<string>it's really late</string>" +
				"		</constant>" +
				"		<constant>" +
				"			<int>239</int>" +
				"		</constant>" +
				"	 </implementation>" +
                "</container>");

        PicoContainer pico = buildContainer(new XStreamContainerBuilder(script, getClass().getClassLoader()), null);
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

