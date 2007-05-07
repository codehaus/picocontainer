/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.adapters;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.adapters.InstantiatingComponentAdapter;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

/**
 * Instantiates components using empty constructors and
 * <a href="http://docs.codehaus.org/display/PICO/Setter+Injection">Setter Injection</a>.
 * For easy setting of primitive properties, also see {@link org.picocontainer.adapters.BeanPropertyComponentAdapter}.
 * <p/>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link org.picocontainer.adapters.CachingComponentAdapter} around this one.
 * </em>
 * </p>
 *
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @version $Revision$
 */
public class SetterInjectionComponentAdapter extends InstantiatingComponentAdapter {
    private String prefix = "set";
    private transient Guard instantiationGuard;
    private transient List<Method> setters;
    private transient Class[] setterTypes;

    /**
     * Constructs a SetterInjectionComponentAdapter
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the addComponent monitor used by this addAdapter
     * @param lifecycleStrategy       the addComponent lifecycle strategy used by this addAdapter
     * @throws org.picocontainer.defaults.AssignabilityRegistrationException
     *                              if the key is a type and the implementation cannot be assigned to.
     * @throws org.picocontainer.defaults.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjectionComponentAdapter(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy);
    }


    /**
     * Constructs a SetterInjectionComponentAdapter
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the addComponent monitor used by this addAdapter
     * @throws AssignabilityRegistrationException
     *                              if the key is a type and the implementation cannot be assigned to.
     * @throws NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjectionComponentAdapter(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor);
    }

    /**
     * Constructs a SetterInjectionComponentAdapter with a {@link org.picocontainer.defaults.DelegatingComponentMonitor} as default.
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @throws AssignabilityRegistrationException
     *                              if the key is a type and the implementation cannot be assigned to.
     * @throws NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjectionComponentAdapter(final Serializable componentKey, final Class componentImplementation, Parameter... parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters);
    }


    protected Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        final Constructor constructor = getConstructor();
        getMatchingParameterListForSetters(container);
        return constructor;
    }

    private Constructor getConstructor() throws PicoInvocationTargetInitializationException {
        Object retVal = AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    return getComponentImplementation().getConstructor((Class[])null);
                } catch (NoSuchMethodException e) {
                    return new PicoInvocationTargetInitializationException(e);
                } catch (SecurityException e) {
                    return new PicoInvocationTargetInitializationException(e);
                }
            }
        });
        if (retVal instanceof Constructor) {
            return (Constructor) retVal;
        } else {
            throw (PicoInitializationException) retVal;
        }
    }

    private Parameter[] getMatchingParameterListForSetters(PicoContainer container) throws PicoInitializationException, UnsatisfiableDependenciesException {
        if (setters == null) {
            initializeSetterAndTypeLists();
        }

        final List<Object> matchingParameterList = new ArrayList<Object>(Collections.nCopies(setters.size(), null));
        final Set<Integer> nonMatchingParameterPositions = new HashSet<Integer>();
        final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(setterTypes);
        for (int i = 0; i < currentParameters.length; i++) {
            final Parameter parameter = currentParameters[i];
            boolean failedDependency = true;
            for (int j = 0; j < setterTypes.length; j++) {
                if (matchingParameterList.get(j) == null && parameter.isResolvable(container, this, setterTypes[j])) {
                    matchingParameterList.set(j, parameter);
                    failedDependency = false;
                    break;
                }
            }
            if (failedDependency) {
                nonMatchingParameterPositions.add(i);
            }
        }

        final Set<Class> unsatisfiableDependencyTypes = new HashSet<Class>();
        for (int i = 0; i < matchingParameterList.size(); i++) {
            if (matchingParameterList.get(i) == null) {
                unsatisfiableDependencyTypes.add(setterTypes[i]);
            }
        }
        if (unsatisfiableDependencyTypes.size() > 0) {
            throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes, container);
        } else if (nonMatchingParameterPositions.size() > 0) {
            throw new PicoInitializationException("Following parameters do not match any of the setters for " + getComponentImplementation() + ": " + nonMatchingParameterPositions.toString());
        }
        return (Parameter[]) matchingParameterList.toArray(new Parameter[matchingParameterList.size()]);
    }

    public Object getComponentInstance(final PicoContainer container) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        final Constructor constructor = getConstructor();
        if (instantiationGuard == null) {
            instantiationGuard = new Guard() {
                public Object run() {
                    final Parameter[] matchingParameters = getMatchingParameterListForSetters(guardedContainer);
                    ComponentMonitor componentMonitor = currentMonitor();
                    Object componentInstance;
                    long startTime = System.currentTimeMillis();
                    try {
                        componentMonitor.instantiating(constructor);
                        componentInstance = newInstance(constructor, null);
                    } catch (InvocationTargetException e) {
                        componentMonitor.instantiationFailed(constructor, e);
                        if (e.getTargetException() instanceof RuntimeException) {
                            throw (RuntimeException) e.getTargetException();
                        } else if (e.getTargetException() instanceof Error) {
                            throw (Error) e.getTargetException();
                        }
                        throw new PicoInvocationTargetInitializationException(e.getTargetException());
                    } catch (InstantiationException e) {
                        // can't get here because checkConcrete() will catch it earlier, but see PICO-191
                        ///CLOVER:OFF
                        componentMonitor.instantiationFailed(constructor, e);
                        throw new PicoInitializationException("Should never get here");
                        ///CLOVER:ON
                    } catch (IllegalAccessException e) {
                        // can't get here because either filtered or access mode set
                        ///CLOVER:OFF
                        componentMonitor.instantiationFailed(constructor, e);
                        throw new PicoInitializationException(e);
                        ///CLOVER:ON
                    }
                    Method setter = null;
                    Object injected[] = new Object[setters.size()];
                    try {
                        for (int i = 0; i < setters.size(); i++) {
                            setter = setters.get(i);
                            componentMonitor.invoking(setter, componentInstance);
                            Object toInject = matchingParameters[i].resolveInstance(guardedContainer, SetterInjectionComponentAdapter.this, setterTypes[i]);
                            setter.invoke(componentInstance, toInject);
                            injected[i] = toInject;
                            //componentMonitor.invoked(setter, componentInstance, System.currentTimeMillis() - startTime);
                        }
                        componentMonitor.instantiated(constructor, componentInstance, injected, System.currentTimeMillis() - startTime);
                        return componentInstance;
                    } catch (InvocationTargetException e) {
                        //componentMonitor.invocationFailed(setter, componentInstance, e);
                        if (e.getTargetException() instanceof RuntimeException) {
                            throw (RuntimeException) e.getTargetException();
                        } else if (e.getTargetException() instanceof Error) {
                            throw (Error) e.getTargetException();
                        }
                        throw new PicoInvocationTargetInitializationException(e.getTargetException());
                    } catch (IllegalAccessException e) {
                        //componentMonitor.invocationFailed(setter, componentInstance, e);
                        throw new PicoInvocationTargetInitializationException(e);
                    }

                }
            };
        }
        instantiationGuard.setArguments(container);
        return instantiationGuard.observe(getComponentImplementation());
    }

    public void verify(final PicoContainer container) throws PicoIntrospectionException {
        if (verifyingGuard == null) {
            verifyingGuard = new Guard() {
                public Object run() {
                    final Parameter[] currentParameters = getMatchingParameterListForSetters(guardedContainer);
                    for (int i = 0; i < currentParameters.length; i++) {
                        currentParameters[i].verify(container, SetterInjectionComponentAdapter.this, setterTypes[i]);
                    }
                    return null;
                }
            };
        }
        verifyingGuard.setArguments(container);
        verifyingGuard.observe(getComponentImplementation());
    }

    private void initializeSetterAndTypeLists() {
        setters = new ArrayList<Method>();
        final List<Class> typeList = new ArrayList<Class>();
        final Method[] methods = getMethods();
        for (final Method method : methods) {
            final Class[] parameterTypes = method.getParameterTypes();
            // We're only interested if there is only one parameter and the method name is bean-style.
            if (parameterTypes.length == 1) {
                String methodName = method.getName();
                boolean isBeanStyle = methodName.length() >= prefix.length() + 1 && methodName.startsWith(prefix) && Character.isUpperCase(methodName.charAt(prefix.length()));
                if (isBeanStyle) {
                    setters.add(method);
                    typeList.add(parameterTypes[0]);
                }
            }
        }
        setterTypes = typeList.toArray(new Class[0]);
    }

    private Method[] getMethods() {
        return (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return getComponentImplementation().getMethods();
            }
        });
    }
}
