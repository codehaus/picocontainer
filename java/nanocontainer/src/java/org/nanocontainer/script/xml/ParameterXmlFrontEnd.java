/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jeppe Cramon                                             *
 *****************************************************************************/

package org.picoextras.script.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.picoextras.reflection.ReflectionFrontEnd;
import org.picoextras.reflection.DefaultReflectionFrontEnd;
import org.picoextras.script.xml.EmptyCompositionException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picoextras.script.PicoCompositionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Handles XML configuration files which includes specification of container implementation (default is DefaultPicoContainer) + plus constant 
 * parameters (including primitive types) for components.
 * Requirement for Containers is that they have a default constructor which initializes it with the necessary
 * requirements.<br>
 * <br>
 * Sample XML document:
 * <pre>
 * &lt;container classname="org.picoextras.jmx.NanoMXContainer"&gt;
 * 		&lt;component key="testClass" classname="org.test.TestClass"&gt;
 * 			&lt;parameter classname="java.lang.String"&gt;Test&lt;/parameter&gt;
 * 			&lt;parameter classname="int"&gt;23&lt;/parameter&gt;
 *  	   &lt;/component&gt;
 * 		&lt;container&gt;
 * 			&lt;component classname="org.test.TestClass2"/&gt;
 * 			&lt;component key="class23" classname="org.test.TestClass3"/&gt;
 * 		&lt;/container&gt;
 * &lt;/container&gt;
 * </pre>
 * @author Jeppe Cramon
 */
public class ParameterXmlFrontEnd implements XmlFrontEnd {
	private int componentCount = 0;

	/**
	 * 
	 */
	public ParameterXmlFrontEnd() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.picoextras.script.xml.XmlFrontEnd#createPicoContainer(org.w3c.dom.Element)
	 */
	public PicoContainer createPicoContainer(Element rootElement)
		throws
			IOException,
			SAXException,
			ClassNotFoundException,
			XmlFrontEndException {
		componentCount = 0;
		PicoContainer container = registerContainer(null, rootElement);
		if (componentCount == 0) {
			throw new EmptyCompositionException();
		}
		return container;
	}

    public PicoContainer createPicoContainer(Element rootElement, MutablePicoContainer mutablePicoContainer) throws IOException, SAXException, ClassNotFoundException, PicoCompositionException {
        // ignore mutablePicoContainer param for now.
        return createPicoContainer(rootElement);
    }

	/**
	 * Accepts elements:<br>
	 * &lt;container classname=""/&gt; where classname is optional. If left out the type of container is the DefaultPicoContainer
	 * @param element
	 * @return
	 */
	private PicoContainer registerContainer(ReflectionFrontEnd parent, Element element) throws XmlFrontEndException {
		if (!element.getNodeName().equals("container")) {
			throw new XmlFrontEndException("Excepted 'container' element, but found '" +  element.getNodeName() + "'");
		}
		// Check which type of container to register
		String containerClass = DefaultPicoContainer.class.getName();
		if (element.getAttribute("classname") != null && element.getAttribute("classname").length() != 0) {
			containerClass = element.getAttribute("classname"); 	
		}

		try {
			MutablePicoContainer container = (MutablePicoContainer) Class.forName(containerClass).newInstance();
			ReflectionFrontEnd frontEnd = new DefaultReflectionFrontEnd(container);
			if (parent != null) {
				MutablePicoContainer parentContainer = (MutablePicoContainer)parent.getPicoContainer();
				container.addParent(parentContainer);
			}
			// Loop through components and child containers
			NodeList elemList = element.getChildNodes();
			for (int index = 0; index < elemList.getLength(); index++) {
				if(elemList.item(index).getNodeType() == Document.ELEMENT_NODE) {
					registerComponentsAndChildContainers(frontEnd, (Element)elemList.item(index));
				}
			}
			return container;		
		} catch (InstantiationException e) {
            throw MakeXmlFrontEndException(containerClass, e);
		} catch (IllegalAccessException e) {
            throw MakeXmlFrontEndException(containerClass, e);
		} catch (ClassNotFoundException e) {
			throw MakeXmlFrontEndException(containerClass, e);
		}
	}

