/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/

package org.picocontainer;



/**
 * This is the core interface for registration of components.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public interface MutablePicoContainer extends PicoContainer {

    /**
     * Registers a component.
     *
     * @param componentKey a key that identifies the compoent. Must be unique within the conainer.
     * The type of the key object has no semantic significance unless explicitly specified
     * in the implementing container.
     * @param componentImplementation the concrete component class.
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException;

    /**
     * Registers a component.
     *
     * @param componentKey a key that identifies the compoent. Must be unique within the conainer.
     * The type of the key object has no semantic significance unless explicitly specified
     * in the implementing container.
     * @param componentImplementation the concrete component class.
     * @param parameters an array of parameters that gives the container hints about what arguments
     *    to pass to the constructor when it is instantiated.
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException;

    /**
     * Registers a component using the componentImplementation as key.
     *
     * @param componentImplementation the concrete component class.
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException;

    /**
     * Registers an arbitrary object, using its class as a key.
     *
     * @param componentInstance
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentInstance(Object componentInstance) throws PicoRegistrationException;

    /**
     * Registers an arbitrary object as a compoent in the container. This is
     * handy when other components in the same container have dependencies on this
     * kind of object, but where letting the container manage and instantiate it
     * is impossible.
     *
     * Beware that too much use of this method is an
     * <a href="http://docs.codehaus.org/display/PICO/Instance+Registration">antipattern</a>.
     *
     * @param componentKey a key that identifies the compoent. Must be unique within the conainer.
     * The type of the key object has no semantic significance unless explicitly specified
     * in the implementing container.
     * @param componentInstance an arbitrary object.
     * @throws PicoRegistrationException if registration fails.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException;

    /**
     * Registers a component via a ComponentAdapter. Use this if you need fine grained control over what ComponentAdapter
     * to use for a specific component.
     *
     * @param componentAdapter the adapter
     * @throws PicoRegistrationException if registration fails.
     * @return the same adapter that was passed as an argument.
     */
    ComponentAdapter registerComponent(ComponentAdapter componentAdapter) throws PicoRegistrationException;

    /**
     * Unregisters a component by key.
     *
     * @param componentKey key of the component to unregister.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter unregisterComponent(Object componentKey);

    /**
     * Unregisters a component by instance.
     *
     * @param componentInstance the component instance to unregister.
     * @return the associated ComponentAdapter.
     */
    ComponentAdapter unregisterComponentByInstance(Object componentInstance);

    /**
     * Sets the Parent container.
     * @param parent parent container.
     * @deprecated This will probably go away. implementations should take the parent
     * in the constructor (constructor injection!)
     */
    void setParent(PicoContainer parent);

}
