/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mike Hogan                                               *
 *****************************************************************************/


package nanocontainer;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import picocontainer.PicoRegistrationException;
import picocontainer.PicoContainer;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.defaults.NullContainer;
import picocontainer.defaults.PicoInvocationTargetInitializationException;
import nanocontainer.reflection.StringToObjectConverter;

public class ConfiguringNanoContainerImpl extends StringRegistrationNanoContainerImpl
        implements InputSourceRegistrationNanoContainer {
    private static final String ADD_METHOD_PREFIX = "add";
    private static final String SET_METHOD_PREFIX = "set";
    private final DocumentBuilder documentBuilder;
    private final Map implConfigurationMap;


    public ConfiguringNanoContainerImpl(DocumentBuilder documentBuilder, PicoContainer parentContainer, ClassLoader classLoader) {
        super(parentContainer, classLoader, new StringToObjectConverter());
        this.documentBuilder = documentBuilder;
        implConfigurationMap = new HashMap();
    }

    public static class Default extends ConfiguringNanoContainerImpl {
        public Default() throws ParserConfigurationException {
            super(DocumentBuilderFactory.newInstance().newDocumentBuilder(), new NullContainer(), ConfiguringNanoContainerImpl.class.getClassLoader());
        }
    }

    public void registerComponents(InputSource registration) throws PicoRegistrationException, ClassNotFoundException {
        try {
            Document doc = documentBuilder.parse(registration);
            NodeList components = doc.getElementsByTagName("component");
            for (int i = 0; i < components.getLength(); i++) {
                NamedNodeMap attributes = components.item(i).getAttributes();

                Node type = attributes.getNamedItem("type");
                Node clazz = attributes.getNamedItem("class");
                if (type != null) {
                    registerComponent(type.getNodeValue(), clazz.getNodeValue());
                } else {
                    registerComponent(clazz.getNodeValue());
                }
                final Collection configurationItems = new ArrayList();
                implConfigurationMap.put(clazz.getNodeValue(), configurationItems);
                recordConfigurationForImpl(configurationItems, components.item(i).getChildNodes());
            }
        } catch (SAXException e) {
            throw new NanoTextRegistrationException("SAXException:" + e.getMessage());
        } catch (IOException e) {
            throw new NanoTextRegistrationException("IOException:" + e.getMessage());
        } catch (PicoIntrospectionException e) {

        }
    }

    private void recordConfigurationForImpl(final Collection configurationItems, final NodeList configurationNodes) {
        for (int i = 0; i < configurationNodes.getLength(); i++) {
            final Node currentNode = configurationNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                if (isConfigurationNode(currentNode)) {
                    configurationItems.add(new ConfigurationItem(currentNode.getNodeName(), currentNode.getFirstChild().getNodeValue()));
                } else if (currentNode.hasChildNodes()) {
                    recordConfigurationForImpl(configurationItems, currentNode.getChildNodes());
                }
            }
        }
    }

    /**
     * Its a configuration node if it contains nothing more or less than a single child text node.
     */
    private boolean isConfigurationNode(Node node) {
        return node.hasChildNodes() &&
                node.getChildNodes().getLength() == 1 &&
                node.getFirstChild().getNodeType() == Node.TEXT_NODE;
    }

    public void instantiateComponents() throws PicoInstantiationException, PicoIntrospectionException {
        super.instantiateComponents();
        final Object[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            final Object configuree = components[i];
            final Collection configurationItems = (Collection) implConfigurationMap.get(configuree.getClass().getName());
            if (configurationItems != null) {
                try {
                    installConfiguration(configurationItems, configuree);
                } catch (InvocationTargetException e) {
                    throw new PicoInvocationTargetInitializationException(e);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * If there is an addXXX() method, call it with the configuration item.
     * Else try with a setXXX() method.
     * Else ignore.
     */
    private void installConfiguration(final Collection configurationItems, final Object configuree) throws InvocationTargetException, IllegalAccessException {
        final Map methodMap = getMethodMap(configuree);
        for (Iterator j = configurationItems.iterator(); j.hasNext();) {
            final ConfigurationItem configurationItem = (ConfigurationItem) j.next();
            Method writeMethod = (Method) methodMap.get(ADD_METHOD_PREFIX + uppercaseInitial(configurationItem.name));
            if (writeMethod != null) {
                writeMethod.invoke(configuree, new Object[]{getTypedValue(writeMethod.getParameterTypes()[0], configurationItem)});
            } else {
                writeMethod = (Method) methodMap.get(SET_METHOD_PREFIX + uppercaseInitial(configurationItem.name));
                if (writeMethod != null) {
                    writeMethod.invoke(configuree, new Object[]{getTypedValue(writeMethod.getParameterTypes()[0], configurationItem)});
                }
            }
        }
    }

    private Object getTypedValue(final Class type, final ConfigurationItem configurationItem) {
        Object result = null;
        if (type == String.class) {
            result = configurationItem.value;
        } else if (type == Integer.TYPE || type == Integer.class) {
            result = new Integer(configurationItem.value);
        } else {
            throw new IllegalArgumentException("Illegal argument " + type);
        }
        return result;
    }

    private String uppercaseInitial(final String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
    }

    /**
     * Return a map of method names to methods, for all methods that
     * begin in set or add and take one parameter only.  These are candidates
     * for configuration methods.
     */
    private Map getMethodMap(final Object configuree) {
        final Map result = new HashMap();
        final Method[] methods = configuree.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if ((method.getName().startsWith(SET_METHOD_PREFIX) || method.getName().startsWith(ADD_METHOD_PREFIX)) && method.getParameterTypes().length == 1) {
                result.put(method.getName(), method);
            }
        }
        return result;
    }

    private static final class ConfigurationItem {
        public String name = null;
        public String value = null;

        public ConfigurationItem(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}