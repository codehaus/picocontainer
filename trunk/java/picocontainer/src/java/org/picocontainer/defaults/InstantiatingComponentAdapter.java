package org.picocontainer.defaults;

import org.picocontainer.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * This ComponentAdapter will instantiate a new object for each call to
 * {@link org.picocontainer.ComponentAdapter#getComponentInstance()}. That means that
 * when used with a PicoContainer, getComponentInstance will return a new
 * object each time.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public abstract class InstantiatingComponentAdapter extends AbstractComponentAdapter {
    private boolean instantiating;
    private boolean verifying;
    protected Parameter[] parameters;

    public InstantiatingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation);
        this.parameters = parameters;
    }

    public Object getComponentInstance()
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        ComponentAdapter[] dependencyAdapters = getMostSatisifableDependencyAdapters(getContainer());
        Object instance = instantiateComponent(dependencyAdapters, getContainer());
        return instance;
    }

    private Parameter[] getParameters(PicoContainer picoContainer) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (parameters == null || parameters.length == 0) {
            return createDefaultParameters(getMostSatisfiableDependencyTypes(picoContainer), picoContainer);
        } else {
            return parameters;
        }
    }

    protected static Parameter[] createDefaultParameters(Class[] parameters, PicoContainer picoContainer) {
        Parameter[] componentParameters = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if(PicoContainer.class.isAssignableFrom(parameters[i])) {
                componentParameters[i] = new ConstantParameter(picoContainer);
            } else {
                componentParameters[i] = new ComponentParameter(parameters[i]);
            }
        }
        return componentParameters;
    }

    private ComponentAdapter[] getMostSatisifableDependencyAdapters(PicoContainer dependencyContainer) {
        final Class[] mostSatisfiableDependencyTypes = getMostSatisfiableDependencyTypes(dependencyContainer);
        final ComponentAdapter[] mostSatisfiableDependencyAdapters = new ComponentAdapter[mostSatisfiableDependencyTypes.length];

        final Parameter[] componentParameters = getParameters(dependencyContainer);

        if (componentParameters.length != mostSatisfiableDependencyAdapters.length) {
            throw new PicoInitializationException() {
                public String getMessage() {
                    return "The number of specified parameters (" +
                            componentParameters.length + ") doesn't match the number of arguments in the greediest satisfiable constructor (" +
                            mostSatisfiableDependencyAdapters.length + "). When parameters are explicitly specified, specify them in the correct order, and one for each constructor argument." +
                            "The greediest satisfiable constructor takes the following arguments: " + Arrays.asList(mostSatisfiableDependencyTypes).toString();
                }
            };
        }
        for (int i = 0; i < mostSatisfiableDependencyAdapters.length; i++) {
            mostSatisfiableDependencyAdapters[i] = componentParameters[i].resolveAdapter(dependencyContainer);
        }
        return mostSatisfiableDependencyAdapters;
    }

    protected Object instantiateComponent(ComponentAdapter[] adapterDependencies, PicoContainer dependencyContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        try {
            Constructor constructor = getGreediestSatisifableConstructor(dependencyContainer);
            if (instantiating) {
                throw new CyclicDependencyException(constructor.getParameterTypes());
            }
            instantiating = true;
            Object[] parameters = getConstructorArguments(adapterDependencies, dependencyContainer);

            return constructor.newInstance(parameters);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            }
            throw new PicoInvocationTargetInitializationException(e.getTargetException());
        } catch (InstantiationException e) {
            throw new PicoInvocationTargetInitializationException(e);
        } catch (IllegalAccessException e) {
            throw new PicoInvocationTargetInitializationException(e);
        } finally {
            instantiating = false;
        }
    }

    public void verify() throws UnsatisfiableDependenciesException {
        try {
            ComponentAdapter[] adapterDependencies = getMostSatisifableDependencyAdapters(getContainer());
            if (verifying) {
                throw new CyclicDependencyException(getMostSatisfiableDependencyTypes(getContainer()));
            }
            verifying = true;
            for (int i = 0; i < adapterDependencies.length; i++) {
                ComponentAdapter adapterDependency = adapterDependencies[i];
                adapterDependency.verify();
            }
        } finally {
            verifying = false;
        }
    }

    protected abstract Class[] getMostSatisfiableDependencyTypes(PicoContainer dependencyContainer) throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException;

    protected abstract Constructor getGreediestSatisifableConstructor(PicoContainer dependencyContainer) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException;

    protected abstract Object[] getConstructorArguments(ComponentAdapter[] adapterDependencies, PicoContainer dependencyContainer);
}
