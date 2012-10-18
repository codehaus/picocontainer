/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Emjection;
import org.picocontainer.NameBinding;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.monitors.NullComponentMonitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Injection will happen through a constructor for the component.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Zohar Melamed
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public class ConstructorInjector<T> extends SingleMemberInjector<T> {
	
	private transient List<Constructor<T>> sortedMatchingConstructors;
    private transient ThreadLocalCyclicDependencyGuard<T> instantiationGuard;
    private boolean rememberChosenConstructor = true;
    private transient CtorAndAdapters<T> chosenConstructor;
    private boolean enableEmjection = false;
    private boolean allowNonPublicClasses = false;

    /**
     * Constructor injector that uses no monitor and no lifecycle adapter.  This is a more
     * convenient constructor for use when instantiating a constructor injector directly.
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters used for initialization
     */
    public ConstructorInjector(final Object componentKey, final Class<?> componentImplementation, Parameter... parameters) {
        this(componentKey, componentImplementation, parameters, new NullComponentMonitor(), false);
    }

    /**
     * Creates a ConstructorInjector
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the component monitor used by this addAdapter
     * @param useNames                use argument names when looking up dependencies
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public ConstructorInjector(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor,
                               boolean useNames) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, useNames);
    }

    /**
     * Creates a ConstructorInjector
     *
     * @param componentKey            the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters              the parameters to use for the initialization
     * @param monitor                 the component monitor used by this addAdapter
     * @param useNames                use argument names when looking up dependencies
     * @param rememberChosenCtor      remember the chosen constructor (to speed up second/subsequent calls)
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException
     *                              if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    public ConstructorInjector(final Object componentKey, final Class componentImplementation, Parameter[] parameters, ComponentMonitor monitor,
                               boolean useNames, boolean rememberChosenCtor) throws  NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, monitor, useNames);
        this.rememberChosenConstructor = rememberChosenCtor;
    }

    private CtorAndAdapters<T> getGreediestSatisfiableConstructor(PicoContainer guardedContainer, @SuppressWarnings("unused") Class<? extends T> componentImplementation) {
        CtorAndAdapters<T> ctor = null;
        try {
            if (chosenConstructor == null) {
                ctor = getGreediestSatisfiableConstructor(guardedContainer);
            }
            if (rememberChosenConstructor) {
                if (chosenConstructor == null) {
                    chosenConstructor = ctor;
                } else {
                    ctor = chosenConstructor;
                }
            }
        } catch (AmbiguousComponentResolutionException e) {
            e.setComponent(getComponentImplementation());
            throw e;
        }
        return ctor;
    }

    @SuppressWarnings("synthetic-access")
    protected CtorAndAdapters<T> getGreediestSatisfiableConstructor(PicoContainer container) throws PicoCompositionException {
        final Set<Constructor> conflicts = new HashSet<Constructor>();
        final Set<Type> unsatisfiableDependencyTypes = new HashSet<Type>();
        final Map<ResolverKey, Parameter.Resolver> resolvers = new HashMap<ResolverKey, Parameter.Resolver>();
        if (sortedMatchingConstructors == null) {
            sortedMatchingConstructors = getSortedMatchingConstructors();
        }
        Constructor<T> greediestConstructor = null;
        Parameter[] greediestConstructorsParameters = null;
        ComponentAdapter[] greediestConstructorsParametersComponentAdapters = null;
        int lastSatisfiableConstructorSize = -1;
        Type unsatisfiedDependency = null;
        Constructor unsatisfiedConstructor = null;
        for (final Constructor<T> sortedMatchingConstructor : sortedMatchingConstructors) {
            try {
                boolean failedDependency = false;
                Type[] parameterTypes = sortedMatchingConstructor.getGenericParameterTypes();
                fixGenericParameterTypes(sortedMatchingConstructor, parameterTypes);
                Annotation[] bindings = getBindings(sortedMatchingConstructor.getParameterAnnotations());
                final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes.length);
                final ComponentAdapter<?>[] currentAdapters = new ComponentAdapter<?>[currentParameters.length];
                // remember: all constructors with less arguments than the given parameters are filtered out already
                for (int j = 0; j < currentParameters.length; j++) {
                    // check whether this constructor is satisfiable
                    Type expectedType = box(parameterTypes[j]);
                    NameBinding expectedNameBinding = new ParameterNameBinding(getParanamer(), sortedMatchingConstructor, j);
                    ResolverKey resolverKey = new ResolverKey(expectedType, useNames() ? expectedNameBinding.getName() : null, useNames(), bindings[j], currentParameters[j]);
                    Parameter.Resolver resolver = resolvers.get(resolverKey);
                    if (resolver == null) {
                        resolver = currentParameters[j].resolve(container, this, null, expectedType, expectedNameBinding, useNames(), bindings[j]);
                        resolvers.put(resolverKey, resolver);
                    }
                    if (resolver.isResolved()) {
                        currentAdapters[j] = resolver.getComponentAdapter();
                        continue;
                    }
                    unsatisfiableDependencyTypes.add(expectedType);
                    unsatisfiedDependency = box(parameterTypes[j]);
                    unsatisfiedConstructor = sortedMatchingConstructor;
                    failedDependency = true;
                }

                if (greediestConstructor != null && parameterTypes.length != lastSatisfiableConstructorSize) {
                    if (conflicts.isEmpty()) {
                        // we found our match [aka. greedy and satisfied]
                        return new CtorAndAdapters<T>(greediestConstructor, greediestConstructorsParameters, greediestConstructorsParametersComponentAdapters);
                    }
                    // fits although not greedy
                    conflicts.add(sortedMatchingConstructor);
                } else if (!failedDependency && lastSatisfiableConstructorSize == parameterTypes.length) {
                    // satisfied and same size as previous one?
                    conflicts.add(sortedMatchingConstructor);
                    conflicts.add(greediestConstructor);
                } else if (!failedDependency) {
                    greediestConstructor = sortedMatchingConstructor;
                    greediestConstructorsParameters = currentParameters;
                    greediestConstructorsParametersComponentAdapters = currentAdapters;
                    lastSatisfiableConstructorSize = parameterTypes.length;
                }
            } catch (AmbiguousComponentResolutionException e) {
                // embellish with the constructor being injected into.
                e.setMember(sortedMatchingConstructor);
                throw e;
            }
        }
        if (!conflicts.isEmpty()) {
            throw new PicoCompositionException(conflicts.size() + " satisfiable constructors is too many for '"+getComponentImplementation()+"'. Constructor List:" + conflicts.toString().replace(getComponentImplementation().getName(),"<init>").replace("public <i","<i"));
        } else if (greediestConstructor == null && !unsatisfiableDependencyTypes.isEmpty()) {
            throw new UnsatisfiableDependenciesException(this.getComponentImplementation().getName()
                    + " has unsatisfied dependency '" + unsatisfiedDependency
                    + "' for constructor '" + unsatisfiedConstructor + "'" + " from " + container);
        } else if (greediestConstructor == null) {
            // be nice to the user, show all constructors that were filtered out
            final Set<Constructor> nonMatching = new HashSet<Constructor>();
            for (Constructor constructor : getConstructors()) {
                nonMatching.add(constructor);
            }
            throw new PicoCompositionException("Either the specified parameters do not match any of the following constructors: " + nonMatching.toString() + "; OR the constructors were not accessible for '" + getComponentImplementation().getName() + "'");
        }
        return new CtorAndAdapters<T>(greediestConstructor, greediestConstructorsParameters, greediestConstructorsParametersComponentAdapters);
    }

    private String toList(Set<Type> unsatisfiableDependencyTypes) {
        StringBuilder sb = new StringBuilder();
        Iterator<Type> it = unsatisfiableDependencyTypes.iterator();
        while (it.hasNext()) {
            Type next = it.next();
            sb.append(next.toString().replace("class ", ""));
            sb.append(", ");
        }
        String s = sb.toString();
        return s.substring(0, s.lastIndexOf(", "));
    }

    public void enableEmjection(boolean enableEmjection) {
        this.enableEmjection = enableEmjection;
    }


    public ConstructorInjector<T> withNonPublicConstructors() {
        allowNonPublicClasses = true;
        return this;
    }

    private static final class ResolverKey {
        private final Type expectedType;
        private final String pName;
        private final boolean useNames;
        private final Annotation binding;
        private final Parameter currentParameter;

        private ResolverKey(Type expectedType, String pName, boolean useNames, Annotation binding, Parameter currentParameter) {
            this.expectedType = expectedType;
            this.pName = pName;
            this.useNames = useNames;
            this.binding = binding;
            this.currentParameter = currentParameter;
        }

        // Generated by IDEA
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ResolverKey that = (ResolverKey) o;

            if (useNames != that.useNames) return false;
            if (binding != null ? !binding.equals(that.binding) : that.binding != null) return false;
            if (!currentParameter.equals(that.currentParameter)) return false;
            if (!expectedType.equals(that.expectedType)) return false;
            if (pName != null ? !pName.equals(that.pName) : that.pName != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            result = expectedType.hashCode();
            result = 31 * result + (pName != null ? pName.hashCode() : 0);
            result = 31 * result + (useNames ? 1 : 0);
            result = 31 * result + (binding != null ? binding.hashCode() : 0);
            result = 31 * result + currentParameter.hashCode();
            return result;
        }
    }

    private void fixGenericParameterTypes(Constructor<T> ctor, Type[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++) {
            Type parameterType = parameterTypes[i];
            if (parameterType instanceof TypeVariable) {
                parameterTypes[i] = ctor.getParameterTypes()[i];
            }
        }
    }

    protected class CtorAndAdapters<TYPE> {
        private final Constructor<TYPE> ctor;
        private final Parameter[] constructorParameters;
        private final ComponentAdapter[] injecteeAdapters;

        public CtorAndAdapters(Constructor<TYPE> ctor, Parameter[] parameters, ComponentAdapter[] injecteeAdapters) {
            this.ctor = ctor;
            this.constructorParameters = parameters;
            this.injecteeAdapters = injecteeAdapters;
        }

        public Constructor<TYPE> getConstructor() {
            return ctor;
        }

        public Object[] getParameterArguments(PicoContainer container) {
            Type[] parameterTypes = ctor.getGenericParameterTypes();
            // as per fixParameterType()
            for (int i = 0; i < parameterTypes.length; i++) {
                Type parameterType = parameterTypes[i];
                if (parameterType instanceof TypeVariable) {
                    parameterTypes[i] = ctor.getParameterTypes()[i];
                }
            }
            boxParameters(parameterTypes);            
            Object[] result = new Object[constructorParameters.length];
            Annotation[] bindings = getBindings(ctor.getParameterAnnotations());
            for (int i = 0; i < constructorParameters.length; i++) {

                result[i] = getParameter(container, ctor, i, parameterTypes[i],
                        bindings[i], constructorParameters[i], injecteeAdapters[i]);
            }
            return result;
        }

        public ComponentAdapter[] getInjecteeAdapters() {
            return injecteeAdapters;
        }

        public Parameter[] getParameters() {
            return constructorParameters;
        }
    }

    @Override
    public T getComponentInstance(final PicoContainer container, @SuppressWarnings("unused") Type into) throws PicoCompositionException {
        if (instantiationGuard == null) {
            instantiationGuard = new ThreadLocalCyclicDependencyGuard<T>() {
                @Override
                @SuppressWarnings("synthetic-access")
                public T run(Object instance) {
                    CtorAndAdapters<T> ctorAndAdapters = getGreediestSatisfiableConstructor(guardedContainer, getComponentImplementation());
                    ComponentMonitor componentMonitor = currentMonitor();
                    Constructor<T> ctor = ctorAndAdapters.getConstructor();
                    try {
                        Object[] ctorParameters = ctorAndAdapters.getParameterArguments(guardedContainer);
                        ctor = componentMonitor.instantiating(container, ConstructorInjector.this, ctor);
                        if(ctorAndAdapters == null) {
                            throw new NullPointerException("Component Monitor " + componentMonitor 
                                            + " returned a null constructor from method 'instantiating' after passing in " + ctorAndAdapters);
                        }
                        long startTime = System.currentTimeMillis();
                        T inst = newInstance(ctor, ctorParameters);
                        componentMonitor.instantiated(container, ConstructorInjector.this,
                                ctor, inst, ctorParameters, System.currentTimeMillis() - startTime);
                        return inst;
                    } catch (InvocationTargetException e) {
                        componentMonitor.instantiationFailed(container, ConstructorInjector.this, ctor, e);
                        if (e.getTargetException() instanceof RuntimeException) {
                            throw (RuntimeException) e.getTargetException();
                        } else if (e.getTargetException() instanceof Error) {
                            throw (Error) e.getTargetException();
                        }
                        throw new PicoCompositionException(e.getTargetException());
                    } catch (InstantiationException e) {
                        return caughtInstantiationException(componentMonitor, ctor, e, container);
                    } catch (IllegalAccessException e) {
                        return caughtIllegalAccessException(componentMonitor, ctor, e, container);

                    }
                }
            };
        }
        instantiationGuard.setGuardedContainer(container);
        T inst = instantiationGuard.observe(getComponentImplementation(), null);
        decorate(inst, container);
        return inst;
    }

    private void decorate(T inst, PicoContainer container) {
        if (enableEmjection) {
            Emjection.setupEmjection(inst, container);
        }
    }

    private List<Constructor<T>> getSortedMatchingConstructors() {
        List<Constructor<T>> matchingConstructors = new ArrayList<Constructor<T>>();
        Constructor<T>[] allConstructors = getConstructors();
        // filter out all constructors that will definately not match
        for (Constructor<T> constructor : allConstructors) {
            int modifiers = constructor.getModifiers();
            if ((parameters == null || constructor.getParameterTypes().length == parameters.length)
                    && (allowNonPublicClasses || (modifiers & Modifier.PUBLIC) != 0)) {
                if ((modifiers & Modifier.PUBLIC) == 0) {
                    constructor.setAccessible(true);
                }
                matchingConstructors.add(constructor);
            }
        }
        // optimize list of constructors moving the longest at the beginning
        if (parameters == null) {        	
            Collections.sort(matchingConstructors, new Comparator<Constructor>() {
                public int compare(Constructor arg0, Constructor arg1) {
                    return arg1.getParameterTypes().length - arg0.getParameterTypes().length;
                }
            });
        }
        return matchingConstructors;
    }

    private Constructor<T>[] getConstructors() {
        return AccessController.doPrivileged(new PrivilegedAction<Constructor<T>[]>() {
            public Constructor<T>[] run() {
                return (Constructor<T>[]) getComponentImplementation().getDeclaredConstructors();
            }
        });
    }

    @Override
    public void verify(final PicoContainer container) throws PicoCompositionException {
        if (verifyingGuard == null) {
            verifyingGuard = new ThreadLocalCyclicDependencyGuard() {
                @Override
                public Object run(Object instance) {
                    final Constructor constructor = getGreediestSatisfiableConstructor(guardedContainer).getConstructor();
                    final Class[] parameterTypes = constructor.getParameterTypes();
                    final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes.length);
                    for (int i = 0; i < currentParameters.length; i++) {
                        currentParameters[i].verify(container, ConstructorInjector.this, box(parameterTypes[i]),
                            new ParameterNameBinding(getParanamer(),  constructor, i),
                                useNames(), getBindings(constructor.getParameterAnnotations())[i]);
                    }
                    return null;
                }
            };
        }
        verifyingGuard.setGuardedContainer(container);
        verifyingGuard.observe(getComponentImplementation(), null);
    }

    @Override
    public String getDescriptor() {
        return "ConstructorInjector-";
    }


}
