/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

/*
TODO:

don't depend on child packages
o dynaop
o aop

don't depend on proxytoys - introduce a PointcutsFactoryFactory
*/

package org.nanocontainer.script.groovy;

import dynaop.Aspects;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.util.BuilderSupport;
import org.aopalliance.intercept.MethodInterceptor;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.aop.AspectablePicoContainer;
import org.nanocontainer.aop.AspectsApplicator;
import org.nanocontainer.aop.AspectsContainer;
import org.nanocontainer.aop.AspectsManager;
import org.nanocontainer.aop.ClassPointcut;
import org.nanocontainer.aop.ComponentPointcut;
import org.nanocontainer.aop.MethodPointcut;
import org.nanocontainer.aop.PointcutsFactory;
import org.nanocontainer.aop.defaults.AspectsComponentAdapterFactory;
import org.nanocontainer.aop.dynaop.DynaopAspectsManager;
import org.nanocontainer.aop.dynaop.InstanceMixinFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.proxy.toys.nullobject.Null;

/**
 * Builds trees of PicoContainers and Pico components using GroovyMarkup
 *
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Stephen Molitor
 * @version $Revision$
 */
public class NanoContainerBuilder extends BuilderSupport {

    private final PointcutsFactory pointcutsFactory;
    private AspectablePicoContainer currentPico;
    private Object currentKey;

    public NanoContainerBuilder() {
        this((PointcutsFactory) Null.object(PointcutsFactory.class));
    }

    public NanoContainerBuilder(PointcutsFactory pointcutsFactory) {
        this.pointcutsFactory = pointcutsFactory;
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
        Map attributes = new HashMap();
        attributes.put("class", value);
        return createNode(name, attributes);
    }

    protected Object createNode(Object name, Map attributes) {
        Object current = getCurrent();
        if (current != null && current instanceof GroovyObject) {
            return createChildBuilder(current, name, attributes);
        } else {
            NanoContainer parent = (NanoContainer) current;
            if (name.equals("container")) {
                return createChildContainer(attributes, parent);
            } else {
                try {
                    return createChildOfContainerNode(parent, name, attributes);
                } catch (ClassNotFoundException e) {
                    throw new PicoBuilderException("ClassNotFoundException:" + e.getMessage(), e);
                }
            }
        }
    }

    private Object createChildBuilder(Object current, Object name, Map attributes) {
        GroovyObject groovyObject = (GroovyObject) current;
        return groovyObject.invokeMethod(name.toString(), attributes);
    }

    private Object createChildOfContainerNode(NanoContainer parentContainer, Object name, Map attributes) throws ClassNotFoundException {
        if (name.equals("aspect")) {
            return createAspectNode(attributes, name);
        } else if (name.equals("component")) {
            rememberComponentKey(attributes);
            return createComponentNode(attributes, parentContainer, name);
        } else if (name.equals("bean")) {
            return createBeanNode(attributes, parentContainer.getPico());
        } else if (name.equals("classpathelement")) {
            return createClassPathElementNode(attributes, parentContainer);
        } else if (name.equals("doCall")) {
            // TODO ????
            //BuilderSupport builder = (BuilderSupport) attributes.remove("class");
            return null;

        } else if (name.equals("newBuilder")) {
            return createNewBuilderNode(attributes, parentContainer);
        }
        throw new PicoBuilderException("Don't know how to create a '" + name + "' child of a container element");
    }

    private Object createNewBuilderNode(Map attributes, NanoContainer parentContainer) {
        String builderClass = (String) attributes.remove("class");
        NanoContainer factory = new DefaultNanoContainer();
        MutablePicoContainer parentPico = parentContainer.getPico();
        factory.getPico().registerComponentInstance(MutablePicoContainer.class, parentPico);
        try {
            factory.registerComponentImplementation(GroovyObject.class, builderClass);
        } catch (ClassNotFoundException e) {
            throw new PicoBuilderException("ClassNotFoundException " + builderClass);
        }
        Object componentInstance = factory.getPico().getComponentInstance(GroovyObject.class);
        return componentInstance;
    }

