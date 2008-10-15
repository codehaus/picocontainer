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
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.behaviors.PropertyApplicator;
import org.picocontainer.behaviors.Cached;

import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;

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

    private final String setterMethodPrefix;

    /**
     * Constructs a SetterInjector
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the component monitor used by this addAdapter
     * @param lifecycleStrategy       the component lifecycle strategy used by this addAdapter
     * @param setterMethodPrefix
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjector(final Object componentKey,
                          final Class componentImplementation,
                          Parameter[] parameters,
                          ComponentMonitor monitor,
                          LifecycleStrategy lifecycleStrategy,
                          String setterMethodPrefix, boolean useNames) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy, useNames);
        this.setterMethodPrefix = setterMethodPrefix;
    }

    protected void injectIntoMember(AccessibleObject member, Object componentInstance, Object toInject)
        throws IllegalAccessException, InvocationTargetException {
        ((Method)member).invoke(componentInstance, toInject);
    }

    protected boolean isInjectorMethod(Method method) {
        String methodName = method.getName();
        return methodName.length() >= getInjectorPrefix().length() + 1 && methodName.startsWith(getInjectorPrefix()) && Character.isUpperCase(methodName.charAt(getInjectorPrefix().length()));
    }

    protected String getInjectorPrefix() {
        return setterMethodPrefix;
    }

    public String getDescriptor() {
        return "SetterInjector-"; 
    }


}
