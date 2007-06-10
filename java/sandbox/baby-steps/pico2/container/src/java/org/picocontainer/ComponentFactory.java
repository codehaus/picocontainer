/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;

/**
 * <p/>
 * A component adapter factory is responsible for creating
 * {@link ComponentAdapter} component adapters. The main use of the component adapter factory is
 * inside {@link DefaultPicoContainer#DefaultPicoContainer(ComponentFactory)}, where it can
 * be used to customize the default component adapter that is used when none is specified
 * explicitly.
 * </p>
 *
 * @author Jon Tirs&eacute;n
 * @author Mauro Talevi
 * @version $Revision$
 */
public interface ComponentFactory {

    /**
     * Create a new component adapter based on the specified arguments.
     *
     * @param componentMonitor
     * @param lifecycleStrategy
     * @param componentCharacteristic
     * @param componentKey                the key to be associated with this addAdapter. This value should be returned
     *                                    from a call to {@link org.picocontainer.ComponentAdapter#getComponentKey()} on the created addAdapter.
     * @param componentImplementation     the implementation class to be associated with this addAdapter.
     *                                    This value should be returned from a call to
     *                                    {@link org.picocontainer.ComponentAdapter#getComponentImplementation()} on the created addAdapter. Should not
     *                                    be null.
     * @param parameters                  additional parameters to use by the component adapter in constructing
     *                                    component instances. These may be used, for example, to make decisions about the
     *                                    arguments passed into the component constructor. These should be considered hints; they
     *                                    may be ignored by some implementations. May be null, and may be of zero length. @return a new component adapter based on the specified arguments. Should not return null. @throws PicoIntrospectionException if the creation of the component adapter results in a
     *                                    {@link PicoIntrospectionException}.
     * @return The component adapter
     * @throws org.picocontainer.PicoRegistrationException
     *          if the creation of the component adapter results in a
     *          {@link org.picocontainer.PicoRegistrationException}.
     * @throws PicoIntrospectionException
     */
    ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                            LifecycleStrategy lifecycleStrategy,
                                            ComponentCharacteristic componentCharacteristic,
                                            Object componentKey,
                                            Class componentImplementation,
                                            Parameter... parameters) throws PicoIntrospectionException,
                                                                            PicoRegistrationException;


}
