/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.PropertyApplicator;
import org.picocontainer.behaviors.Cached;

import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Instantiates components using empty constructors and
 * <a href="http://picocontainer.org/setter-injection.html">Setter Injection</a>.
 * For easy setting of primitive properties, also see {@link PropertyApplicator}.
 * <p/>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link Cached} around this one.
 * </em>
 * </p>
 *
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class SetterInjector<T> extends IterativeInjector<T> {

    protected final String prefix;
    private final boolean optional;
    private final String notThisOneThough;

    /**
     * Constructs a SetterInjector
     *
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the component monitor used by this addAdapter
     * @param prefix                  the prefix to use (e.g. 'set')
     * @param notThisOneThough        a setter name that's not for injecting through
     * @param optional                not all setters need to be injected
     * @param useNames @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjector(final Object componentKey,
                          final Class componentImplementation,
                          Parameter[] parameters,
                          ComponentMonitor monitor,
                          String prefix, String notThisOneThough,
                          boolean optional, boolean useNames) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, useNames);
        this.prefix = prefix;
        this.optional = optional;
        this.notThisOneThough = notThisOneThough != null ? notThisOneThough : "";
    }

    protected Object memberInvocationReturn(Object lastReturn, AccessibleObject member, Object instance) {
        return member != null && ((Method)member).getReturnType()!=void.class ? lastReturn : instance;
    }

    @Override
    protected Object injectIntoMember(AccessibleObject member, Object componentInstance, Object toInject)
        throws IllegalAccessException, InvocationTargetException {
        return ((Method)member).invoke(componentInstance, toInject);
    }

    @Override
    protected boolean isInjectorMethod(Method method) {
        String methodName = method.getName();
        return methodName.length() >= getInjectorPrefix().length() + 1 // long enough
                && methodName.startsWith(getInjectorPrefix())
                && !methodName.equals(notThisOneThough)
                && Character.isUpperCase(methodName.charAt(getInjectorPrefix().length()));        
    }

    protected String getInjectorPrefix() {
        return prefix;
    }

    @Override
    public String getDescriptor() {
        return "SetterInjector-"; 
    }

    @Override
    protected void unsatisfiedDependencies(PicoContainer container, Set<Type> unsatisfiableDependencyTypes) {
        if (!optional) {
            super.unsatisfiedDependencies(container, unsatisfiableDependencyTypes);
        }
    }

}
