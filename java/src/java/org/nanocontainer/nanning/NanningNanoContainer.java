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
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.DefaultComponentFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.hierarchical.HierarchicalPicoContainer;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision: 1.15 $
 */
public class NanningNanoContainer extends HierarchicalPicoContainer {
    private final AspectSystem aspectSystem;

    private RegistrationPicoContainer serviceAndAspectContainer;

    public NanningNanoContainer(ComponentFactory componentFactory, 
                                RegistrationPicoContainer serviceAndAspectContainer, AspectSystem aspectSystem) {
        super(new NanningComponentFactory(aspectSystem, componentFactory), serviceAndAspectContainer,
            new DefaultComponentRegistry());
        this.serviceAndAspectContainer = serviceAndAspectContainer;
        this.aspectSystem = aspectSystem;
    }

    public static class Default extends NanningNanoContainer {
        public Default(AspectSystem aspectSystem) {
            super(new DefaultComponentFactory(), new DefaultPicoContainer.Default(), aspectSystem);
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

        super.instantiateComponents();
    }
}
