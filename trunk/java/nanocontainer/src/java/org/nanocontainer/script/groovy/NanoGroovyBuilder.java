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

import groovy.lang.Closure;
import groovy.util.BuilderSupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.nanocontainer.reflection.ReflectionContainerAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstantParameter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    protected Object doInvokeMethod(String s, Object name, Object args) {
        //TODO use setDelegate() from Groovy beta 7
        Object answer = super.doInvokeMethod(s, name, args);
        List list = InvokerHelper.asList(args);
        if (!list.isEmpty()) {
            Object o = list.get(list.size() - 1);
            if (o instanceof Closure) {
                Closure closure = (Closure) o;
                closure.setDelegate(answer);
            }
        }
        return answer;
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
            try {
                return createChildOfContainerNode(parent, name, attributes);
            } catch (ClassNotFoundException e) {
                throw new PicoBuilderException("ClassNotFoundException:" + e.getMessage(), e);
            }
        }
        throw new PicoBuilderException("Unknown method: '" + name + "'");
    }

    private Object createChildOfContainerNode(Object parent, Object name, Map attributes) throws ClassNotFoundException {
        SoftCompositionPicoContainer parentContainer = (SoftCompositionPicoContainer) parent;
        if (name.equals("component")) {
            return createComponentNode(attributes, parentContainer, name);
        } else if (name.equals("builder")) {
            return createBuilderNode(attributes, parentContainer);
        } else if (name.equals("bean")) {
            return createBeanNode(attributes, parentContainer);
        } else if (name.equals("classpathelement")) {
            return createClassPathElementNode(attributes, parentContainer);
        } else if (name.equals("doCall")) {
            // TODO ????
            //BuilderSupport builder = (BuilderSupport) attributes.remove("class");
            return null;

        }
        throw new PicoBuilderException("Method: '" + name + "' must be a child of a container element");
    }

    private Object createContainerNode(Object parent, Map attributes) {
        MutablePicoContainer parentContainer = null;
        if (parent instanceof MutablePicoContainer) {
            parentContainer = (MutablePicoContainer) parent;
        }
        if (parentContainer == null) {
            parentContainer = (MutablePicoContainer) attributes.remove("parent");
            ;
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
        return pathURL;
    }

    private Object createBeanNode(Map attributes, MutablePicoContainer pico) {
        // lets create a bean
        Object answer = createBean(attributes);
        pico.registerComponentInstance(answer);
        return answer;
    }

    private Object createBuilderNode(Map attributes, MutablePicoContainer pico) throws ClassNotFoundException {
        // lets create a bean
        DefaultSoftCompositionPicoContainer dpc = new DefaultSoftCompositionPicoContainer();
        dpc.registerComponentInstance(MutablePicoContainer.class, pico);
        Object o = attributes.remove("class");
        Object answer = null;
        if (o instanceof Class) {
            answer = dpc.registerComponentImplementation((Class) o).getComponentInstance(pico);
        } else {
            answer = dpc.registerComponentImplementation((String) o).getComponentInstance(pico);
        }
        System.out.println("--> ? " + answer);

        return answer;
    }


    private Object createComponentNode(Map attributes, SoftCompositionPicoContainer pico, Object name) throws ClassNotFoundException {
        Object key = attributes.remove("key");
        Object type = attributes.remove("class");
        Object instance = attributes.remove("instance");
        List parameters = (List) attributes.remove("parameters");

        if (type != null) {
            registerComponentImplementation(pico, key, type, parameters);
        } else if (instance != null) {
            registerComponentInstance(pico, key, instance);
        } else {
            throw new PicoBuilderException("Must specify a class attribute for a component");
        }

        return name;
    }


    protected Object createNode(Object name, Map attributes, Object value) {
        return createNode(name, attributes);
    }

    protected MutablePicoContainer createContainer(Map attributes, MutablePicoContainer parent) {
        ComponentAdapterFactory adapterFactory = (ComponentAdapterFactory) attributes.remove("adapterFactory");
        Class containerImpl = (Class) attributes.remove("class");
        String name = (String) attributes.remove("name");
        SoftCompositionPicoContainer softPico = null;

        if (containerImpl != null) {
            SoftCompositionPicoContainer scpc = null;
            if (parent != null) {
                ClassLoader cl = null;
                if (parent instanceof SoftCompositionPicoContainer) {
                    cl = ((SoftCompositionPicoContainer) parent).getComponentClassLoader();
                } else {
                    cl = this.getClass().getClassLoader();
                }
                scpc = new DefaultSoftCompositionPicoContainer(cl);
                scpc.registerComponentInstance(ClassLoader.class, cl);
                scpc.registerComponentInstance(PicoContainer.class, parent);
            } else {
                scpc = new DefaultSoftCompositionPicoContainer(NanoGroovyBuilder.class.getClassLoader());
                scpc.registerComponentInstance(ClassLoader.class, NanoGroovyBuilder.class.getClassLoader());
            }
            if (adapterFactory != null) {
                scpc.registerComponentInstance(ComponentAdapterFactory.class, adapterFactory);
            }
            scpc.registerComponentImplementation(SoftCompositionPicoContainer.class, containerImpl);
            softPico = (SoftCompositionPicoContainer) scpc.getComponentInstance(SoftCompositionPicoContainer.class);
        } else {

            if (parent != null) {
                ClassLoader cl = null;
                if (parent instanceof SoftCompositionPicoContainer) {
                    cl = ((SoftCompositionPicoContainer) parent).getComponentClassLoader();
                } else {
                    cl = this.getClass().getClassLoader();
                }
                if (adapterFactory != null) {
                    softPico = new DefaultSoftCompositionPicoContainer(cl, adapterFactory, parent);
                } else {
                    softPico = new DefaultSoftCompositionPicoContainer(cl, parent);
                }
                parent.addChildContainer(name, softPico);

            } else {
                if (adapterFactory != null) {
                    softPico = new DefaultSoftCompositionPicoContainer(NanoGroovyBuilder.class.getClassLoader(), adapterFactory, null);
                } else {
                    softPico = new DefaultSoftCompositionPicoContainer();
                }
            }
        }

        return softPico;
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

    private void registerComponentImplementation(SoftCompositionPicoContainer pico,
                                                 Object key, Object type, List paramsList) throws ClassNotFoundException {

        Parameter[] parameters = getParameters(paramsList);
        if (key != null) {
            if (type instanceof String) {
                if (parameters != null) {
                    pico.registerComponentImplementation(key, (String) type, parameters);
                } else {
                    pico.registerComponentImplementation(key, (String) type);
                }
            } else {
                if (parameters != null) {
                    pico.registerComponentImplementation(key, (Class) type, parameters);
                } else {
                    pico.registerComponentImplementation(key, (Class) type);
                }
            }
        } else {
            if (type instanceof String) {
                pico.registerComponentImplementation((String) type);
            } else {
                pico.registerComponentImplementation((Class) type);
            }
        }
    }

    private Parameter[] getParameters(List paramsList) {
        if (paramsList == null) {
            return null;
        }
        int n = paramsList.size();
        Parameter[] parameters = new Parameter[n];
        for (int i = 0; i < n; ++i) {
            parameters[i] = toParameter(paramsList.get(i));
        }
        return parameters;
    }

    private Parameter toParameter(Object obj) {
        return obj instanceof Parameter ? (Parameter) obj : new ConstantParameter(obj);
    }

    private void registerComponentInstance(SoftCompositionPicoContainer pico, Object key, Object instance) {
        if (key != null) {
            pico.registerComponentInstance(key, instance);
        } else {
            pico.registerComponentInstance(instance);
        }
    }
}
