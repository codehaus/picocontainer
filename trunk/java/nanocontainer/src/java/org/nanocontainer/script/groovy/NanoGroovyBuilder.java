/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.nanocontainer.script.groovy;

import groovy.util.BuilderSupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.nanocontainer.reflection.ReflectionContainerAdapter;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Builds trees of PicoContainers and Pico components using GroovyMarkup
 *
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @author Paul Hammant
 * @version $Revision$
 */
public class NanoGroovyBuilder extends BuilderSupport {

    public NanoGroovyBuilder() {
    }

    protected void setParent(Object parent, Object child) {
    }

    protected Object createNode(Object name) {
        return createNode(name, Collections.EMPTY_MAP);
    }

    protected Object createNode(Object name, Object value) {
        if (value instanceof Class) {
            Map attributes = new HashMap();
            attributes.put("class", value);
            return createNode(name, attributes);
        }
        return createNode(name);
    }

    protected Object createNode(Object name, Map attributes) {
        Object parent = getCurrent();
        if (name.equals("container")) {
            return createContainerNode(parent, attributes);
        } else if (parent instanceof MutablePicoContainer) {
            return createChildOfContainerNode(parent, name, attributes);
        }
        throw new PicoBuilderException("Uknown method: '" + name + "'");
    }

    private Object createChildOfContainerNode(Object parent, Object name, Map attributes) {
        SoftCompositionPicoContainer parentContainer = (SoftCompositionPicoContainer) parent;
        if (name.equals("component")) {
            return createComponentNode(attributes, parentContainer, name);
        } else if (name.equals("bean")) {
            return createBeanNode(attributes, parentContainer);
        } else if (name.equals("classpathelement")) {
            return createClassPathElementNode(attributes, parentContainer);

        }
        throw new PicoBuilderException("Method: '" + name + "' must be a child of a container element");
    }

    private Object createContainerNode(Object parent, Map attributes) {
        PicoContainer parentContainer = null;
        if (parent instanceof MutablePicoContainer) {
            parentContainer = (PicoContainer) parent;
        }
        MutablePicoContainer answer = createContainer(attributes, parentContainer);
        return answer;
    }

    private Object createClassPathElementNode(Map attributes, ReflectionContainerAdapter reflectionContainerAdapter) {

        String path = (String) attributes.remove("path");
        URL pathURL = null;
        try {
            if (path.toLowerCase().startsWith("http://")) {
                pathURL = new URL(path);
            } else {
                pathURL = new File(path).toURL();
            }
        } catch (MalformedURLException e) {
            throw new PicoBuilderException("classpath '" + path + "' malformed ", e);
        }
        reflectionContainerAdapter.addClassLoaderURL(pathURL);
        return null;
    }

    private Object createBeanNode(Map attributes, MutablePicoContainer pico) {
        // lets create a bean
        Object answer = createBean(attributes);
        pico.registerComponentInstance(answer);
        return answer;
    }

    private Object createComponentNode(Map attributes, MutablePicoContainer pico, Object name) {
        Class type = (Class) attributes.remove("class");
        if (type != null) {
            Object key = attributes.remove("key");
            if (key != null) {
                pico.registerComponentImplementation(key, type);
            } else {
                pico.registerComponentImplementation(type);
            }
            return name;
        } else {
            throw new PicoBuilderException("Must specify a class attribute for a component");
        }
    }

    protected Object createNode(Object name, Map attributes, Object value) {
        return createNode(name, attributes);
    }

    protected MutablePicoContainer createContainer(Map attributes, PicoContainer parent) {
        ComponentAdapterFactory adapterFactory = (ComponentAdapterFactory) attributes.remove("adapterFactory");
        Class containerImpl = (Class) attributes.remove("class");
        if (containerImpl == null) {
            containerImpl = DefaultSoftCompositionPicoContainer.class;
        }
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        if (parent != null) {
            dpc.registerComponentInstance(parent);
        }
        if (adapterFactory != null) {
            dpc.registerComponentInstance(adapterFactory);
        }
        DefaultPicoContainer dpc2 = new DefaultPicoContainer(dpc);
        dpc2.registerComponentImplementation(SoftCompositionPicoContainer.class, containerImpl);

        return (SoftCompositionPicoContainer) dpc2.getComponentInstance(SoftCompositionPicoContainer.class);
    }

    protected Object createBean(Map attributes) {
        Class type = (Class) attributes.remove("beanClass");
        if (type == null) {
            throw new PicoBuilderException("Bean must have a beanClass attribute");
        }
        try {
            Object bean = type.newInstance();
            // now let's set the properties on the bean
            for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = entry.getKey().toString();
                Object value = entry.getValue();
                InvokerHelper.setProperty(bean, name, value);
            }
            return bean;
        } catch (IllegalAccessException e) {
            throw new PicoBuilderException("Failed to create bean of type '" + type + "'. Reason: " + e, e);
        } catch (InstantiationException e) {
            throw new PicoBuilderException("Failed to create bean of type " + type + "'. Reason: " + e, e);
        }
    }
}