    private XmlFrontEndException MakeXmlFrontEndException(final String containerClass, final Exception e) {
        return new XmlFrontEndException("Failed to create an instance of container '" + containerClass + "', due to " + e.getMessage(), e);
    }

	/**
	 * @param frontEnd
	 * @param element
	 */
	private void registerComponentsAndChildContainers(ReflectionFrontEnd frontEnd, Element element) throws ClassNotFoundException, XmlFrontEndException {
		if (element.getNodeName().equals("container")) {
			registerContainer(frontEnd, element);
		} else if (element.getNodeName().equals("component")) {
			registerComponent(frontEnd, element);
		} else {
			throw new XmlFrontEndException("Unknown element type: '" + element.getNodeName() + "'");
		}
	}

	/**
	 * Accepts elements of the following type:<br>
	 * &lt;component key="" classname=""/&gt; where key is optional. If key is left out the key value default to the classname<br>
	 * It also accepts parameter childelements :<br>
	 * &lt;parameter classname=""&gt;[Value]&lt;/parameter&gt;<br>
	 * Full classname for type is needed, but shorthands for most primitives and wrapper classes exists. 
	 *  
	 * @param pico
	 * @param element
	 */
	private void registerComponent(ReflectionFrontEnd pico, Element element) throws ClassNotFoundException, XmlFrontEndException {
		String className = element.getAttribute("classname");
		String key = element.getAttribute("key");
		if(key == null || key.equals("")) {
			key = className;
		}
		// Check for initialization parameters
		if (element.getChildNodes().getLength() == 0) {
			pico.registerComponentImplementation(key, className);
		} else {
			List parameterTypes = new ArrayList();
			List parameterValues = new ArrayList();
			NodeList elemList = element.getChildNodes();
			int parameterIndex = 0;
			for (int index = 0; index < elemList.getLength(); index++) {
				if(elemList.item(index).getNodeType() == Document.ELEMENT_NODE) {
					Element paramElement = (Element) elemList.item(index);
					if (paramElement.getNodeName().equals("parameter")) {
						parameterTypes.add(handleShortHands(paramElement.getAttribute("classname")));
						if (paramElement.hasChildNodes() && paramElement.getChildNodes().item(0).getNodeType() == Document.TEXT_NODE) {
							parameterValues.add(paramElement.getChildNodes().item(0).getNodeValue());
						} else {
							throw new XmlFrontEndException("No value specified for parameter " + (String)parameterTypes.get(parameterIndex) + " at order " + parameterIndex + 1);
						}
						parameterIndex++;
					} else {
						throw new XmlFrontEndException("Unkown component parameter element '" + paramElement.getNodeName() + "'");
					}
				}
			}
			pico.registerComponentImplementation(key, className, (String[])parameterTypes.toArray(new String[parameterTypes.size()]), (String[])parameterValues.toArray(new String[parameterValues.size()]));
		}
		componentCount++;
	}

	/**
	 * Handles shorthands for most of the simple type wrappers (translates from a shorthand for to the full
	 * form, e.g. from "int" to "java.lang.Integer" og "Short" to "java.lang.Short"
	 * @param name Shorthand
	 * @return The translated shorthand or the parameter value in case no shorthand was found
	 */
	private String handleShortHands(String name) {
		if (name.equals("String")) {
			return "java.lang.String";
        } else if (name.equals("")) {
            return "java.lang.String";
		} else if (name.equals("Integer")) {
			return "java.lang.Integer";
		} else if (name.equals("Float")) {
			return "java.lang.Float";
		} else if (name.equals("Double")) {
			return "java.lang.Double";
		} else if (name.equals("Boolean")) {
			return "java.lang.Boolean";
		} else if (name.equals("Short")) {
			return "java.lang.Short";
		} else if (name.equals("int")) {
			return "java.lang.Integer";
		} else if (name.equals("long")) {
			return "java.lang.Long";
		} else if (name.equals("float")) {
			return "java.lang.Float";
		} else if (name.equals("double")) {
			return "java.lang.Double";
		} else if (name.equals("boolean")) {
			return "java.lang.Boolean";
		} else if (name.equals("short")) {
			return "java.lang.Short";
		} else {
			return name;
		}
	}

}