    private Object createClassPathElementNode(Map attributes, NanoContainer nanoContainer) {

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
        nanoContainer.addClassLoaderURL(pathURL);
        return pathURL;
    }

    private Object createBeanNode(Map attributes, MutablePicoContainer pico) {
        // lets create a bean
        Object answer = createBean(attributes);
        pico.registerComponentInstance(answer);
        return answer;
    }

    private Object createComponentNode(Map attributes, NanoContainer nano, Object name) throws ClassNotFoundException {
        Object key = attributes.remove("key");
        Object classValue = attributes.remove("class");
        Object instance = attributes.remove("instance");
        List parameters = (List) attributes.remove("parameters");

        Parameter[] parameterArray = getParameters(parameters);
        if (classValue instanceof Class) {
            Class clazz = (Class) classValue;
            key = key == null ? clazz : key;
            MutablePicoContainer pico = nano.getPico();
            pico.registerComponentImplementation(key, clazz, parameterArray);
        } else if (classValue instanceof String) {
            String className = (String) classValue;
            key = key == null ? className : key;
            nano.registerComponentImplementation(key, className, parameterArray);
        } else if (instance != null) {
            key = key == null ? instance.getClass() : key;
            nano.getPico().registerComponentInstance(key, instance);
        } else {
            throw new PicoBuilderException("Must specify a class attribute for a component");
        }

        return name;
    }


    protected Object createNode(Object name, Map attributes, Object value) {
        return createNode(name, attributes);
    }

