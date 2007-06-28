/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.ioc.avalon;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * An {@link ComponentAdapterFactory} which creates {@link AvalonComponentAdapter}s.
 * 
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class AvalonComponentAdapterFactory implements ComponentAdapterFactory {
    /**
     * {@inheritDoc}
     * 
     * @param componentKey {@inheritDoc}
     * @param componentImplementation {@inheritDoc}
     * @param parameters {@inheritDoc}
     * @return {@inheritDoc}
     * @throws PicoIntrospectionException {@inheritDoc}
     * @throws AssignabilityRegistrationException {@inheritDoc}
     * @throws NotConcreteRegistrationException {@inheritDoc}
     */ 
    public ComponentAdapter createComponentAdapter(final Object componentKey, final Class componentImplementation, final Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new AvalonComponentAdapter(componentKey, componentImplementation);
    }
}
