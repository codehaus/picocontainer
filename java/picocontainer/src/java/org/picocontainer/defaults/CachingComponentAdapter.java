/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.*;

import java.io.Serializable;

/**
 * This ComponentAdapter caches the instance.
 * @version $Revision$
 */
public class CachingComponentAdapter extends DecoratingComponentAdapter {

    private ObjectReference instanceReference;

    public CachingComponentAdapter(ComponentAdapter delegate) {
        this(delegate, new SimpleReference());
    }

    public CachingComponentAdapter(ComponentAdapter delegate, ObjectReference instanceReference) {
        super(delegate);
        this.instanceReference = instanceReference;
    }

    public Object getComponentInstance()
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (instanceReference.get() == null) {
            instanceReference.set(super.getComponentInstance());
        }
        return instanceReference.get();
    }
}
