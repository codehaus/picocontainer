package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.MutablePicoContainer;

import java.util.Arrays;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This ComponentAdapter will instantiate a new object for each call to
 * {@link #getComponentInstance(org.picocontainer.MutablePicoContainer)}. That means that
 * when used with a PicoContainer, getComponentInstance will return a new
 * object each time.
 *
 * @author Aslak Helles&oslash;y
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

    public Object getComponentInstance(MutablePicoContainer picoContainer)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter[] adapterDependencies = findDependencies(picoContainer);
        return instantiateComponent(adapterDependencies, picoContainer);
    }

    private Parameter[] getParameters(PicoContainer picoContainer) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (parameters == null) {
            return createDefaultParameters(getDependencies(picoContainer));
        } else {
            return parameters;
        }
    }

    protected static Parameter[] createDefaultParameters(Class[] parameters) {
        ComponentParameter[] componentParameters = new ComponentParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            componentParameters[i] = new ComponentParameter(parameters[i]);
        }
        return componentParameters;
    }

    private ComponentAdapter[] findDependencies(PicoContainer picoContainer) {
        final Class[] dependencyTypes = getDependencies(picoContainer);
        final ComponentAdapter[] adapterDependencies = new ComponentAdapter[dependencyTypes.length];

        final Parameter[] componentParameters = getParameters(picoContainer);

        if (componentParameters.length != adapterDependencies.length) {
            throw new PicoInitializationException() {
                public String getMessage() {
                    return "The number of specified parameters (" +
                            componentParameters.length + ") doesn't match the number of arguments in the greediest satisfiable constructor (" +
                            adapterDependencies.length + "). When parameters are explicitly specified, specify them in the correct order, and one for each constructor argument." +
                            "The greediest satisfiable constructor takes the following arguments: " + Arrays.asList(dependencyTypes).toString();
                }
            };
        }
        for (int i = 0; i < adapterDependencies.length; i++) {
            adapterDependencies[i] = componentParameters[i].resolveAdapter(picoContainer);
        }
        return adapterDependencies;
    }

    protected Object instantiateComponent(ComponentAdapter[] adapterDependencies, MutablePicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        try {
            Constructor constructor = getConstructor(picoContainer);
            if (instantiating) {
                throw new CyclicDependencyException(constructor.getParameterTypes());
            }
            instantiating = true;
            Object[] parameters = getConstructorArguments(adapterDependencies, picoContainer);

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

    public void verify(PicoContainer picoContainer) {
        ComponentAdapter[] adapterDependencies = findDependencies(picoContainer);
        verifyComponent(adapterDependencies, picoContainer);
    }

    private void verifyComponent(ComponentAdapter[] adapterDependencies, PicoContainer picoContainer) throws NoSatisfiableConstructorsException {
        try {
            if (verifying) {
                throw new CyclicDependencyException(getDependencies(picoContainer));
            }
            verifying = true;
            for (int i = 0; i < adapterDependencies.length; i++) {
                ComponentAdapter adapterDependency = adapterDependencies[i];
                adapterDependency.verify(picoContainer);
            }
        } catch (Exception e) {
            throw new PicoInvocationTargetInitializationException(e);
        } finally {
            verifying = false;
        }
    }

    protected abstract Class[] getDependencies(PicoContainer picoContainer) throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
    protected abstract Constructor getConstructor(PicoContainer picoContainer) throws PicoIntrospectionException, NoSatisfiableConstructorsException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
    protected abstract Object[] getConstructorArguments(ComponentAdapter[] adapterDependencies, MutablePicoContainer picoContainer);
}
