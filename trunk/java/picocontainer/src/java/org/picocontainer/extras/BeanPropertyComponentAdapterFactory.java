package org.picocontainer.extras;

import org.picocontainer.Parameter;
import org.picocontainer.defaults.*;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.util.Map;
import java.util.HashMap;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;

/**
 * A generic ComponentAdapter that will set bean properties on the instantiated
 * component. These properties can be registered on beforehand via the
 * {@link Adapter#setPropertyValue(PropertyDescriptor, Object)} method.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    public BeanPropertyComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new Adapter(super.createComponentAdapter(componentKey, componentImplementation, parameters));
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

    public static class Adapter extends DecoratingComponentAdapter {
        private final Map propertyValues = new HashMap();
        private PropertyDescriptor[] propertyDescriptors;
        private Map propertyDescriptorMap = new HashMap();

        public Adapter(ComponentAdapter delegate) throws PicoBeanInfoInitializationException {
            super(delegate);

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

        public Object getComponentInstance(AbstractPicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            Object result = super.getComponentInstance(picoContainer);

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(getComponentImplementation());
                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
                for (int i = 0; i < descriptors.length; i++) {
                    PropertyDescriptor descriptor = descriptors[i];
                    Method setter = descriptor.getWriteMethod();
                    if(setter != null) {
                        Object value = propertyValues.get(descriptor);
                        setter.invoke(result, new Object[]{value});
                    }
                }
            } catch (Exception e) {
                throw new PicoInitializationException() {
                    public String getMessage() {
                        return "Failed to set properties";
                    }
                };
            }

            return result;
        }

        public void setPropertyValue(String propertyName, Object value) throws NoSuchPropertyException {
            propertyValues.put(getPropertyDescriptor(propertyName), value);
        }

        private PropertyDescriptor getPropertyDescriptor(String propertyName) throws NoSuchPropertyException {
            PropertyDescriptor propertyDescriptor = (PropertyDescriptor) propertyDescriptorMap.get(propertyName);
            if( propertyDescriptor == null ) {
                throw new NoSuchPropertyException( getDelegate().getComponentImplementation().getName() +
                        " does not have a settable property named " + propertyName);
            }
            return propertyDescriptor;
        }

        public void setPropertyValue(PropertyDescriptor propertyDescriptor, Object value) {
            propertyValues.put(propertyDescriptor, value);
        }

        public Object getPropertValue(PropertyDescriptor propertyDescriptor) {
            return propertyValues.get(propertyDescriptor);
        }

        public PropertyDescriptor[] getPropertyDescriptors() {
            return propertyDescriptors;
        }
    }
}
