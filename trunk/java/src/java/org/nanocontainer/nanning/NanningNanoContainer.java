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
import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentRegistry;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.hierarchical.HierarchicalComponentRegistry;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision: 1.15 $
 */
public class NanningNanoContainer implements PicoContainer, Serializable {

    private final PicoContainer mainContainer;
    HierarchicalComponentRegistry hierarchicalComponentRegistry;
    private final AspectSystem aspectSystem;
    private RegistrationPicoContainer serviceAndAspectContainer;

    public NanningNanoContainer(RegistrationPicoContainer serviceAndAspectContainer,
                                PicoContainer mainContainer,
                                ComponentRegistry componentRegistry,
                                AspectSystem aspectSystem) {
        hierarchicalComponentRegistry =
            new HierarchicalComponentRegistry.WithParentContainerAndChildRegistry(serviceAndAspectContainer, componentRegistry);
        this.serviceAndAspectContainer = serviceAndAspectContainer;
        this.mainContainer = mainContainer;
        this.aspectSystem = aspectSystem;
    }

    public static class WithNanningComponentFactory extends Default {
        public WithNanningComponentFactory(AspectSystem aspectSystem, ComponentFactory componentFactory) {
            super(aspectSystem, new NanningComponentFactory(aspectSystem, componentFactory));
        }
    }

    public static class Default extends NanningNanoContainer {
        public Default(AspectSystem aspectSystem, NanningComponentFactory componentFactory) {
            super(new DefaultPicoContainer.WithComponentFactory(componentFactory),
                  new DefaultPicoContainer.Default(),
                  new DefaultComponentRegistry(),
                  aspectSystem);
        }
    }

    public static class WithHierachicalComponentRegistry extends NanningNanoContainer {
        public WithHierachicalComponentRegistry(AspectSystem aspectSystem, NanningComponentFactory componentFactory, HierarchicalComponentRegistry hcr) {
            super(new DefaultPicoContainer.WithComponentFactory(componentFactory),
                  new DefaultPicoContainer.WithComponentRegistry(hcr),
                  new DefaultComponentRegistry(),
                  aspectSystem);
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

    public Set getComponents() {
        return mainContainer.getComponents();
    }

    public Set getComponentKeys() {
        return mainContainer.getComponentKeys();
    }

    public Object getCompositeComponent() {
        return mainContainer.getCompositeComponent();
    }

    public Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        return mainContainer.getCompositeComponent(callInInstantiationOrder, callUnmanagedComponents);
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
}
