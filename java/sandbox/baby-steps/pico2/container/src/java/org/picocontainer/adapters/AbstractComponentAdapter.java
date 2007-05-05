/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.adapters;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoVisitor;
import org.picocontainer.adapters.MonitoringComponentAdapter;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DelegatingComponentMonitor;

/**
 * Base class for a ComponentAdapter with general functionality.
 * This implementation provides basic checks for a healthy implementation of a ComponentAdapter.
 * It does not allow to use <code>null</code> for the component key or the implementation, 
 * ensures that the implementation is a concrete class and that the key is assignable from the 
 * implementation if the key represents a type.   
 *  
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 * @since 1.0
 */
public abstract class AbstractComponentAdapter extends MonitoringComponentAdapter {
    private Object componentKey;
    private Class componentImplementation;

    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @throws org.picocontainer.defaults.AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to.
     */
    protected AbstractComponentAdapter(Object componentKey, Class componentImplementation) throws AssignabilityRegistrationException {
        this(componentKey, componentImplementation, new DelegatingComponentMonitor());
    }

    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param monitor the component monitor used by this ComponentAdapter
     * @throws AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to.
     */
    protected AbstractComponentAdapter(Object componentKey, Class componentImplementation, ComponentMonitor monitor) throws AssignabilityRegistrationException {
        super(monitor);
        if (componentImplementation == null) {
            throw new NullPointerException("componentImplementation");
        }
        this.componentKey = componentKey;
        this.componentImplementation = componentImplementation;
        checkTypeCompatibility();
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.ComponentAdapter#getComponentKey()
     */
    public Object getComponentKey() {
        if (componentKey == null) {
            throw new NullPointerException("componentKey");
        }
        return componentKey;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.ComponentAdapter#getComponentImplementation()
     */
    public Class getComponentImplementation() {
        return componentImplementation;
    }

    protected void checkTypeCompatibility() throws AssignabilityRegistrationException {
        if (componentKey instanceof Class) {
            Class componentType = (Class) componentKey;
            if (!componentType.isAssignableFrom(componentImplementation)) {
                throw new AssignabilityRegistrationException(componentType, componentImplementation);
            }
        }
    }

    /**
     * @return Returns the ComponentAdapter's class name and the component's key.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getClass().getName() + "[" + getComponentKey() + "]";
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
    }
    
}
