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
import org.picocontainer.Parameter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Instantiates components using empty constructors and
 * <a href="http://docs.codehaus.org/display/PICO/Setter+Injection">Setter Injection</a>.
 * For easy setting of primitive properties, also see {@link BeanPropertyComponentAdapter}.
 * <p/>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link CachingComponentAdapter} around this one.
 * </em>
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SetterInjectionComponentAdapter extends DecoratingComponentAdapter {
    private List setters;
    private final Parameter[] parameters;

    public SetterInjectionComponentAdapter(ComponentAdapter delegate) {
        this(delegate, null);
    }

    public SetterInjectionComponentAdapter(ComponentAdapter delegate, Parameter[] parameters) {
        super(delegate);
        this.parameters = parameters;
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Object result = super.getComponentInstance();

        setDependencies(result);
        return result;
    }

    private void setDependencies(Object componentInstance) {
        Set unsatisfiableDependencyTypes = new HashSet();
        Method[] setters = getSetters();
        for (int i = 0; i < setters.length; i++) {
            Method setter = setters[i];
            Class type = setter.getParameterTypes()[0];
            Object dependency = getDependencyInstance(type, unsatisfiableDependencyTypes);
            try {
                setter.invoke(componentInstance, new Object[]{dependency});
            } catch (Exception e) {
                throw new PicoIntrospectionException(e);
            }
        }
        if (!unsatisfiableDependencyTypes.isEmpty()) {
            throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
        }
    }

    private Object getDependencyInstance(Class type, Set unsatisfiableDependencyTypes) {
        if(parameters != null) {
            return getDependencyInstanceFromParameters(type, unsatisfiableDependencyTypes);
        }
        ComponentAdapter adapterDependency = getContainer().getComponentAdapterOfType(type);
        if (adapterDependency != null) {
            return adapterDependency.getComponentInstance();
        } else {
            unsatisfiableDependencyTypes.add(type);
            return null;
        }
    }

    private Object getDependencyInstanceFromParameters(Class type, Set unsatisfiableDependencyTypes) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            ComponentAdapter adapter = parameter.resolveAdapter(getContainer(), type);
            if(adapter != null) {
                return adapter.getComponentInstance();
            }
        }
        unsatisfiableDependencyTypes.add(type);
        return null;
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
