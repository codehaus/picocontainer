package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Instantiates components using Setter-Based Dependency Injection. 
 * {@inheritDoc}
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanComponentAdapter extends InstantiatingComponentAdapter {
    private List setters;

    public BeanComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
        super(componentKey, componentImplementation, parameters);
    }

    protected Class[] getMostSatisfiableDependencyTypes(PicoContainer dependencyContainer) throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Method[] setters = getSetters();
        Class[] dependencies = new Class[setters.length];
        for (int i = 0; i < setters.length; i++) {
            dependencies[i] = setters[i].getParameterTypes()[0];
        }
        return dependencies;
    }

    protected Object instantiateComponent(ComponentAdapter[] adapterDependencies, PicoContainer dependencyContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Object result = super.instantiateComponent(adapterDependencies, dependencyContainer);
        setDependencies(result, adapterDependencies);
        return result;
    }

    protected Constructor getGreediestSatisifableConstructor(PicoContainer dependencyContainer) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        try {
            return getComponentImplementation().getConstructor(new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new PicoIntrospectionException("No empty constructor for " + getComponentImplementation().getName(), e);
        } catch (SecurityException e) {
            throw new PicoInvocationTargetInitializationException(e);
        }
    }

    protected Object[] getConstructorArguments(ComponentAdapter[] adapterDependencies) {
        return null;
    }

    private void setDependencies(Object componentInstance, ComponentAdapter[] adapterDependencies) {
        Set unsatisfiableDependencyTypes = new HashSet();
        Method[] setters = getSetters();
        for (int i = 0; i < setters.length; i++) {
            Method setter = setters[i];
            ComponentAdapter adapterDependency = adapterDependencies[i];
            if(adapterDependency != null) {
                Object dependency = adapterDependency.getComponentInstance();
                try {
                    setter.invoke(componentInstance, new Object[]{dependency});
                } catch (Exception e) {
                    throw new PicoIntrospectionException(e);
                }
            } else {
                unsatisfiableDependencyTypes.add(setter.getParameterTypes()[0]);
            }
        }
        if(!unsatisfiableDependencyTypes.isEmpty()) {
            throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
        }
    }

    private Method[] getSetters() {
        if (setters == null) {
            setters = new ArrayList();
            Method[] methods = getComponentImplementation().getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                Class[] parameterTypes = method.getParameterTypes();
                // We're only interested if there is only one parameter and the method name is bean-style.
                boolean hasOneParameter = parameterTypes.length == 1;
                boolean isBeanStyle = method.getName().length() >= 4 && method.getName().startsWith("set") && Character.isUpperCase(method.getName().charAt(3));
                if (hasOneParameter && isBeanStyle) {
                    setters.add(method);
                }
            }
        }
        return (Method[]) setters.toArray(new Method[setters.size()]);
    }
}
