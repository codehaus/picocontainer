/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

/**
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class DefaultComponentAdapter extends TransientComponentAdapter {

    private Object componentInstance;

    /**
     * Explicitly specifies parameters, if null uses default parameters.
     * 
     * @param componentKey            
     * @param componentImplementation 
     * @param parameters              
     */
    public DefaultComponentAdapter(final Object componentKey,
                                   final Class componentImplementation,
                                   Parameter[] parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters);
    }

    /**
     * Use default parameters.
     * 
     * @param componentKey            
     * @param componentImplementation 
     */
    public DefaultComponentAdapter(Object componentKey,
                                   Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        this(componentKey, componentImplementation, null);
    }


    public Object getComponentInstance(MutablePicoContainer picoContainer)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (componentInstance == null) {
            componentInstance = super.getComponentInstance(picoContainer);
            picoContainer.addOrderedComponentAdapter(this);
        }
        return componentInstance;
    }

}
