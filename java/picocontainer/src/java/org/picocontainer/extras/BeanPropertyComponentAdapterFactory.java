package org.picocontainer.extras;

import org.picocontainer.defaults.*;
import org.picocontainer.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;

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

    public void setProperties(Object componentKey, Map properies) {
        componentProperties.put(componentKey, properies);
    }

    public static class PicoBeanInfoInitializationException extends PicoIntrospectionException {
        protected PicoBeanInfoInitializationException(String message, Exception cause) {
            super(message, cause);
        }
    }

    public static class NoSuchPropertyException extends PicoInitializationException {
        public NoSuchPropertyException(String message) {
            super(message);
        }
    }

    private class Adapter extends DecoratingComponentAdapter {
        private final Map propertyValues;
        private PropertyDescriptor[] propertyDescriptors;
        private Map propertyDescriptorMap = new HashMap();
        private Object componentInstance = null;

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
                throw new PicoBeanInfoInitializationException(
                        "Couldn't load BeanInfo for" + delegate.getComponentImplementation().getName(), e);
                ///CLOVER:ON
            }
        }

        public Object getComponentInstance(MutablePicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            if (componentInstance == null) {
                componentInstance = super.getComponentInstance(picoContainer);

                if (propertyValues != null) {
                    Set propertyNames = propertyValues.keySet();
                    for (Iterator iterator = propertyNames.iterator(); iterator.hasNext();) {
                        final String propertyName = (String) iterator.next();
                        final Object propertyValue = propertyValues.get(propertyName);
                        final PropertyDescriptor propertyDescriptor = (PropertyDescriptor) propertyDescriptorMap.get(propertyName);
                        Method setter = propertyDescriptor.getWriteMethod();
                        if(setter == null) {
                            throw new PicoInitializationException() {
                                public String getMessage() {
                                    return "There is no public setter method for property " + propertyName + " in " + componentInstance.getClass().getName() +
                                            ". Setter: " + propertyDescriptor.getWriteMethod() +
                                            ". Getter: " + propertyDescriptor.getReadMethod();
                                }
                            };
                        }
                        try {
                            setter.invoke(componentInstance, new Object[]{propertyValue});
                        } catch (final Exception e) {
                            e.printStackTrace();
                            throw new PicoInitializationException() {
                                public String getMessage() {
                                    return "Failed to set property " + propertyName + " to " + propertyValue + ". " + e.getMessage();
                                }
                            };
                        }
                    }
                }
            }
            return componentInstance;
        }
    }
}
