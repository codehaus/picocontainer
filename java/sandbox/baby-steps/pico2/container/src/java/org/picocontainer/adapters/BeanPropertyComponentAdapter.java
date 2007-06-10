package org.picocontainer.adapters;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoClassNotFoundException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.adapters.BehaviorAdapter;

/**
 * Decorating addComponent addAdapter that can be used to set additional properties
 * on a addComponent in a bean style. These properties must be managed manually
 * by the user of the API, and will not be managed by PicoContainer. This class
 * is therefore <em>not</em> the same as {@link SetterInjectionAdapter},
 * which is a true Setter Injection addAdapter.
 * <p/>
 * This addAdapter is mostly handy for setting various primitive properties via setters;
 * it is also able to set javabean properties by discovering an appropriate
 * {@link PropertyEditor} and using its <code>setAsText</code> method.
 * <p/>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link CachingBehaviorAdapter} around this one.
 * </em>
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @since 1.0
 */
public class BeanPropertyComponentAdapter extends BehaviorAdapter {
    private Map properties;
    private transient Map<String, Method> setters = null;

    /**
     * Construct a BeanPropertyComponentAdapter.
     *
     * @param delegate the wrapped {@link ComponentAdapter}
     * @throws PicoInitializationException {@inheritDoc}
     */
    public BeanPropertyComponentAdapter(ComponentAdapter delegate) throws PicoInitializationException {
        super(delegate);
    }

    /**
     * Get a addComponent instance and set given property values.
     *
     * @return the addComponent instance with any properties of the properties map set.
     * @throws PicoInitializationException {@inheritDoc}
     * @throws PicoIntrospectionException  {@inheritDoc}
     * @throws org.picocontainer.PicoRegistrationException
     *                                     {@inheritDoc}
     * @see #setProperties(Map)
     */
    public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException,
                                                                       PicoRegistrationException
    {
        final Object componentInstance = super.getComponentInstance(container);
        if (setters == null) {
            setters = getSetters(getComponentImplementation());
        }

        if (properties != null) {
            ComponentMonitor componentMonitor = currentMonitor();
            Set<String> propertyNames = properties.keySet();
            for (String propertyName : propertyNames) {
                final Object propertyValue = properties.get(propertyName);
                Method setter = setters.get(propertyName);

                Object valueToInvoke = this.getSetterParameter(propertyName, propertyValue, componentInstance, container);

                try {
                    componentMonitor.invoking(setter, componentInstance);
                    long startTime = System.currentTimeMillis();
                    setter.invoke(componentInstance, valueToInvoke);
                    componentMonitor.invoked(setter, componentInstance, System.currentTimeMillis() - startTime);
                } catch (final Exception e) {
                    componentMonitor.invocationFailed(setter, componentInstance, e);
                    throw new PicoInitializationException("Failed to set property " + propertyName + " to " + propertyValue + ": " + e.getMessage(), e);
                }
            }
        }
        return componentInstance;
    }

    private Map<String, Method> getSetters(Class clazz) {
        Map<String, Method> result = new HashMap<String, Method>();
        Method[] methods = getMethods(clazz);
        for (Method method : methods) {
            if (isSetter(method)) {
                result.put(getPropertyName(method), method);
            }
        }
        return result;
    }

