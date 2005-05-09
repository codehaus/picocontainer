/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by the committers                                           *
 *****************************************************************************/
package org.picocontainer.alternatives;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.LifecycleManager;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
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
public class ImplementationHidingCachingPicoContainer extends AbstractDelegatingMutablePicoContainer implements Serializable {

    private CachingComponentAdapterFactory caf;
    private LifecycleManager lifecycleManager;

    /**
     * Creates a new container with a parent container.
     */

    public ImplementationHidingCachingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent) {
        this(parent, new CachingComponentAdapterFactory(caf), new DefaultLifecycleManager());
    }

    public ImplementationHidingCachingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent, LifecycleManager lifecyleManager) {
        this(parent, new CachingComponentAdapterFactory(caf), lifecyleManager);
    }

    private ImplementationHidingCachingPicoContainer(PicoContainer parent, CachingComponentAdapterFactory caf, LifecycleManager lifecycleManager) {
        super(new ImplementationHidingPicoContainer(caf, parent, lifecycleManager));
        this.caf = caf;
        this.lifecycleManager = lifecycleManager;
    }

    /**
     * Creates a new container with a parent container.
     */
    public ImplementationHidingCachingPicoContainer(PicoContainer parent) {
        this(new DefaultComponentAdapterFactory(), parent);
    }


    /**
     * Creates a new container with no parent container.
     */
    public ImplementationHidingCachingPicoContainer() {
        this(null);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            if (clazz.isInterface()) {
                ComponentAdapter delegate = caf.createComponentAdapter(componentKey, componentImplementation, null);
                return getDelegate().registerComponent(new CachingComponentAdapter(new ImplementationHidingComponentAdapter(delegate, true)));
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
                return getDelegate().registerComponent(new CachingComponentAdapter(ihDelegate));
            }
        }
        return getDelegate().registerComponentImplementation(componentKey, componentImplementation, parameters);
    }


    public MutablePicoContainer makeChildContainer() {
        ImplementationHidingCachingPicoContainer pc = new ImplementationHidingCachingPicoContainer(this, caf, lifecycleManager);
        getDelegate().addChildContainer(pc);
        return pc;

    }

}
