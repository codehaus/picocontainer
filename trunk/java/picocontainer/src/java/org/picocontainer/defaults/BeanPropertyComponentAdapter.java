package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

/**
 * Decorating component adapter that can be used to set additional properties
 * on a component. These properties must be managed manually by the user of the API,
 * and will not be managed by PicoContainer. This class is therefore <em>not</em>
 * the same as {@link SetterInjectionComponentAdapter}, which is a true Setter Injection adapter.
 * <p>
 * This adapter is mostly handy for setting various primitive properties via setters.
 * <p>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link CachingComponentAdapter} around this one.
 * </em>
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyComponentAdapter extends DecoratingComponentAdapter {
    private Map properties;
    private PropertyDescriptor[] propertyDescriptors;
    private Map propertyDescriptorMap = new HashMap();

    public BeanPropertyComponentAdapter(ComponentAdapter delegate) throws PicoInitializationException {
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
            throw new PicoInitializationException("Couldn't load BeanInfo for" + delegate.getComponentImplementation().getName(), e);
            ///CLOVER:ON
        }
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        final Object componentInstance = super.getComponentInstance();

        if (properties != null) {
            Set propertyNames = properties.keySet();
            for (Iterator iterator = propertyNames.iterator(); iterator.hasNext();) {
                final String propertyName = (String) iterator.next();
                final Object propertyValue = properties.get(propertyName);
                final PropertyDescriptor propertyDescriptor = (PropertyDescriptor) propertyDescriptorMap.get(propertyName);
                if (propertyDescriptor == null) {
                    throw new PicoIntrospectionException("Unknown property '" + propertyName + "' in class " + componentInstance.getClass().getName() + " Existing properties:" + propertyDescriptorMap);
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
                if (component != null && type.isAssignableFrom(component.getClass())) {
                    return component;
                }
            }
        }
        return propertyValue;
    }

    /**
     * Sets the bean property values that should be set upon creation.
     *
     * @param properties bean properties
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }
}