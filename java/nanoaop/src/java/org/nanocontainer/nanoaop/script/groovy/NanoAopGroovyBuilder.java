/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.script.groovy;

import dynaop.Aspects;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.aop.AspectablePicoContainer;
import org.nanocontainer.aop.AspectsApplicator;
import org.nanocontainer.aop.AspectsContainer;
import org.nanocontainer.aop.AspectsManager;
import org.nanocontainer.aop.AspectsManager;
import org.nanocontainer.aop.ClassPointcut;
import org.nanocontainer.aop.ClassPointcut;
import org.nanocontainer.aop.ComponentPointcut;
import org.nanocontainer.aop.ComponentPointcut;
import org.nanocontainer.aop.MethodPointcut;
import org.nanocontainer.aop.MethodPointcut;
import org.nanocontainer.aop.PointcutsFactory;
import org.nanocontainer.aop.PointcutsFactory;
import org.nanocontainer.aop.defaults.AspectsComponentAdapterFactory;
import org.nanocontainer.aop.defaults.AspectsComponentAdapterFactory;
import org.nanocontainer.aop.dynaop.DynaopAspectsManager;
import org.nanocontainer.aop.dynaop.DynaopPointcutsFactory;
import org.nanocontainer.aop.dynaop.DynaopPointcutsFactory;
import org.nanocontainer.aop.dynaop.InstanceMixinFactory;
import org.nanocontainer.aop.dynaop.InstanceMixinFactory;
import org.nanocontainer.script.groovy.NanoGroovyBuilder;
import org.nanocontainer.script.groovy.PicoBuilderException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends <code>org.nanocontainer.script.groovy.NanoGroovyBuilder</code> to
 * provide support for adding aspects.
 * 
 * @author Stephen Molitor
 * @version $Revision$
 * @see org.nanocontainer.script.groovy.NanoGroovyBuilder
 */
public class NanoAopGroovyBuilder extends NanoGroovyBuilder {

    private PointcutsFactory pointcutsFactory;
    private AspectablePicoContainer currentPico;
    private Object currentKey;

    /**
     * Creates a new <code>NanoAopGroovyBuilder</code> that will use
     * a <code>org.nanocontainer.aop.dynaop.DynaopPointcutsFactory</code>.
     */
    public NanoAopGroovyBuilder() {
        this(new DynaopPointcutsFactory());
    }

    /**
     * Creates a new <code>NanoAopGroovyBuilder</code> that will use
     * the given pointcuts factory.
     * 
     * @param pointcutsFactory the pointcuts factory.
     */
    public NanoAopGroovyBuilder(PointcutsFactory pointcutsFactory) {
        this.pointcutsFactory = pointcutsFactory;
    }

    protected Object createNode(Object name) {
        if (name.equals("pointcuts")) {
            return pointcutsFactory;
        }
        return super.createNode(name);
    }

    protected Object createNode(Object name, Map attributes) {
        if (name.equals("aspect")) {
            return createAspectNode(attributes, name);
        }
        if (name.equals("component")) {
            rememberComponentKey(attributes);
        }
        return super.createNode(name, attributes);
    }

    protected NanoContainer createChildContainer(Map attributes, NanoContainer parent) {
        AspectsManager aspectsManager = (AspectsManager) attributes.remove("aspectsManager");
        if (aspectsManager == null) {
            aspectsManager = new DynaopAspectsManager(pointcutsFactory);
        }
        pointcutsFactory = aspectsManager.getPointcutsFactory();
        ComponentAdapterFactory delegateAdapterFactory = (ComponentAdapterFactory) attributes.remove("adapterFactory");
        AspectsComponentAdapterFactory adapterFactory = createAdapterFactory(aspectsManager, delegateAdapterFactory);

        Map newAttributes = new HashMap(attributes);
        newAttributes.put("adapterFactory", adapterFactory);
        NanoContainer nano = super.createChildContainer(newAttributes, parent);
        currentPico = mixinAspectablePicoContainer(aspectsManager, nano.getPico());
        return new DefaultNanoContainer(currentPico);
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
            throw new PicoBuilderException(
                    "No advice specified - must specify one of interceptor, interceptorKey, mixinClass, or mixinKey");
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

    private AspectablePicoContainer mixinAspectablePicoContainer(AspectsManager aspectsManager,
            MutablePicoContainer pico) {
        Aspects aspects = new Aspects();
        aspects.mixin(Pointcuts.ALL_CLASSES, new Class[] { AspectsContainer.class }, new InstanceMixinFactory(
                aspectsManager));
        aspects.interfaces(Pointcuts.ALL_CLASSES, new Class[] { AspectablePicoContainer.class });
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

    private Class[] toClassArray(List l) {
        if (l == null) {
            return null;
        }
        return (Class[]) l.toArray(new Class[l.size()]);
    }

}