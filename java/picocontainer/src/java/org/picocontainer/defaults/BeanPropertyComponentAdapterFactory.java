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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

/**
 * A generic ComponentAdapter that will set bean properties on the instantiated
 * component. Properties can be set on beforehand via the {@link #setProperties}
 * method.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    /**
     * Map of maps. The key for each map value is a component key. The key/value pairs in the
     * map values are property name/property value pairs.
     */
    private Map componentProperties = new HashMap();

    public BeanPropertyComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter decoratedAdapter = super.createComponentAdapter(componentKey, componentImplementation, parameters);

        Map propertyMap = (Map) componentProperties.get(componentKey);
        Adapter propertyAdapter = new Adapter(decoratedAdapter, propertyMap);
        return propertyAdapter;
    }

    /**
     * Set properties to set upon the component instance upon instantiation.
     *
     * @param componentKey key of component instance where properties should be set.
     * @param properties   map of bean property name -> property value
     */
    public void setProperties(Object componentKey, Map properties) {
        componentProperties.put(componentKey, properties);
    }

    public static class PicoBeanInfoInitializationException extends PicoIntrospectionException {
        protected PicoBeanInfoInitializationException(String message, Exception cause) {
            super(message, cause);
        }
    }

    public static class NoSuchPropertyException extends PicoInitializationException {
    }

    private class Adapter extends DecoratingComponentAdapter {
        private final Map propertyValues;
        private PropertyDescriptor[] propertyDescriptors;
        private Map propertyDescriptorMap = new HashMap();

        public Adapter(ComponentAdapter delegate, Map propertyValues) throws PicoBeanInfoInitializationException {
            super(delegate);
            this.propertyValues = propertyValues;

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(delegate.getComponentImplementation());
                propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (int i = 0; i < propertyDescriptors.length; i++) {
                    PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                    propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
                }
            } catch (IntrospectionException e) {
                ///CLOVER:OFF
                throw new PicoBeanInfoInitializationException("Couldn't load BeanInfo for" + delegate.getComponentImplementation().getName(), e);
                ///CLOVER:ON
            }
        }

        public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            final Object componentInstance = super.getComponentInstance();

            if (propertyValues != null) {
                Set propertyNames = propertyValues.keySet();
                for (Iterator iterator = propertyNames.iterator(); iterator.hasNext();) {
                    final String propertyName = (String) iterator.next();
                    final Object propertyValue = propertyValues.get(propertyName);
                    final PropertyDescriptor propertyDescriptor = (PropertyDescriptor) propertyDescriptorMap.get(propertyName);
                    if (propertyDescriptor == null) {
                        throw new PicoIntrospectionException("Unknown property '" + propertyName + "' in class " + componentInstance.getClass().getName());
                    }
                    Method setter = propertyDescriptor.getWriteMethod();
                    if (setter == null) {
                        throw new PicoInitializationException("There is no public setter method for property " + propertyName + " in " + componentInstance.getClass().getName() +
                                        ". Setter: " + propertyDescriptor.getWriteMethod() +
                                        ". Getter: " + propertyDescriptor.getReadMethod());
                    }
                    try {
                        setter.invoke(componentInstance, new Object[]{convertType(setter, propertyValue)});
                    } catch (final Exception e) {
                        throw new PicoInitializationException("Failed to set property " + propertyName + " to " + propertyValue + ": " + e.getMessage(), e);
                    }
                }
            }
            return componentInstance;
        }

        private Object convertType(Method setter, Object propertyValue) throws MalformedURLException {
            if (propertyValue == null) {
                return null;
            }
            Class type = setter.getParameterTypes()[0];
            if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                return Boolean.valueOf(propertyValue.toString());
            } else if (type.equals(Byte.class) || type.equals(byte.class)) {
                return Byte.valueOf(propertyValue.toString());
            } else if (type.equals(Short.class) || type.equals(short.class)) {
                return Short.valueOf(propertyValue.toString());
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                return Integer.valueOf(propertyValue.toString());
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                return Long.valueOf(propertyValue.toString());
            } else if (type.equals(Float.class) || type.equals(float.class)) {
                return Float.valueOf(propertyValue.toString());
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                return Double.valueOf(propertyValue.toString());
            } else if (type.equals(Character.class) || type.equals(char.class)) {
                return new Character(propertyValue.toString().toCharArray()[0]);
            } else if (File.class.isAssignableFrom(type)) {
                return new File(propertyValue.toString());
            } else if (URL.class.isAssignableFrom(type)) {
                return new URL(propertyValue.toString());
            } else {

                // check if the propertyValue is a key of a component in the container
                // if so, the type of the component and the setters parameter type
                // have to be compatible
                if (getContainer() != null) {
                    Object component = getContainer().getComponentInstance(propertyValue);
                    if (type.isAssignableFrom(component.getClass())) {
                        return component;
                    }
                }
            }
            return propertyValue;
        }
    }
}
