/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@inheritDoc}
 * Instantiates components using empty constructors and
 * <a href="http://docs.codehaus.org/display/PICO/Setter+Injection">Setter Injection</a>.
 * For easy setting of primitive properties, also see {@link BeanPropertyComponentAdapter}.
 * <p>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link CachingComponentAdapter} around this one.
 * </em>
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanComponentAdapter extends DecoratingComponentAdapter {
    private List setters;

    public BeanComponentAdapter(ComponentAdapter delegate) {
        super(delegate);
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Object result = super.getComponentInstance();

        Class[] deps = getSetterTypes();
        ComponentAdapter[] componentAdapters = new ComponentAdapter[deps.length];
        for (int i = 0; i < componentAdapters.length; i++) {
            componentAdapters[i] = getContainer().getComponentAdapterOfType(deps[i]);
        }
        setDependencies(result, componentAdapters);
        return result;
    }

    private Class[] getSetterTypes() throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Method[] setters = getSetters();
        Class[] dependencies = new Class[setters.length];
        for (int i = 0; i < setters.length; i++) {
            dependencies[i] = setters[i].getParameterTypes()[0];
        }
        return dependencies;
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
