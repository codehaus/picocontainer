package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Instantiates components using IoC type 2. That means trying to
 * pass the bean setters arguments created by other ComponentAdapters
 * in the container.
 * {@inheritDoc}
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanComponentAdapter extends InstantiatingComponentAdapter {
    public BeanComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
        super(componentKey, componentImplementation, parameters);
    }

    public Class[] getDependencies(PicoContainer picoContainer) throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Method[] setters = getSetters();
        Class[] dependencies = new Class[setters.length];
        for (int i = 0; i < setters.length; i++) {
            dependencies[i] = setters[i].getParameterTypes()[0];
        }
        return dependencies;
    }

    protected Object instantiateComponent(ComponentAdapter[] adapterDependencies, MutablePicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Object result = super.instantiateComponent(adapterDependencies, picoContainer);
        setDependencies(result, adapterDependencies, picoContainer);
        return result;
    }

    protected Constructor getConstructor(PicoContainer picoContainer) throws PicoIntrospectionException, NoSatisfiableConstructorsException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        try {
            return getComponentImplementation().getConstructor(new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new NoSatisfiableConstructorsException(getComponentImplementation(), Collections.EMPTY_SET);
        } catch (SecurityException e) {
            throw new NoSatisfiableConstructorsException(getComponentImplementation(), Collections.EMPTY_SET);
        }
    }

    protected Object[] getConstructorArguments(ComponentAdapter[] adapterDependencies, MutablePicoContainer picoContainer) {
        return null;
    }

    private void setDependencies(Object componentInstance, ComponentAdapter[] adapterDependencies, MutablePicoContainer picoContainer) {
        Method[] setters = getSetters();
        for (int i = 0; i < setters.length; i++) {
            Method setter = setters[i];
            Object dependency = adapterDependencies[i].getComponentInstance(picoContainer);
            try {
                setter.invoke(componentInstance, new Object[]{dependency});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private Method[] getSetters() {
        // TODO use caching.
        List setters = new ArrayList();
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
        return (Method[]) setters.toArray(new Method[setters.size()]);
    }
}
