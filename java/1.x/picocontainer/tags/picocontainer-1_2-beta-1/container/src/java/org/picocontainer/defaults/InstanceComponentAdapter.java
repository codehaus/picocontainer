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

import org.picocontainer.PicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public class InstanceComponentAdapter extends AbstractComponentAdapter {
    private Object componentInstance;

    public InstanceComponentAdapter(Object componentKey, Object componentInstance) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentInstance.getClass());
        this.componentInstance = componentInstance;
    }

    public Object getComponentInstance(PicoContainer container) {
        return componentInstance;
    }

    public void verify(PicoContainer container) {
    }
}