    private Method[] getMethods(final Class clazz) {
        return (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return clazz.getMethods();
            }
        });
    }


    private String getPropertyName(Method method) {
        final String name = method.getName();
        String result = name.substring(3);
        if(result.length() > 1 && !Character.isUpperCase(result.charAt(1))) {
            result = "" + Character.toLowerCase(result.charAt(0)) + result.substring(1);
        } else if(result.length() == 1) {
            result = result.toLowerCase();
        }
        return result;
    }

    private boolean isSetter(Method method) {
        final String name = method.getName();
        return name.length() > 3 &&
                name.startsWith("set") &&
                method.getParameterTypes().length == 1;
    }



    private Object convertType(PicoContainer container, Method setter, String propertyValue) {
        if (propertyValue == null) {
            return null;
        }
        Class type = setter.getParameterTypes()[0];
        String typeName = type.getName();

        Object result = convert(typeName, propertyValue, Thread.currentThread().getContextClassLoader());

        if (result == null) {

            // check if the propertyValue is a key of a addComponent in the container
            // if so, the typeName of the addComponent and the setters parameter typeName
            // have to be compatible

            // TODO: null check only because of test-case, otherwise null is impossible
            if (container != null) {
                Object component = container.getComponent(propertyValue);
                if (component != null && type.isAssignableFrom(component.getClass())) {
                    return component;
                }
            }
        }
        return result;
    }

    /**
     * Converts a String value of a named type to an object.
     * Works with primitive wrappers, String, File, URL types, or any type that has
     * an appropriate {@link PropertyEditor}.
     *  
     * @param typeName    name of the type
     * @param value       its value
     * @param classLoader used to load a class if typeName is "class" or "java.lang.Class" (ignored otherwise)
     * @return instantiated object or null if the type was unknown/unsupported
     */
    public static Object convert(String typeName, String value, ClassLoader classLoader) {
        if (typeName.equals(Boolean.class.getName()) || typeName.equals(boolean.class.getName())) {
            return Boolean.valueOf(value);
        } else if (typeName.equals(Byte.class.getName()) || typeName.equals(byte.class.getName())) {
            return Byte.valueOf(value);
        } else if (typeName.equals(Short.class.getName()) || typeName.equals(short.class.getName())) {
            return Short.valueOf(value);
        } else if (typeName.equals(Integer.class.getName()) || typeName.equals(int.class.getName())) {
            return Integer.valueOf(value);
        } else if (typeName.equals(Long.class.getName()) || typeName.equals(long.class.getName())) {
            return Long.valueOf(value);
        } else if (typeName.equals(Float.class.getName()) || typeName.equals(float.class.getName())) {
            return Float.valueOf(value);
        } else if (typeName.equals(Double.class.getName()) || typeName.equals(double.class.getName())) {
            return Double.valueOf(value);
        } else if (typeName.equals(Character.class.getName()) || typeName.equals(char.class.getName())) {
            return value.toCharArray()[0];
        } else if (typeName.equals(String.class.getName()) || typeName.equals("string")) {
            return value;
        } else if (typeName.equals(File.class.getName()) || typeName.equals("file")) {
            return new File(value);
        } else if (typeName.equals(URL.class.getName()) || typeName.equals("url")) {
            try {
                return new URL(value);
            } catch (MalformedURLException e) {
                throw new PicoInitializationException(e);
            }
        } else if (typeName.equals(Class.class.getName()) || typeName.equals("class")) {
            return loadClass(classLoader, value);
        } else {
            final Class clazz = loadClass(classLoader, typeName);
            final PropertyEditor editor = PropertyEditorManager.findEditor(clazz);
            if (editor != null) {
                editor.setAsText(value);
                return editor.getValue();
            }
        }
        return null;
    }

    private static Class loadClass(ClassLoader classLoader, String typeName) {
        try {
            return classLoader.loadClass(typeName);
        } catch (ClassNotFoundException e) {
            throw new PicoClassNotFoundException(typeName, e);
        }
    }


    /**
     * Sets the bean property values that should be set upon creation.
     *
     * @param properties bean properties
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    /**
     * Converts and validates the given property value to an appropriate object
     * for calling the bean's setter.
     * @param propertyName String the property name on the addComponent that
     * we will be setting the value to.
     * @param propertyValue Object the property value that we've been given. It
     * may need conversion to be formed into the value we need for the
     * addComponent instance setter.
     * @param componentInstance the addComponent that we're looking to provide
     * the setter to.
     * @return Object: the final converted object that can
     * be used in the setter.
     * @param container
     */
    private Object getSetterParameter(final String propertyName, final Object propertyValue,
        final Object componentInstance, PicoContainer container) {

        if (propertyValue == null) {
            return null;
        }

        Method setter = setters.get(propertyName);

        //We can assume that there is only one object (as per typical setters)
        //because the Setter introspector does that job for us earlier.
        Class setterParameter = setter.getParameterTypes()[0];

        Object convertedValue;

        Class<? extends Object> givenParameterClass = propertyValue.getClass();

        //
        //If property value is a string or a true primative then convert it to whatever
        //we need.  (String will convert to string).
        //
            convertedValue = convertType(container, setter, propertyValue.toString());

        //Otherwise, check the parameter type to make sure we can
        //assign it properly.
        if (convertedValue == null) {
            if (setterParameter.isAssignableFrom(givenParameterClass)) {
                convertedValue = propertyValue;
            } else {
                throw new ClassCastException("Setter: " + setter.getName() + " for addComponent: "
                    + componentInstance.toString() + " can only take objects of: " + setterParameter.getName()
                    + " instead got: " + givenParameterClass.getName());
            }
        }
        return convertedValue;
    }
}
