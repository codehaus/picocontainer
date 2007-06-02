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
import org.picocontainer.ParameterName;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.adapters.InjectingAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;

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
 * use a {@link org.picocontainer.adapters.CachingBehaviorAdapter} around this one.
 * </em>
 * </p>
 *
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Paul Hammant
 * @version $Revision$
 */
public class SetterInjectionAdapter extends InjectingAdapter {
    private transient ThreadLocalCyclicDependencyGuard instantiationGuard;
    private transient List<Method> injectionMethods;
    private transient Class[] injectionTypes;

    /**
     * Constructs a SetterInjectionComponentAdapter
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the addComponent monitor used by this addAdapter
     * @param lifecycleStrategy       the addComponent lifecycle strategy used by this addAdapter
     * @throws org.picocontainer.defaults.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjectionAdapter(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy);
    }


    /**
     * Constructs a SetterInjectionComponentAdapter
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the addComponent monitor used by this addAdapter
     * @throws NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjectionAdapter(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor);
    }

    /**
     * Constructs a SetterInjectionComponentAdapter with a {@link org.picocontainer.monitors.DelegatingComponentMonitor} as default.
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @throws NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public SetterInjectionAdapter(final Serializable componentKey, final Class componentImplementation, Parameter... parameters) throws NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters);
    }


    protected Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws PicoIntrospectionException, AmbiguousComponentResolutionException, NotConcreteRegistrationException {
        final Constructor constructor = getConstructor();
        getMatchingParameterListForSetters(container);
        return constructor;
    }

    private Constructor getConstructor()  {
        Object retVal = AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    return getComponentImplementation().getConstructor((Class[])null);
                } catch (NoSuchMethodException e) {
                    return new PicoInitializationException(e);
                } catch (SecurityException e) {
                    return new PicoInitializationException(e);
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
        if (injectionMethods == null) {
            initializeInjectionMethodsAndTypeLists();
        }

        final List<Object> matchingParameterList = new ArrayList<Object>(Collections.nCopies(injectionMethods.size(), null));
        final Set<Integer> nonMatchingParameterPositions = new HashSet<Integer>();
        final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(injectionTypes);
        for (int i = 0; i < currentParameters.length; i++) {
            final Parameter parameter = currentParameters[i];
            boolean failedDependency = true;
            for (int j = 0; j < injectionTypes.length; j++) {
                if (matchingParameterList.get(j) == null && parameter.isResolvable(container, this, injectionTypes[j], new ParameterName() {
                    public String getParameterName() {
                        return ""; // TODO 
                    }
                })) {
                    matchingParameterList.set(j, parameter);
                    failedDependency = false;
                    break;
                }
                int y = 0;
            }
            if (failedDependency) {
                nonMatchingParameterPositions.add(i);
            }
        }

        final Set<Class> unsatisfiableDependencyTypes = new HashSet<Class>();
        for (int i = 0; i < matchingParameterList.size(); i++) {
            if (matchingParameterList.get(i) == null) {
                unsatisfiableDependencyTypes.add(injectionTypes[i]);
            }
        }
        if (unsatisfiableDependencyTypes.size() > 0) {
            throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes, container);
        } else if (nonMatchingParameterPositions.size() > 0) {
            throw new PicoInitializationException("Following parameters do not match any of the injectionMethods for " + getComponentImplementation() + ": " + nonMatchingParameterPositions.toString());
        }
        return matchingParameterList.toArray(new Parameter[matchingParameterList.size()]);
    }

    public Object getComponentInstance(final PicoContainer container) throws PicoInitializationException, PicoIntrospectionException, NotConcreteRegistrationException {
        final Constructor constructor = getConstructor();
        if (instantiationGuard == null) {
            instantiationGuard = new ThreadLocalCyclicDependencyGuard() {
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
                        throw new PicoInitializationException(e.getTargetException());
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
                    Object injected[] = new Object[injectionMethods.size()];
                    try {
                        for (int i = 0; i < injectionMethods.size(); i++) {
                            setter = injectionMethods.get(i);
                            componentMonitor.invoking(setter, componentInstance);
                            Object toInject = matchingParameters[i].resolveInstance(guardedContainer, SetterInjectionAdapter.this, injectionTypes[i], new ParameterName() {
                                public String getParameterName() {
                                    return ""; // TODO
                                }
                            });
                            setter.invoke(componentInstance, toInject);
                            injected[i] = toInject;
                        }
                        componentMonitor.instantiated(constructor, componentInstance, injected, System.currentTimeMillis() - startTime);
                        return componentInstance;
                    } catch (InvocationTargetException e) {
                        componentMonitor.invocationFailed(setter, componentInstance, e);
                        if (e.getTargetException() instanceof RuntimeException) {
                            throw (RuntimeException) e.getTargetException();
                        } else if (e.getTargetException() instanceof Error) {
                            throw (Error) e.getTargetException();
                        }
                        throw new PicoInitializationException(e.getTargetException());
                    } catch (IllegalAccessException e) {
                        componentMonitor.invocationFailed(setter, componentInstance, e);
                        throw new PicoInitializationException(e);
                    }

                }
            };
        }
        instantiationGuard.setGuardedContainer(container);
        return instantiationGuard.observe(getComponentImplementation());
    }

    public void verify(final PicoContainer container) throws PicoIntrospectionException {
        if (verifyingGuard == null) {
            verifyingGuard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    final Parameter[] currentParameters = getMatchingParameterListForSetters(guardedContainer);
                    for (int i = 0; i < currentParameters.length; i++) {
                        currentParameters[i].verify(container, SetterInjectionAdapter.this, injectionTypes[i], new ParameterName() {
                            public String getParameterName() {
                                return ""; // TODO
                            }
                        });
                    }
                    return null;
                }
            };
        }
        verifyingGuard.setGuardedContainer(container);
        verifyingGuard.observe(getComponentImplementation());
    }

    private void initializeInjectionMethodsAndTypeLists() {
        injectionMethods = new ArrayList<Method>();
        final List<Class> typeList = new ArrayList<Class>();
        final Method[] methods = getMethods();
        for (final Method method : methods) {
            final Class[] parameterTypes = method.getParameterTypes();
            // We're only interested if there is only one parameter and the method name is bean-style.
            if (parameterTypes.length == 1) {
                boolean isInjector = isInjectorMethod(method);
                if (isInjector) {
                    injectionMethods.add(method);
                    typeList.add(parameterTypes[0]);
                }
            }
        }
        injectionTypes = typeList.toArray(new Class[0]);
    }

    protected boolean isInjectorMethod(Method method) {
        String methodName = method.getName();
        return methodName.length() >= getInjectorPrefix().length() + 1 && methodName.startsWith(getInjectorPrefix()) && Character.isUpperCase(methodName.charAt(getInjectorPrefix().length()));
    }

    protected String getInjectorPrefix() {
        return "set";
    }

    private Method[] getMethods() {
        return (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return getComponentImplementation().getMethods();
            }
        });
    }
}