    //TODO - two adapterFactory attributes ??
    protected NanoContainer createChildContainer(Map attributes, NanoContainer parent) {
        AspectsManager aspectsManager = (AspectsManager) attributes.remove("aspectsManager");
        if (aspectsManager == null) {
            aspectsManager = new DynaopAspectsManager(pointcutsFactory);
        }
//        ComponentAdapterFactory delegateAdapterFactory = (ComponentAdapterFactory) attributes.remove("adapterFactory");

//        Map newAttributes = new HashMap(attributes);
//        newAttributes.put("adapterFactory", adapterFactory);
//

        ComponentAdapterFactory componentAdapterFactory = (ComponentAdapterFactory) attributes.remove("componentAdapterFactory");
        componentAdapterFactory = componentAdapterFactory != null ? componentAdapterFactory : new DefaultComponentAdapterFactory();
        AspectsComponentAdapterFactory aspectsComponentAdapterFactory = createAdapterFactory(aspectsManager, componentAdapterFactory);

//        String name = (String) attributes.remove("name");
        ClassLoader parentClassLoader = null;
        MutablePicoContainer parentPicoContainer = null;
        if (parent != null) {
            parentClassLoader = parent.getComponentClassLoader();
            parentPicoContainer = parent.getPico();
            parentPicoContainer = new DefaultPicoContainer(aspectsComponentAdapterFactory, parentPicoContainer);
            parent.getPico().addChildContainer(parentPicoContainer);
        } else {
            parentClassLoader = Thread.currentThread().getContextClassLoader();
            parentPicoContainer = new DefaultPicoContainer(aspectsComponentAdapterFactory);
        }
        currentPico = mixinAspectablePicoContainer(aspectsManager, parentPicoContainer);

        return new DefaultNanoContainer(parentClassLoader, currentPico);
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

    private Object createAspectNode(Map attributes, Object name) {
        ClassPointcut classCut = (ClassPointcut) attributes.remove("classCut");
        MethodPointcut methodCut = (MethodPointcut) attributes.remove("methodCut");
        MethodInterceptor interceptor = (MethodInterceptor) attributes.remove("interceptor");
        Object interceptorKey = attributes.remove("interceptorKey");
        Class mixinClass = (Class) attributes.remove("mixinClass");
        List mixinInterfaces = (List) attributes.remove("mixinInterfaces");

        ComponentPointcut componentCut = (ComponentPointcut) attributes.remove("componentCut");
        if (componentCut == null && currentKey != null) {
            componentCut = currentPico.getPointcutsFactory().component(currentKey);
        }

        if (interceptor != null || interceptorKey != null) {
            registerInterceptor(currentPico, classCut, componentCut, methodCut, interceptor, interceptorKey);
        } else if (mixinClass != null) {
            registerMixin(currentPico, classCut, componentCut, toClassArray(mixinInterfaces), mixinClass);
        } else {
            throw new PicoBuilderException("No advice specified - must specify one of interceptor, interceptorKey, mixinClass, or mixinKey");
        }

        return name;
    }

    private void registerInterceptor(AspectablePicoContainer pico, ClassPointcut classCut,
                                     ComponentPointcut componentCut, MethodPointcut methodCut, MethodInterceptor interceptor,
                                     Object interceptorKey) {
        // precondition:
        if (interceptor == null && interceptorKey == null) {
            throw new RuntimeException("assertion failed -- non-null interceptor or interceptorKey expected");
        }

        // validate script:
        if (classCut == null && componentCut == null) {
            throw new PicoBuilderException("classCut or componentCut required for interceptor advice");
        }
        if (methodCut == null) {
            throw new PicoBuilderException("methodCut required for interceptor advice");
        }

        if (classCut != null) {
            if (interceptor != null) {
                pico.registerInterceptor(classCut, methodCut, interceptor);
            } else {
                pico.registerInterceptor(classCut, methodCut, interceptorKey);
            }
        } else {
            if (interceptor != null) {
                pico.registerInterceptor(componentCut, methodCut, interceptor);
            } else {
                pico.registerInterceptor(componentCut, methodCut, interceptorKey);
            }
        }
    }

    private void registerMixin(AspectablePicoContainer pico, ClassPointcut classCut, ComponentPointcut componentCut,
                               Class[] mixinInterfaces, Class mixinClass) {
        // precondition:
        if (mixinClass == null) {
            throw new RuntimeException("assertion failed -- mixinClass required");
        }

        // validate script:
        if (classCut == null && componentCut == null) {
            throw new PicoBuilderException("classCut or componentCut required for mixin advice");
        }

        if (classCut != null) {
            if (mixinInterfaces != null) {
                pico.registerMixin(classCut, mixinInterfaces, mixinClass);
            } else {
                pico.registerMixin(classCut, mixinClass);
            }
        } else {
            if (mixinInterfaces != null) {
                pico.registerMixin(componentCut, mixinInterfaces, mixinClass);
            } else {
                pico.registerMixin(componentCut, mixinClass);
            }
        }
    }

    private Class[] toClassArray(List l) {
        if (l == null) {
            return null;
        }
        return (Class[]) l.toArray(new Class[l.size()]);
    }

    private AspectablePicoContainer mixinAspectablePicoContainer(AspectsManager aspectsManager,
                                                                 MutablePicoContainer pico) {
        Aspects aspects = new Aspects();
        aspects.mixin(Pointcuts.ALL_CLASSES, new Class[]{AspectsContainer.class}, new InstanceMixinFactory(aspectsManager));
        aspects.interfaces(Pointcuts.ALL_CLASSES, new Class[]{AspectablePicoContainer.class});
        return (AspectablePicoContainer) ProxyFactory.getInstance(aspects).wrap(pico);
    }

    private AspectsComponentAdapterFactory createAdapterFactory(AspectsApplicator aspectsApplicator,
                                                                ComponentAdapterFactory delegateAdapterFactory) {
        if (delegateAdapterFactory != null) {
            return new AspectsComponentAdapterFactory(aspectsApplicator, delegateAdapterFactory);
        } else {
            return new AspectsComponentAdapterFactory(aspectsApplicator);
        }
    }

    private void rememberComponentKey(Map attributes) {
        Object key = attributes.get("key");
        Object clazz = attributes.get("class");
        if (key != null) {
            currentKey = key;
        } else {
            currentKey = clazz;
        }
    }
}
