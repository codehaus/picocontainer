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

import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * This is the core interface used for registration of components with a container. It is possible to register {@link
 * #registerComponentImplementation(Object,Class) an implementation class}, {@link #registerComponentInstance(Object) an
 * instance} or {@link #registerComponent(ComponentAdapter) a ComponentAdapter}.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 * @see "The <a href='package-summary.html#package_description'>The package description</a> has a basic overview of how to use the picocontainer package."
 * @since 1.0
 */
public interface MutablePicoContainer extends PicoContainer {

    /**
     * Register a component.
     *
     * @param componentKey            a key that identifies the component. Must be unique within the container. The type
     *                                of the key object has no semantic significance unless explicitly specified in the
     *                                documentation of the implementing container.
     * @param componentImplementation the component's implementation class. This must be a concrete class (ie, a
     *                                class that can be instantiated).
     * @return the ComponentAdapter that has been associated with this component. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the component later on.
     * @throws PicoRegistrationException if registration of the component fails.
     * @see #registerComponentImplementation(Object,Class,Parameter[]) a variant of this method that allows more control
     *      over the parameters passed into the componentImplementation constructor when constructing an instance.
     */
    ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException;

    /**
     * Register a component.
     *
     * @param componentKey            a key that identifies the component. Must be unique within the container. The type
     *                                of the key object has no semantic significance unless explicitly specified in the
     *                                documentation of the implementing container.
     * @param componentImplementation the component's implementation class. This must be a concrete class (ie, a
     *                                class that can be instantiated).
     * @param parameters              an array of parameters that gives the container hints about what arguments to pass
     *                                to the constructor when it is instantiated. Container implementations may ignore
     *                                one or more of these hints.
     * @return the ComponentAdapter that has been associated with this component. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the component later on.
     * @throws PicoRegistrationException if registration of the component fails.
     */
    ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException;

    /**
     * Register a component using the componentImplementation as key. Calling this method is equivalent to calling
     * <code>registerComponentImplementation(componentImplementation, componentImplementation)</code>.
     *
     * @param componentImplementation the concrete component class.
     * @return the ComponentAdapter that has been associated with this component. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the component later on.
     * @throws PicoRegistrationException if registration fails.
     */
    ComponentAdapter registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException;

    /**
     * Register an arbitrary object. The class of the object will be used as a key. Calling this method is equivalent to
     * calling     * <code>registerComponentImplementation(componentImplementation, componentImplementation)</code>.
     *
     * @param componentInstance
     * @return the ComponentAdapter that has been associated with this component. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the component later on.
     * @throws PicoRegistrationException if registration fails.
     */
    ComponentAdapter registerComponentInstance(Object componentInstance) throws PicoRegistrationException;

    /**
     * Register an arbitrary object as a component in the container. This is handy when other components in the same
     * container have dependencies on this kind of object, but where letting the container manage and instantiate it is
     * impossible.
     * <p/>
     * Beware that too much use of this method is an <a href="http://docs.codehaus.org/display/PICO/Instance+Registration">antipattern</a>.
     *
     * @param componentKey      a key that identifies the component. Must be unique within the conainer. The type of the
     *                          key object has no semantic significance unless explicitly specified in the implementing
     *                          container.
     * @param componentInstance an arbitrary object.
     * @return the ComponentAdapter that has been associated with this component. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the component later on.
     * @throws PicoRegistrationException if registration fails.
     */
    ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException;

    /**
     * Register a component via a ComponentAdapter. Use this if you need fine grained control over what
     * ComponentAdapter to use for a specific component.
     * 
     * @param componentAdapter the adapter
     * @return the same adapter that was passed as an argument.
     * @throws PicoRegistrationException if registration fails.
     */
    ComponentAdapter registerComponent(ComponentAdapter componentAdapter) throws PicoRegistrationException;

    /**
     * Unregister a component by key.
     * 
     * @param componentKey key of the component to unregister.
     * @return the ComponentAdapter that was associated with this component.
     */
    ComponentAdapter unregisterComponent(Object componentKey);

    /**
     * Unregister a component by instance.
     * 
     * @param componentInstance the component instance to unregister.
     * @return the ComponentAdapter that was associated with this component.
     */
    ComponentAdapter unregisterComponentByInstance(Object componentInstance);

    /**
     * Set the Parent container.
     * 
     * @param parent parent container.
     * @deprecated This will probably go away. implementations should take the parent in the constructor (constructor
     *             injection!)
     */
    void setParent(PicoContainer parent);

    /**
     * Make/Get an Immutable wrapper for this container.
     * @return an immutable wrapper for this container
     */
    PicoContainer getImmutable();

    MutablePicoContainer makeChildContainer();
    void addChildContainer(MutablePicoContainer child);
    void removeChildContainer(MutablePicoContainer child);

}
