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

import java.util.List;
import java.util.Set;

/**
 * This is the core interface used for registration of components with a container. It is possible to register
 * implementations and instances here
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 * @see <a href="package-summary.html#package_description">See package description for basic overview how to use PicoContainer.</a>
 * @since 1.0
 */
public interface MutablePicoContainer extends PicoContainer, Startable, Disposable {

    /**
     * Register a addComponent and creates specific instructions on which constructor to use, along with
     * which components and/or constants to provide as constructor arguments.  These &quot;directives&quot; are
     * provided through an array of <tt>Parameter</tt> objects.  Parameter[0] correspondes to the first constructor
     * argument, Parameter[N] corresponds to the  N+1th constructor argument.
     * <h4>Tips for Parameter usage</h4>
     * <ul>
     * <li><strong>Partial Autowiring: </strong>If you have two constructor args to match and you only wish to specify one of the constructors and 
     * let PicoContainer wire the other one, you can use as parameters: 
     * <code><strong>new ComponentParameter()</strong>, new ComponentParameter("someService")</code>
     * The default constructor for the addComponent parameter indicates auto-wiring should take place for
     * that parameter.
     * </li>
     * <li><strong>Force No-Arg constructor usage:</strong> If you wish to force a addComponent to be constructed with
     * the no-arg constructor, use a zero length Parameter array.  Ex:  <code>new Parameter[0]</code> 
     * <ul>
     * 
     *
     * @param componentKey            a key that identifies the addComponent. Must be unique within the container. The type
     *                                of the key object has no semantic significance unless explicitly specified in the
     *                                documentation of the implementing container.
     * @param componentImplementationOrInstance the addComponent's implementation class. This must be a concrete class (ie, a
     *                                class that can be instantiated). Or an intance of the compoent.
     * @param parameters              the parameters that gives the container hints about what arguments to pass
     *                                to the constructor when it is instantiated. Container implementations may ignore
     *                                one or more of these hints.
     * @return the ComponentAdapter that has been associated with this addComponent. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the addComponent later on.
     * @throws PicoRegistrationException if registration of the addComponent fails.
     * @see org.picocontainer.Parameter
     * @see org.picocontainer.defaults.ConstantParameter
     * @see org.picocontainer.defaults.ComponentParameter
     */
    MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters);

    /**
     * Register a addComponent using the componentImplementation as key. Calling this method is equivalent to calling
     * <code>addAdapter(componentImplementation, componentImplementation)</code>.
     *
     * @param componentImplementation the concrete addComponent class.
     * @return the ComponentAdapter that has been associated with this addComponent. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the addComponent later on.
     * @throws PicoRegistrationException if registration fails.
     */
    MutablePicoContainer addComponent(Class componentImplementation);

    /**
     * Register an arbitrary object. The class of the object will be used as a key. Calling this method is equivalent to
     * calling     * <code>addAdapter(componentImplementation, componentImplementation)</code>.
     *
     * @param componentInstance
     * @return the ComponentAdapter that has been associated with this addComponent. In the majority of cases, this return
     *         value can be safely ignored, as one of the <code>getXXX()</code> methods of the
     *         {@link PicoContainer} interface can be used to retrieve a reference to the addComponent later on.
     * @throws PicoRegistrationException if registration fails.
     */
    MutablePicoContainer addComponent(Object componentInstance);

    /**
     * Register a addComponent via a ComponentAdapter. Use this if you need fine grained control over what
     * ComponentAdapter to use for a specific addComponent.
     * 
     * @param componentAdapter the addAdapter
     * @return the same addAdapter that was passed as an argument.
     * @throws PicoRegistrationException if registration fails.
     */
    MutablePicoContainer addAdapter(ComponentAdapter componentAdapter);

    /**
     * Unregister a addComponent by key.
     * 
     * @param componentKey key of the addComponent to unregister.
     * @return the ComponentAdapter that was associated with this addComponent.
     */
    ComponentAdapter removeComponent(Object componentKey);

    /**
     * Unregister a addComponent by instance.
     * 
     * @param componentInstance the addComponent instance to unregister.
     * @return the ComponentAdapter that was associated with this addComponent.
     */
    ComponentAdapter removeComponentByInstance(Object componentInstance);

    /**
     * Make a child container, using the same implementation of MutablePicoContainer as the parent.
     * It will have a reference to this as parent.  This will list the resulting MPC as a child.
     * Lifecycle events will be cascaded from parent to child
     * as a consequence of this.
     * 
     * @return the new child container.
     * @since 1.1
     */
    MutablePicoContainer makeChildContainer();

    /**
     * Add a child container. This action will list the the 'child' as exactly that in the parents scope.
     * It will not change the child's view of a parent.  That is determined by the constructor arguments of the child
     * itself. Lifecycle events will be cascaded from parent to child
     * as a consequence of calling this method.
     * 
     * @param child the child container
     * @return <code>true</code> if the child container was not already in.
     * @since 1.1                                           
     */
    boolean addChildContainer(PicoContainer child);

    /**
     * Remove a child container from this container. It will not change the child's view of a parent.
     * Lifecycle event will no longer be cascaded from the parent to the child.
     * 
     * @param child the child container
     * @return <code>true</code> if the child container has been removed.
     * @since 1.1
     */
    boolean removeChildContainer(PicoContainer child);


    ComponentAdapter lastCA();


    /**
     * You can change the characteristic of registration of a addComponent on the fly.
     * @param rcs characteristics
     * @return the same Pico instance with changed characteritics
     */
    MutablePicoContainer change(ComponentCharacteristic... rcs);

}
