/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsen                                               *
 *****************************************************************************/

package org.nanocontainer.nanning;

import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.AspectSystem;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.extras.HierarchicalComponentRegistry;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.Parameter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision: 1.15 $
 */
public class NanningNanoContainer implements RegistrationPicoContainer, Serializable {

    private final RegistrationPicoContainer mainContainer;
    HierarchicalComponentRegistry hierarchicalComponentRegistry;
    private final AspectSystem aspectSystem;
    private RegistrationPicoContainer serviceAndAspectContainer;

    public NanningNanoContainer(RegistrationPicoContainer serviceAndAspectContainer,
                                ComponentRegistry parentRegistry,
                                ComponentRegistry componentRegistry,
                                AspectSystem aspectSystem) {
        hierarchicalComponentRegistry =
                new HierarchicalComponentRegistry.WithChildRegistry(parentRegistry,
                        componentRegistry);
        this.serviceAndAspectContainer = serviceAndAspectContainer;
        this.mainContainer =
                new DefaultPicoContainer(new NanningComponentAdapterFactory(aspectSystem,
                        new DefaultComponentAdapterFactory()),
                        hierarchicalComponentRegistry);
        this.aspectSystem = aspectSystem;
    }

    public static class WithParentComponentRegistry extends NanningNanoContainer {
        public WithParentComponentRegistry(AspectSystem aspectSystem, ComponentRegistry parentRegistry) {
            super(new DefaultPicoContainer.WithComponentRegistry(parentRegistry),
                    parentRegistry,
                    new DefaultComponentRegistry(),
                    aspectSystem);
        }
    }

    public static class Default extends WithParentComponentRegistry {
        public Default(AspectSystem aspectSystem) {
            super(aspectSystem, new DefaultComponentRegistry());
        }
    }

    /**
     * Register aspect or service, these will <em>not</em> be weaved by the aspects.
     * @param serviceClass
     */
    public void registerServiceOrAspect(Class serviceClass, Class compomentImplementation)
            throws PicoRegistrationException, PicoIntrospectionException {
        serviceAndAspectContainer.registerComponent(serviceClass, compomentImplementation);
    }

    /**
     * Register aspect or service, these will <em>not</em> be weaved by the aspects.
     * @param compomentImplementation
     */
    public void registerServiceOrAspect(Class compomentImplementation) throws PicoRegistrationException, PicoIntrospectionException {
        serviceAndAspectContainer.registerComponentByClass(compomentImplementation);
    }

    public boolean hasComponent(Object componentKey) {
        return mainContainer.hasComponent(componentKey);
    }

    public Object getComponent(Object componentKey) {
        return mainContainer.getComponent(componentKey);
    }

    public Collection getComponents() {
        return mainContainer.getComponents();
    }

    public Collection getComponentKeys() {
        return mainContainer.getComponentKeys();
    }

    public Object getComponentMulticaster() {
        return mainContainer.getComponentMulticaster();
    }

    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        return mainContainer.getComponentMulticaster(callInInstantiationOrder, callUnmanagedComponents);
    }

    public void instantiateComponents() throws PicoInitializationException {
        serviceAndAspectContainer.instantiateComponents();
        Collection components = serviceAndAspectContainer.getComponents();
        for (Iterator iterator = components.iterator(); iterator.hasNext();) {
            Object component = (Object) iterator.next();
            if (component instanceof Aspect) {
                Aspect aspect = (Aspect) component;
                aspectSystem.addAspect(aspect);
            }
        }

        mainContainer.instantiateComponents();

    }

    public void registerComponentByClass(Class componentImplementation)
            throws PicoRegistrationException, PicoIntrospectionException {
        mainContainer.registerComponentByClass(componentImplementation);
    }

    public void registerComponent(Object componentKey, Class componentImplementation)
            throws PicoRegistrationException, PicoIntrospectionException {
        mainContainer.registerComponent(componentKey, componentImplementation);
    }

    public void registerComponent(Object componentKey, Object componentInstance)
            throws PicoRegistrationException, PicoIntrospectionException {
        mainContainer.registerComponent(componentKey, componentInstance);
    }

    public void registerComponentByInstance(Object componentInstance)
            throws PicoRegistrationException, PicoIntrospectionException {
        mainContainer.registerComponentByInstance(componentInstance);
    }

    public void registerComponent(Object componentKey, Class componentImplementation, Parameter[] parameters)
            throws PicoRegistrationException, PicoIntrospectionException {
        mainContainer.registerComponent(componentKey, componentImplementation, parameters);
    }

    public void addParameterToComponent(Object componentKey, Class parameter, Object arg) throws PicoIntrospectionException {
        mainContainer.addParameterToComponent(componentKey, parameter, arg);
    }
}
