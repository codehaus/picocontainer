/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picoextras.pool;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.extras.DecoratingComponentAdapter;

/**
 *  <p><code>DefaultPicoPool</code> is a pooling component that uses a pico container for the pool
 * and is picofiable (though does break the single ctor thing)
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $ Revision: 1.0 $
 */
public class PicoPoolComponentAdapter extends DecoratingComponentAdapter {
    protected DefaultPicoPool pool;

    /**
     * @param delegate
     */
    public PicoPoolComponentAdapter(ComponentAdapter delegate, DefaultPicoPool pool) {
        super(delegate);
        this.pool = pool;
    }

    public void returnComponentInstance(Object component) {
        pool.returnComponent(component);
    }

    /* (non-Javadoc)
     * @see org.picocontainer.ComponentAdapter#getComponentInstance(org.picocontainer.MutablePicoContainer)
     */
    public Object getComponentInstance(MutablePicoContainer componentRegistry)
            throws
            PicoInitializationException,
            PicoIntrospectionException,
            AssignabilityRegistrationException,
            NotConcreteRegistrationException {
        return pool.borrowComponent();
    }

}
