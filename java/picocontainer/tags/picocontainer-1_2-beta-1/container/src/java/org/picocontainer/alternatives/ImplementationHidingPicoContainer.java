/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.alternatives;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.LifecycleManager;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DefaultLifecycleManager;

import java.io.Serializable;

/**
 * This special MutablePicoContainer hides implementations of components if the key is an interface.
 * It's very simple. Instances that are registered directly and components registered without key
 * are not hidden.
 *
 * @author Paul Hammant
 * @version $Revision$
 * @since 1.1
 */
public class ImplementationHidingPicoContainer extends AbstractDelegatingMutablePicoContainer implements Serializable {

    private ComponentAdapterFactory caf;
    private LifecycleManager lifecycleManager;

    /**
     * Creates a new container with a parent container.
     */
    public ImplementationHidingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent, LifecycleManager lifecycleManager) {
        super(new DefaultPicoContainer(caf, parent, lifecycleManager));
        this.caf = caf;
        this.lifecycleManager = lifecycleManager;
    }


    public ImplementationHidingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent) {
        this(caf, parent, new DefaultLifecycleManager());
    }

    /**
     * Creates a new container with a parent container.
     */
    public ImplementationHidingPicoContainer(PicoContainer parent) {
        this(new DefaultComponentAdapterFactory(), parent);
    }


    /**
     * Creates a new container with no parent container.
     */
    public ImplementationHidingPicoContainer() {
        this(null);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            if (clazz.isInterface()) {
                ComponentAdapter delegate = caf.createComponentAdapter(componentKey, componentImplementation, null);
                return getDelegate().registerComponent(new ImplementationHidingComponentAdapter(delegate, true));
            }
        }
        return getDelegate().registerComponentImplementation(componentKey, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            if (clazz.isInterface()) {
                ComponentAdapter delegate = caf.createComponentAdapter(componentKey, componentImplementation, parameters);
                ImplementationHidingComponentAdapter ihDelegate = new ImplementationHidingComponentAdapter(delegate, true);
                return getDelegate().registerComponent(ihDelegate);
            }
        }
        return getDelegate().registerComponentImplementation(componentKey, componentImplementation, parameters);
    }

    public MutablePicoContainer makeChildContainer() {
        ImplementationHidingPicoContainer pc = new ImplementationHidingPicoContainer(caf, this, lifecycleManager);
        getDelegate().addChildContainer(pc);
        return pc;
    }

}
