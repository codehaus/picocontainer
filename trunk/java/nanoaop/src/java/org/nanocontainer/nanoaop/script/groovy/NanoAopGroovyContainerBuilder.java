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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.nanocontainer.nanoaop.AspectablePicoContainer;
import org.nanocontainer.nanoaop.AspectsApplicator;
import org.nanocontainer.nanoaop.AspectsContainer;
import org.nanocontainer.nanoaop.AspectsManager;
import org.nanocontainer.nanoaop.ClassPointcut;
import org.nanocontainer.nanoaop.ComponentPointcut;
import org.nanocontainer.nanoaop.MethodPointcut;
import org.nanocontainer.nanoaop.PointcutsFactory;
import org.nanocontainer.nanoaop.defaults.AspectsComponentAdapterFactory;
import org.nanocontainer.nanoaop.dynaop.DynaopAspectsManager;
import org.nanocontainer.nanoaop.dynaop.DynaopPointcutsFactory;
import org.nanocontainer.nanoaop.dynaop.InstanceMixinFactory;
import org.nanocontainer.script.groovy.NanoGroovyBuilder;
import org.nanocontainer.script.groovy.PicoBuilderException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;

import dynaop.Aspects;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;

/**
 * Extends <code>org.nanocontainer.script.groovy.NanoGroovyBuilder</code> to
 * provide support for adding aspects. {@inheritDoc}
 * 
 * @author Stephen Molitor
 * @version $Revision$
 */
public class NanoAopGroovyContainerBuilder extends NanoGroovyBuilder {

    private PointcutsFactory pointcutsFactory;
    private AspectablePicoContainer currentPico;
    private Object currentKey;

    /**
     * Creates a new <code>NanoAopGroovyContainerBuilder</code> that will use a
     * <code>org.nanocontainer.nanoaop.dynaop.DynaopPointcutsFactory</code>.
     */
    public NanoAopGroovyContainerBuilder() {
        this(new DynaopPointcutsFactory());
    }

    /**
     * Creates a new <code>NanoAopGroovyContainerBuilder</code> that will use the given
     * pointcuts factory.
     * 
     * @param pointcutsFactory the pointcuts factory.
     */
    public NanoAopGroovyContainerBuilder(PointcutsFactory pointcutsFactory) {
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

    protected MutablePicoContainer createContainer(Map attributes, MutablePicoContainer parent) {
        AspectsManager aspectsManager = (AspectsManager) attributes.remove("aspectsManager");
        if (aspectsManager == null) {
            aspectsManager = new DynaopAspectsManager(pointcutsFactory);
        }
        pointcutsFactory = aspectsManager.getPointcutsFactory();
        ComponentAdapterFactory delegateAdapterFactory = (ComponentAdapterFactory) attributes.remove("adapterFactory");
        AspectsComponentAdapterFactory adapterFactory = createAdapterFactory(aspectsManager, delegateAdapterFactory);

        Map newAttributes = new HashMap(attributes);
        newAttributes.put("adapterFactory", adapterFactory);
        MutablePicoContainer pico = super.createContainer(newAttributes, parent);
        currentPico = mixinAspectablePicoContainer(aspectsManager, pico);
        return currentPico;
    }

    private void rememberComponentKey(Map attributes) {
        Object key = attributes.get("key");
        Object clazz = attributes.get("class");
        Object instance = attributes.get("instance");
        Class beanClass = (Class) attributes.get("beanClass");
        if (key != null) {
            currentKey = key;
        } else if (clazz != null) {
            currentKey = clazz;
        } else if (instance != null) {
            currentKey = instance.getClass();
        } else if (beanClass != null) {
            currentKey = beanClass;
        } else {
            throw new PicoBuilderException("at least one of key, class, instance, or beanClass required for component");
        }
    }

    private Object createAspectNode(Map attributes, Object name) {
        ClassPointcut classCut = (ClassPointcut) attributes.remove("classCut");
        MethodPointcut methodCut = (MethodPointcut) attributes.remove("methodCut");
        MethodInterceptor interceptor = (MethodInterceptor) attributes.remove("interceptor");
        Object interceptorKey = attributes.remove("interceptorKey");
        Class mixinClass = (Class) attributes.remove("mixinClass");
        Object mixinKey = attributes.remove("mixinKey");
        List mixinInterfaces = (List) attributes.remove("mixinInterfaces");

        ComponentPointcut componentCut = (ComponentPointcut) attributes.remove("componentCut");
        if (componentCut == null && currentKey != null) {
            componentCut = currentPico.getPointcutsFactory().component(currentKey);
        }

        if (interceptor != null || interceptorKey != null) {
            registerInterceptor(currentPico, classCut, componentCut, methodCut, interceptor, interceptorKey);
        } else if (mixinClass != null || mixinKey != null) {
            registerMixin(currentPico, classCut, componentCut, toClassArray(mixinInterfaces), mixinClass, mixinKey);
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
            Class[] mixinInterfaces, Class mixinClass, Object mixinKey) {
        // precondition:
        if (mixinClass == null && mixinKey == null) {
            throw new RuntimeException("assertion failed -- non-null mixinClass or mixinKey expected");
        }

        // validate script:
        if (classCut == null && componentCut == null) {
            throw new PicoBuilderException("classCut or componentCut required for mixin advice");
        }
        if (mixinClass == null && mixinInterfaces == null) {
            throw new PicoBuilderException("mixinInterfaces required with mixinKey");
        }

        if (classCut != null) {
            if (mixinClass != null) {
                if (mixinInterfaces != null) {
                    pico.registerMixin(classCut, mixinInterfaces, mixinClass);
                } else {
                    pico.registerMixin(classCut, mixinClass);
                }
            } else {
                pico.registerMixin(classCut, mixinInterfaces, mixinKey);
            }
        } else {
            if (mixinClass != null) {
                if (mixinInterfaces != null) {
                    pico.registerMixin(componentCut, mixinInterfaces, mixinClass);
                } else {
                    pico.registerMixin(componentCut, mixinClass);
                }
            } else {
                pico.registerMixin(componentCut, mixinInterfaces, mixinKey);
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