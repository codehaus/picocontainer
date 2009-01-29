/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant & Obie Fernandez & Aslak                    *
 *****************************************************************************/

package org.picocontainer;

import org.picocontainer.behaviors.Cached;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.adapters.AbstractAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * A component monitor is responsible for monitoring the component instantiation
 * and method invocation.
 * 
 * @author Paul Hammant
 * @author Obie Fernandez
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
public interface ComponentMonitor {

    /**
     * Event thrown as the component is being instantiated using the given constructor
     *
     * @param container
     * @param componentAdapter
     * @param constructor the Constructor used to instantiate the addComponent @return the constructor to use in instantiation (nearly always the same one as passed in)
     */
    <T> Constructor<T> instantiating(PicoContainer container, ComponentAdapter<T> componentAdapter,
                              Constructor<T> constructor
    );

    /**
     * Event thrown after the component has been instantiated using the given constructor.
     * This should be called for both Constructor and Setter DI.
     *
     * @param container
     * @param componentAdapter
     * @param constructor the Constructor used to instantiate the addComponent
     * @param instantiated the component that was instantiated by PicoContainer
     * @param injected the components during instantiation.
     * @param duration the duration in milliseconds of the instantiation
     */

    <T> void instantiated(PicoContainer container, ComponentAdapter<T> componentAdapter,
                      Constructor<T> constructor,
                      Object instantiated,
                      Object[] injected,
                      long duration);

    /**
     * Event thrown if the component instantiation failed using the given constructor
     * 
     * @param container
     * @param componentAdapter
     * @param constructor the Constructor used to instantiate the addComponent
     * @param cause the Exception detailing the cause of the failure
     */
    <T> void instantiationFailed(PicoContainer container,
                             ComponentAdapter<T> componentAdapter,
                             Constructor<T> constructor,
                             Exception cause);

    /**
     * Event thrown as the component method is being invoked on the given instance
     * 
     * @param container
     * @param componentAdapter
     * @param member
     * @param instance the component instance
     */
    void invoking(PicoContainer container, ComponentAdapter<?> componentAdapter, Member member, Object instance);

    /**
     * Event thrown after the component method has been invoked on the given instance
     * 
     * @param container
     * @param componentAdapter
     * @param method the Method invoked on the component instance
     * @param instance the component instance
     * @param duration the duration in millis of the invocation
     */
    void invoked(PicoContainer container,
                 ComponentAdapter<?> componentAdapter,
                 Method method,
                 Object instance,
                 long duration);

    /**
     * Event thrown if the component method invocation failed on the given instance
     * 
     * @param member
     * @param instance the component instance
     * @param cause the Exception detailing the cause of the failure
     */
    void invocationFailed(Member member, Object instance, Exception cause);

    /**
     * Event thrown if a lifecycle method invocation - start, stop or dispose - 
     * failed on the given instance
     *
     * @param container
     * @param componentAdapter
     * @param method the lifecycle Method invoked on the component instance
     * @param instance the component instance
     * @param cause the RuntimeException detailing the cause of the failure
     */
     void lifecycleInvocationFailed(MutablePicoContainer container,
                                   ComponentAdapter<?> componentAdapter, Method method,
                                   Object instance,
                                   RuntimeException cause);


    /**
     * No Component has been found for the key in question. Implementors of this have a last ditch opportunity to
     * specify something for the need.
     *
     * @param container
     * @param componentKey
     */
    Object noComponentFound(MutablePicoContainer container, Object componentKey);

    /**
     * A mechanism to monitor or override the Injectors being made for components.
     *
     * @param injector
     * @return an Injector. For most implementations, the same one as was passed in.
     */
    Injector newInjector(Injector injector);

    /**
     * A mechanism to monitor or override the Behaviors being made for components.
     *
     * @param behavior
     * @return an Behavior. For most implementations, the same one as was passed in.
     */
    Behavior newBehavior(Behavior behavior);
}
