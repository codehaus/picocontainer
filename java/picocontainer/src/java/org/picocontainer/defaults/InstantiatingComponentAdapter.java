/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This ComponentAdapter will instantiate a new object for each call to
 * {@link org.picocontainer.ComponentAdapter#getComponentInstance()}. That means that
 * when used with a PicoContainer, getComponentInstance will return a new
 * object each time.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public abstract class InstantiatingComponentAdapter extends AbstractComponentAdapter {
    protected Parameter[] parameters;

    protected InstantiatingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation);
        this.parameters = parameters;
    }

    public Object getComponentInstance()
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        List dependencyAdapterList = new ArrayList();
        Object instance = instantiateComponent(dependencyAdapterList);

        // Now, track the instantiation order
        for (Iterator it = dependencyAdapterList.iterator(); it.hasNext(); ) {
            ComponentAdapter dependencyAdapter = (ComponentAdapter) it.next();
            getContainer().addOrderedComponentAdapter(dependencyAdapter);
        }
        return instance;
    }

    protected Parameter[] createDefaultParameters(Class[] parameters) {
        Parameter[] componentParameters = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if(PicoContainer.class.isAssignableFrom(parameters[i])) {
                componentParameters[i] = new ConstantParameter(getContainer());
            } else {
                componentParameters[i] = new ComponentParameter();
            }
        }
        return componentParameters;
    }

    /**
     * Instantiate the object. 
     * @param adapterDependencies This list is filled with the dependent adapters of the instance.
     * @return Returns the new instance.
     * @throws PicoInitializationException
     * @throws PicoIntrospectionException
     * @throws AssignabilityRegistrationException
     * @throws NotConcreteRegistrationException
     */
    protected abstract Object instantiateComponent(List adapterDependencies) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
}
