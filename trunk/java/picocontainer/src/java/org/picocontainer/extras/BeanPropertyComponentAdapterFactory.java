package org.picocontainer.extras;

import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.Parameter;
import org.picocontainer.internals.ComponentAdapterFactory;
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

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException {
        try {
            return new Adapter(super.createComponentAdapter(componentKey, componentImplementation, parameters));
        } catch (final IntrospectionException e) {
            ///CLOVER:OFF
            throw new PicoIntrospectionException() {
                public String getMessage() {
                    return "Couldn't introspect bean:" + e.getMessage();
                }
            };
            ///CLOVER:ON
        }
    }

    public static class Adapter extends DecoratingComponentAdapter {
        private final Map propertyValues = new HashMap();
        private PropertyDescriptor[] propertyDescriptors;

        public Adapter(ComponentAdapter delegate) throws IntrospectionException {
            super(delegate);

            BeanInfo beanInfo = Introspector.getBeanInfo(delegate.getComponentImplementation());
            propertyDescriptors = beanInfo.getPropertyDescriptors();
        }

        public Object instantiateComponent(ComponentRegistry componentRegistry) throws PicoInitializationException {
            Object result = super.instantiateComponent(componentRegistry);

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
