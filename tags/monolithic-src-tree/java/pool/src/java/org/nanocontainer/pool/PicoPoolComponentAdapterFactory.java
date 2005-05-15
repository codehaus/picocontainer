/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 */

package org.nanocontainer.pool;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import java.io.Serializable;

/**
 * <p><code>PicoPoolComponentAdapterFactory</code> TODO (document class)
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $ Revision: 1.0 $
 */
public class PicoPoolComponentAdapterFactory implements ComponentAdapterFactory, Serializable {

    private DefaultPicoPool pool;
    private ComponentAdapterFactory delegate;

    public PicoPoolComponentAdapterFactory() {
        this(new DefaultComponentAdapterFactory());
    }

    public PicoPoolComponentAdapterFactory(ComponentAdapterFactory componentAdapterFactory) {
        delegate = componentAdapterFactory;
    }

    public PicoPoolComponentAdapterFactory(DefaultPicoPool pool, ComponentAdapterFactory componentAdapterFactory) {
        this.pool = pool;
        delegate = componentAdapterFactory;
    }


    /* (non-Javadoc)
     * @see org.picocontainer.defaults.ComponentAdapterFactory#createComponentAdapter(java.lang.Object, java.lang.Class, org.picocontainer.Parameter[])
     */
    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (pool == null) {
            pool = new DefaultPicoPool(componentImplementation);
        }
        return new PicoPoolComponentAdapter(delegate.createComponentAdapter(componentKey, componentImplementation, parameters), pool);
    }

}
