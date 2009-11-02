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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ObjectReference;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.parameters.ComponentParameter;

/**
 * This ComponentAdapter will instantiate a new object for each call to
 * {@link org.picocontainer.ComponentAdapter#getComponentInstance(PicoContainer, Type)}.
 * That means that when used with a PicoContainer, getComponent will
 * return a new object each time.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public abstract class AbstractInjector<T> extends AbstractAdapter<T> implements LifecycleStrategy, org.picocontainer.Injector<T> {
    /** The cycle guard for the verification. */
    protected transient ThreadLocalCyclicDependencyGuard verifyingGuard;
    /** The parameters to use for initialization. */
    protected transient Parameter[] parameters;

    /** The strategy used to control the lifecycle */
    protected LifecycleStrategy lifecycleStrategy;
    private final boolean useNames;

    /**
     * Constructs a new ComponentAdapter for the given key and implementation.
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @param monitor the component monitor used by this ComponentAdapter
     * @param lifecycleStrategy the lifecycle strategy used by this ComponentAdapter
     * @throws org.picocontainer.injectors.AbstractInjector.NotConcreteRegistrationException if the implementation is not a concrete class
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected AbstractInjector(final Object componentKey, final Class<?> componentImplementation, final Parameter[] parameters,
                                            final ComponentMonitor monitor, final LifecycleStrategy lifecycleStrategy, final boolean useNames) {
        super(componentKey, componentImplementation, monitor);
        this.useNames = useNames;
        checkConcrete();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if(parameters[i] == null) {
                    throw new NullPointerException("Parameter " + i + " is null");
                }
            }
        }
        this.parameters = parameters;
        this.lifecycleStrategy = lifecycleStrategy;
    }

    public boolean useNames() {
        return useNames;
    }

    private void checkConcrete() throws NotConcreteRegistrationException {
        // Assert that the component class is concrete.
        boolean isAbstract = (getComponentImplementation().getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
        if (getComponentImplementation().isInterface() || isAbstract) {
            throw new NotConcreteRegistrationException(getComponentImplementation());
        }
    }

    /**
     * Create default parameters for the given types.
     *
     * @param parameterTypes the parameter types
     * @return the array with the default parameters.
     */
    protected Parameter[] createDefaultParameters(final Type[] parameterTypes) {
        Parameter[] componentParameters = new Parameter[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            componentParameters[i] = ComponentParameter.DEFAULT;
        }
        return componentParameters;
    }

    @SuppressWarnings("unused") 
    public void verify(PicoContainer container) throws PicoCompositionException {
    }

    @Override
    public T getComponentInstance(PicoContainer container) throws PicoCompositionException {
        return getComponentInstance(container, NOTHING.class);
    }

    public abstract T getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException;

    @SuppressWarnings("unused") 
    public Object decorateComponentInstance(PicoContainer container, Type into, T instance) {
        return null;
    }

    @Override
	public void accept(final PicoVisitor visitor) {
        super.accept(visitor);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                parameter.accept(visitor);
            }
        }
    }
    public void start(final Object component) {
        lifecycleStrategy.start(component);
    }

    public void stop(final Object component) {
        lifecycleStrategy.stop(component);
    }

    public void dispose(final Object component) {
        lifecycleStrategy.dispose(component);
    }

    public boolean hasLifecycle(final Class<?> type) {
        return lifecycleStrategy.hasLifecycle(type);
    }

    public boolean isLazy(ComponentAdapter<?> adapter) {
        return lifecycleStrategy.isLazy(adapter);
    }

    public String getDescriptor() {
        return "Asbtract Injector";
    }

    /**
     * Instantiate an object with given parameters and respect the accessible flag.
     *
     * @param constructor the constructor to use
     * @param parameters the parameters for the constructor
     * @return the new object.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected T newInstance(final Constructor<T> constructor, final Object[] parameters)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return constructor.newInstance(parameters);
    }
    /**
     * inform monitor about component instantiation failure
     * @param componentMonitor
     * @param constructor
     * @param e
     * @param container
     * @return
     */
    protected T caughtInstantiationException(final ComponentMonitor componentMonitor,
                                                final Constructor<T> constructor,
                                                final InstantiationException e, final PicoContainer container) {
        // can't get here because checkConcrete() will catch it earlier, but see PICO-191
        componentMonitor.instantiationFailed(container, this, constructor, e);
        throw new PicoCompositionException("Should never get here");
    }

    /**
     * inform monitor about access exception.
     * @param componentMonitor
     * @param constructor
     * @param e
     * @param container
     * @return
     */
    protected T caughtIllegalAccessException(final ComponentMonitor componentMonitor,
                                                final Constructor<T> constructor,
                                                final IllegalAccessException e, final PicoContainer container) {
        // can't get here because either filtered or access mode set
        componentMonitor.instantiationFailed(container, this, constructor, e);
        throw new PicoCompositionException(e);
    }

    /**
     * inform monitor about exception while instantiating component
     * @param componentMonitor
     * @param member
     * @param componentInstance
     * @param e
     * @return 
     */
    protected T caughtInvocationTargetException(final ComponentMonitor componentMonitor,
                                                   final Member member,
                                                   final Object componentInstance, final InvocationTargetException e) {
        componentMonitor.invocationFailed(member, componentInstance, e);
        if (e.getTargetException() instanceof RuntimeException) {
            throw (RuntimeException) e.getTargetException();
        } else if (e.getTargetException() instanceof Error) {
            throw (Error) e.getTargetException();
        }
        throw new PicoCompositionException(e.getTargetException());
    }

    protected Object caughtIllegalAccessException(final ComponentMonitor componentMonitor,
                                                final Member member,
                                                final Object componentInstance, final IllegalAccessException e) {
        componentMonitor.invocationFailed(member, componentInstance, e);
        throw new PicoCompositionException(e);
    }

    protected Type box(Type parameterType) {
        if (parameterType instanceof Class && ((Class) parameterType).isPrimitive()) {
            String parameterTypeName = ((Class) parameterType).getName();
            if (parameterTypeName == "int") {
                return Integer.class;
            } else if (parameterTypeName == "boolean") {
                return Boolean.class;
            } else if (parameterTypeName == "long") {
                return Long.class;
            } else if (parameterTypeName == "float") {
                return Float.class;
            } else if (parameterTypeName == "double") {
                return Double.class;
            } else if (parameterTypeName == "char") {
                return Character.class;
            } else if (parameterTypeName == "byte") {
                return Byte.class;
            } else if (parameterTypeName == "short") {
                return Short.class;
            }
        }
        return parameterType;
    }

    /**
     * Abstract utility class to detect recursion cycles.
     * Derive from this class and implement {@link ThreadLocalCyclicDependencyGuard#run}.
     * The method will be called by  {@link ThreadLocalCyclicDependencyGuard#observe}. Select
     * an appropriate guard for your scope. Any {@link ObjectReference} can be
     * used as long as it is initialized with  <code>Boolean.FALSE</code>.
     *
     * @author J&ouml;rg Schaible
     */
    static abstract class ThreadLocalCyclicDependencyGuard<T> extends ThreadLocal<Boolean> {

        protected PicoContainer guardedContainer;

        @Override
		protected Boolean initialValue() {
            return Boolean.FALSE;
        }

        /**
         * Derive from this class and implement this function with the functionality
         * to observe for a dependency cycle.
         *
         * @return a value, if the functionality result in an expression,
         *      otherwise just return <code>null</code>
         */
        public abstract T run();

        /**
         * Call the observing function. The provided guard will hold the {@link Boolean} value.
         * If the guard is already <code>Boolean.TRUE</code> a {@link CyclicDependencyException}
         * will be  thrown.
         *
         * @param stackFrame the current stack frame
         * @return the result of the <code>run</code> method
         */
        public final T observe(final Class<?> stackFrame) {
            if (Boolean.TRUE.equals(get())) {
                throw new CyclicDependencyException(stackFrame);
            }
            T result = null;
            try {
                set(Boolean.TRUE);
                result = run();
            } catch (final CyclicDependencyException e) {
                e.push(stackFrame);
                throw e;
            } finally {
                set(Boolean.FALSE);
            }
            return result;
        }

        public void setGuardedContainer(final PicoContainer container) {
            this.guardedContainer = container;
        }

    }

	public static class CyclicDependencyException extends PicoCompositionException {
        private final List<Class> stack;

        /**
         * @param element
         */
        public CyclicDependencyException(final Class<?> element) {
            super((Throwable)null);
            this.stack = new LinkedList<Class>();
            push(element);
        }

        /**
         * @param element
         */
        public void push(final Class<?> element) {
            stack.add(element);
        }

        public Class[] getDependencies() {
            return stack.toArray(new Class[stack.size()]);
        }

        @Override
		public String getMessage() {
            return "Cyclic dependency: " + stack.toString();
        }
    }

    /**
     * Exception that is thrown as part of the introspection. Raised if a PicoContainer cannot resolve a
     * type dependency because the registered {@link org.picocontainer.ComponentAdapter}s are not
     * distinct.
     *
     * @author Paul Hammant
     * @author Aslak Helles&oslash;y
     * @author Jon Tirs&eacute;n
     */
    public static final class AmbiguousComponentResolutionException extends PicoCompositionException {


		private Class<?> component;
        private final Class<?> ambiguousDependency;
        private final Object[] ambiguousComponentKeys;


        /**
         * Construct a new exception with the ambigous class type and the ambiguous component keys.
         *
         * @param ambiguousDependency the unresolved dependency type
         * @param componentKeys the ambiguous keys.
         */
        public AmbiguousComponentResolutionException(final Class<?> ambiguousDependency, final Object[] componentKeys) {
            super("");
            this.ambiguousDependency = ambiguousDependency;
            this.ambiguousComponentKeys = new Class[componentKeys.length];
            System.arraycopy(componentKeys, 0, ambiguousComponentKeys, 0, componentKeys.length);
        }

        /**
         * @return Returns a string containing the unresolved class type and the ambiguous keys.
         */
        @Override
		public String getMessage() {
            StringBuffer msg = new StringBuffer();
            msg.append(component != null ? component : "<no-component>");
            msg.append(" needs a '");
            msg.append(ambiguousDependency.getName());
            msg.append("' injected, but there are too many choices to inject. These:");
            msg.append(Arrays.asList(getAmbiguousComponentKeys()));
            msg.append(", refer http://picocontainer.org/ambiguous-injectable-help.html");
            return msg.toString();
        }

        /**
         * @return Returns the ambiguous component keys as array.
         */
        public Object[] getAmbiguousComponentKeys() {
            return ambiguousComponentKeys;
        }

        public void setComponent(final Class<?> component) {
            this.component = component;
        }
    }

    /**
     * Exception thrown when some of the component's dependencies are not satisfiable.
     *
     * @author Aslak Helles&oslash;y
     * @author Mauro Talevi
     */
    public static class UnsatisfiableDependenciesException extends PicoCompositionException {

		
		private final ComponentAdapter<?> instantiatingComponentAdapter;
        private final Set unsatisfiableDependencies;
        private final Type unsatisfiedDependencyType;
        
        /**
         * The original container requesting the instantiation of the component.
         */
        private final PicoContainer leafContainer;

        public UnsatisfiableDependenciesException(final ComponentAdapter<?> instantiatingComponentAdapter,
                                                  final Type unsatisfiedDependencyType, final Set unsatisfiableDependencies,
                                                  final PicoContainer leafContainer) {
            super(instantiatingComponentAdapter.getComponentImplementation().getName() + " has unsatisfied dependency: " + unsatisfiedDependencyType
                    +" among unsatisfiable dependencies: "+unsatisfiableDependencies + " where " + leafContainer
                    + " was the leaf container being asked for dependencies.");
            this.instantiatingComponentAdapter = instantiatingComponentAdapter;
            this.unsatisfiableDependencies = unsatisfiableDependencies;
            this.unsatisfiedDependencyType = unsatisfiedDependencyType;
            this.leafContainer = leafContainer;
        }

        public ComponentAdapter<?> getUnsatisfiableComponentAdapter() {
            return instantiatingComponentAdapter;
        }

        public Set getUnsatisfiableDependencies() {
            return unsatisfiableDependencies;
        }

        public Type getUnsatisfiedDependencyType() {
            return unsatisfiedDependencyType;
        }

        public PicoContainer getLeafContainer() {
            return leafContainer;
        }

    }

    /**
     * @author Aslak Hellesoy
     */
    public static class NotConcreteRegistrationException extends PicoCompositionException {
		
		private final Class<?> componentImplementation;

        public NotConcreteRegistrationException(final Class<?> componentImplementation) {
            super("Bad Access: '" + componentImplementation.getName() + "' is not instantiable");
            this.componentImplementation = componentImplementation;
        }

        public Class<?> getComponentImplementation() {
            return componentImplementation;
        }
    }
}
