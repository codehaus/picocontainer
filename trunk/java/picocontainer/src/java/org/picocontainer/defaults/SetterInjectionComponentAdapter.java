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
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
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
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class SetterInjectionComponentAdapter extends InstantiatingComponentAdapter {
    private transient boolean instantiating;
    private transient List setters;
    private transient List setterNames;
    private transient Class[] setterTypes;

    /**
     * Explicitly specifies parameters, if null uses default parameters.
     * {@inheritDoc}
     */
    public SetterInjectionComponentAdapter(final Object componentKey,
                                       final Class componentImplementation,
                                       Parameter[] parameters,
                                       boolean allowNonPublicClasses) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, allowNonPublicClasses);
    }

    public SetterInjectionComponentAdapter(final Object componentKey,
                                       final Class componentImplementation,
                                       Parameter[] parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, false);
    }

    /**
     * @see org.picocontainer.defaults.InstantiatingComponentAdapter#getGreediestSatisifableConstructor(java.util.List)
     */
    protected Constructor getGreediestSatisifableConstructor(List adapterInstantiationOrderTrackingList) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        final Constructor constructor;
        try {
            constructor = getComponentImplementation().getConstructor(null);
        } catch (NoSuchMethodException e) {
            throw new PicoInvocationTargetInitializationException(e);
        } catch (SecurityException e) {
            throw new PicoInvocationTargetInitializationException(e);
        }
        
        if(setters == null) {
            initializeSetterAndTypeLists();
        }

        final List matchingTypeList = new ArrayList(Collections.nCopies(setters.size(), null));
        final Set nonMatchingParameterPositions = new HashSet();
        final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(setterTypes);
        for (int i = 0; i < currentParameters.length; i++) {
            final Parameter parameter = currentParameters[i];
            boolean failedDependency = true;
            for(int j = 0; j < setterTypes.length; j++) {
                if (matchingTypeList.get(j) == null) {
                    final ComponentAdapter adapter = parameter.resolveAdapter(getContainer(), setterTypes[j]);
                    if (adapter != null && !adapter.equals(this) && !getComponentKey().equals(adapter.getComponentKey())) {
                        matchingTypeList.set(j, adapter);
                        failedDependency = false;
                        break;
                    }
                }
            }
            if(failedDependency) {
                nonMatchingParameterPositions.add(new Integer(i));
            }
        }
        
        final Set unsatisfiableDependencyTypes = new HashSet();
        for (int i = 0; i < matchingTypeList.size(); i++) {
            if (matchingTypeList.get(i) == null) {
                unsatisfiableDependencyTypes.add(setterTypes[i]);
            }
        }
        if (unsatisfiableDependencyTypes.size() > 0) {
            throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
        }

        if (nonMatchingParameterPositions.size() > 0) {
            throw new PicoInitializationException("Following parameters do not match any of the setters for "
                    + getComponentImplementation() +  ": " + nonMatchingParameterPositions.toString());
        }
        adapterInstantiationOrderTrackingList.addAll(matchingTypeList);
        return constructor;
    }

    /**
     * @see org.picocontainer.defaults.InstantiatingComponentAdapter#instantiateComponent(java.util.List)
     */
    protected Object instantiateComponent(List adapterInstantiationOrderTrackingList) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        try {
            final Constructor constructor = getGreediestSatisifableConstructor(adapterInstantiationOrderTrackingList);
            if (instantiating) {
                throw new CyclicDependencyException(setterTypes);
            }
            instantiating = true;
            final Object componentInstance = newInstance(constructor, null);
            for (int i = 0; i < setters.size(); i++) {
                final Method setter = (Method)setters.get(i);
                final ComponentAdapter adapter = (ComponentAdapter)adapterInstantiationOrderTrackingList.get(i);
                setter.invoke(componentInstance, new Object[]{ adapter.getComponentInstance() });
            }

            return componentInstance;
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

    private void initializeSetterAndTypeLists() {
        setters = new ArrayList();
        setterNames = new ArrayList();
        final List typeList = new ArrayList();
        final Method[] methods = getComponentImplementation().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            final Class[] parameterTypes = method.getParameterTypes();
            // We're only interested if there is only one parameter and the method name is bean-style.
            if (parameterTypes.length == 1) {
                String methodName = method.getName();   
                boolean isBeanStyle = methodName.length() >= 4 && methodName.startsWith("set") && Character.isUpperCase(methodName.charAt(3));
                if (isBeanStyle) {
                    String attribute = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4); 
                    setters.add(method);
                    setterNames.add(attribute);
                    typeList.add(parameterTypes[0]);
                }
            }
        }
        setterTypes = (Class[])typeList.toArray(new Class[0]);
    }
}
