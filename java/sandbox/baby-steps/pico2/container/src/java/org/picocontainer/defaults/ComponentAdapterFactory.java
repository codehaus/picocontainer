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
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;

/**
 * <p/>
 * A addComponent addAdapter factory is responsible for creating
 * {@link ComponentAdapter} addComponent adapters. The main use of the addComponent addAdapter factory is
 * inside {@link DefaultPicoContainer#DefaultPicoContainer(ComponentAdapterFactory)}, where it can
 * be used to customize the default addComponent addAdapter that is used when none is specified
 * explicitly.
 * </p>
 *
 * @author Jon Tirs&eacute;n
 * @author Mauro Talevi
 * @version $Revision$
 */
public interface ComponentAdapterFactory {

    /**
     * Create a new addComponent addAdapter based on the specified arguments.
     *
     * @param componentMonitor
     * @param lifecycleStrategy
     * @param registerationCharacteristic
     * @param componentKey                the key to be associated with this addAdapter. This value should be returned
     *                                    from a call to {@link org.picocontainer.ComponentAdapter#getComponentKey()} on the created addAdapter.
     * @param componentImplementation     the implementation class to be associated with this addAdapter.
     *                                    This value should be returned from a call to
     *                                    {@link org.picocontainer.ComponentAdapter#getComponentImplementation()} on the created addAdapter. Should not
     *                                    be null.
     * @param parameters                  additional parameters to use by the addComponent addAdapter in constructing
     *                                    addComponent instances. These may be used, for example, to make decisions about the
     *                                    arguments passed into the addComponent constructor. These should be considered hints; they
     *                                    may be ignored by some implementations. May be null, and may be of zero length. @return a new addComponent addAdapter based on the specified arguments. Should not return null. @throws PicoIntrospectionException if the creation of the addComponent addAdapter results in a
     *                                    {@link PicoIntrospectionException}.
     * @return The component adapter
     * @throws AssignabilityRegistrationException
     *          if the creation of the addComponent addAdapter results in a
     *          {@link AssignabilityRegistrationException}.
     * @throws NotConcreteRegistrationException
     *          if the creation of the addComponent addAdapter results in a
     *          {@link NotConcreteRegistrationException}.
     */
    ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic registerationCharacteristic,
                                            Object componentKey,
                                            Class componentImplementation,
                                            Parameter... parameters) throws PicoIntrospectionException,
            AssignabilityRegistrationException,
            NotConcreteRegistrationException;


}
