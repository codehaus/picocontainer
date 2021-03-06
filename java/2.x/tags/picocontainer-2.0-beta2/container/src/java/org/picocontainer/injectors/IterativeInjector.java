package org.picocontainer.injectors;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.ParameterName;

import java.lang.reflect.Member;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class IterativeInjector extends AbstractInjector {
    private transient ThreadLocalCyclicDependencyGuard instantiationGuard;
    protected transient List<Member> injectionMembers;
    protected transient Class[] injectionTypes;

    /**
     * Constructs a IterativeInjector
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the component monitor used by this addAdapter
     * @param lifecycleStrategy       the component lifecycle strategy used by this addAdapter
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public IterativeInjector(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy);
    }


    protected Constructor getConstructor()  {
        Object retVal = AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    return getComponentImplementation().getConstructor((Class[])null);
                } catch (NoSuchMethodException e) {
                    return new PicoCompositionException(e);
                } catch (SecurityException e) {
                    return new PicoCompositionException(e);
                }
            }
        });
        if (retVal instanceof Constructor) {
            return (Constructor) retVal;
        } else {
            throw (PicoCompositionException) retVal;
        }
    }

    private Parameter[] getMatchingParameterListForSetters(PicoContainer container) throws PicoCompositionException {
        if (injectionMembers == null) {
            initializeInjectionMembersAndTypeLists();
        }

        final List<Object> matchingParameterList = new ArrayList<Object>(Collections.nCopies(injectionMembers.size(), null));
        final Set<Integer> nonMatchingParameterPositions = new HashSet<Integer>();
        final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(injectionTypes);
        for (int i = 0; i < currentParameters.length; i++) {
            final Parameter parameter = currentParameters[i];
            boolean failedDependency = true;
            for (int j = 0; j < injectionTypes.length; j++) {
                if (matchingParameterList.get(j) == null && parameter.isResolvable(container, this, injectionTypes[j],
                                                                                   new IterativeInjectorParameterName())) {
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
                unsatisfiableDependencyTypes.add(injectionTypes[i]);
            }
        }
        if (unsatisfiableDependencyTypes.size() > 0) {
            unsatisfiedDependencies(container, unsatisfiableDependencyTypes);
        } else if (nonMatchingParameterPositions.size() > 0) {
            throw new PicoCompositionException("Following parameters do not match any of the injectionMembers for " + getComponentImplementation() + ": " + nonMatchingParameterPositions.toString());
        }
        return matchingParameterList.toArray(new Parameter[matchingParameterList.size()]);
    }

    protected void unsatisfiedDependencies(PicoContainer container, Set<Class> unsatisfiableDependencyTypes) {
        throw new UnsatisfiableDependenciesException(this, null, unsatisfiableDependencyTypes, container);
    }

    public Object getComponentInstance(final PicoContainer container) throws PicoCompositionException {
        final Constructor constructor = getConstructor();
        if (instantiationGuard == null) {
            instantiationGuard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    final Parameter[] matchingParameters = getMatchingParameterListForSetters(guardedContainer);
                    ComponentMonitor componentMonitor = currentMonitor();
                    Object componentInstance;

                    componentInstance = getOrMakeInstance(container, constructor, componentMonitor);
                    Member member = null;
                    Object injected[] = new Object[injectionMembers.size()];
                    try {
                        for (int i = 0; i < injectionMembers.size(); i++) {
                            member = injectionMembers.get(i);
                            componentMonitor.invoking(container, IterativeInjector.this, member, componentInstance);
                            if (matchingParameters[i] == null) {
                                continue;
                            }
                            Object toInject = matchingParameters[i].resolveInstance(guardedContainer, IterativeInjector.this, injectionTypes[i],
                                                                                    new IterativeInjectorParameterName());
                            injectIntoMember(member, componentInstance, toInject);
                            injected[i] = toInject;
                        }
                        return componentInstance;
                    } catch (InvocationTargetException e) {
                        return caughtInvocationTargetException(componentMonitor, member, componentInstance, e);
                    } catch (IllegalAccessException e) {
                        return caughtIllegalAccessException(componentMonitor, member, componentInstance, e);
                    }

                }
            };
        }
        instantiationGuard.setGuardedContainer(container);
        return instantiationGuard.observe(getComponentImplementation());
    }

    protected Object getOrMakeInstance(PicoContainer container,
                                       Constructor constructor,
                                       ComponentMonitor componentMonitor) {
        long startTime = System.currentTimeMillis();
        Constructor constructorToUse = componentMonitor.instantiating(container,
                                                                      IterativeInjector.this, constructor);
        Object componentInstance;
        try {
            componentInstance = newInstance(constructorToUse, null);
        } catch (InvocationTargetException e) {
            componentMonitor.instantiationFailed(container, IterativeInjector.this, constructorToUse, e);
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error)e.getTargetException();
            }
            throw new PicoCompositionException(e.getTargetException());
        } catch (InstantiationException e) {
            return caughtInstantiationException(componentMonitor, constructor, e, container);
        } catch (IllegalAccessException e) {
            return caughtIllegalAccessException(componentMonitor, constructor, e, container);
        }
        componentMonitor.instantiated(container,
                                      IterativeInjector.this,
                                      constructorToUse,
                                      componentInstance,
                                      null,
                                      System.currentTimeMillis() - startTime);
        return componentInstance;
    }

    protected void injectIntoMember(Member member, Object componentInstance, Object toInject)
        throws IllegalAccessException, InvocationTargetException {
        ((Method)member).invoke(componentInstance, toInject);
    }

    public void verify(final PicoContainer container) throws PicoCompositionException {
        if (verifyingGuard == null) {
            verifyingGuard = new ThreadLocalCyclicDependencyGuard() {
                public Object run() {
                    final Parameter[] currentParameters = getMatchingParameterListForSetters(guardedContainer);
                    for (int i = 0; i < currentParameters.length; i++) {
                        currentParameters[i].verify(container, IterativeInjector.this, injectionTypes[i],
                                                    new IterativeInjectorParameterName());
                    }
                    return null;
                }
            };
        }
        verifyingGuard.setGuardedContainer(container);
        verifyingGuard.observe(getComponentImplementation());
    }

    protected void initializeInjectionMembersAndTypeLists() {
        injectionMembers = new ArrayList<Member>();
        final List<Class> typeList = new ArrayList<Class>();
        final Method[] methods = getMethods();
        for (final Method method : methods) {
            final Class[] parameterTypes = method.getParameterTypes();
            // We're only interested if there is only one parameter and the method name is bean-style.
            if (parameterTypes.length == 1) {
                boolean isInjector = isInjectorMethod(method);
                if (isInjector) {
                    injectionMembers.add(method);
                    typeList.add(parameterTypes[0]);
                }
            }
        }
        injectionTypes = typeList.toArray(new Class[0]);
    }

    protected boolean isInjectorMethod(Method method) {
        return false;
    }

    private Method[] getMethods() {
        return (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return getComponentImplementation().getMethods();
            }
        });
    }

    private static class IterativeInjectorParameterName implements ParameterName {
        public String getName() {
            return ""; // TODO
        }
    }

}
