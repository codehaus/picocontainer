package org.picocontainer.defaults;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

/**
 * Decorating component adapter that can be used to set additional properties
 * on a component in a bean style. These properties must be managed manually
 * by the user of the API, and will not be managed by PicoContainer. This class
 * is therefore <em>not</em> the same as {@link SetterInjectionComponentAdapter},
 * which is a true Setter Injection adapter.
 * <p/>
 * This adapter is mostly handy for setting various primitive properties via setters.
 * <p/>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link CachingComponentAdapter} around this one.
 * </em>
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @since 1.0
 */
public class BeanPropertyComponentAdapter extends DecoratingComponentAdapter {
    private Map properties;
    private transient Map setters = null;

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
     * Get a component instance and set given property values.
     *
     * @return the component instance with any properties of the properties map set.
     * @throws PicoInitializationException {@inheritDoc}
     * @throws PicoIntrospectionException  {@inheritDoc}
     * @throws AssignabilityRegistrationException
     *                                     {@inheritDoc}
     * @throws NotConcreteRegistrationException
     *                                     {@inheritDoc}
     * @see #setProperties(Map)
     */
    public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        final Object componentInstance = super.getComponentInstance(container);
        if (setters == null) {
            setters = new SetterIntrospector().getSetters(getComponentImplementation());
        }

        if (properties != null) {
            ComponentMonitor componentMonitor = currentMonitor();
            Set propertyNames = properties.keySet();
            for (Iterator iterator = propertyNames.iterator(); iterator.hasNext();) {
                final String propertyName = (String) iterator.next();
                final Object propertyValue = properties.get(propertyName);
                Method setter = (Method) setters.get(propertyName);

                Object valueToInvoke = this.getSetterParameter(propertyName,propertyValue,componentInstance,container);

                try {
                    componentMonitor.invoking(setter, componentInstance);
                    long startTime = System.currentTimeMillis();
                    setter.invoke(componentInstance, new Object[]{valueToInvoke});
                    componentMonitor.invoked(setter, componentInstance, System.currentTimeMillis() - startTime);
                } catch (final Exception e) {
                    componentMonitor.invocationFailed(setter, componentInstance, e);
                    throw new PicoInitializationException("Failed to set property " + propertyName + " to " + propertyValue + ": " + e.getMessage(), e);
                }
            }
        }
        return componentInstance;
    }

    private Object convertType(PicoContainer container, Method setter, String propertyValue) throws ClassNotFoundException {
        if (propertyValue == null) {
            return null;
        }
        Class type = setter.getParameterTypes()[0];
        String typeName = type.getName();

        Object result = convert(typeName, propertyValue, Thread.currentThread().getContextClassLoader());

        if (result == null) {

            // check if the propertyValue is a key of a component in the container
            // if so, the typeName of the component and the setters parameter typeName
            // have to be compatible

            // TODO: null check only because of test-case, otherwise null is impossible
            if (container != null) {
                Object component = container.getComponentInstance(propertyValue);
                if (component != null && type.isAssignableFrom(component.getClass())) {
                    return component;
                }
            }
        }
        return result;
    }

    /**
     * Converts a type name to an object.
     *
     * @param typeName    name of the type
     * @param value       its value
     * @param classLoader used to load a class if typeName is "class" or "java.lang.Class" (ignored otherwise)
     * @return instantiated object or null if the type was unknown/unsupported
     * @throws ClassNotFoundException if typeName is "class" or "java.lang.Class" and class couldn't be loaded.
     */
    public static Object convert(String typeName, String value, ClassLoader classLoader) throws ClassNotFoundException {
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
            return new Character(value.toCharArray()[0]);
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
            return classLoader.loadClass(value);
        }
        return null;
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
     * @param propertyName String the property name on the component that
     * we will be setting the value to.
     * @param propertyValue Object the property value that we've been given. It
     * may need conversion to be formed into the value we need for the
     * component instance setter.
     * @param componentInstance the component that we're looking to provide
     * the setter to.
     * @return Object: the final converted object that can
     * be used in the setter.
     */
    private Object getSetterParameter(final String propertyName, final Object propertyValue,
        final Object componentInstance, PicoContainer container) throws PicoInitializationException, ClassCastException {

        if (propertyValue == null) {
            return null;
        }

        Method setter = (Method) setters.get(propertyName);

        //We can assume that there is only one object (as per typical setters)
        //because the Setter introspector does that job for us earlier.
        Class setterParameter = setter.getParameterTypes()[0];

        Object convertedValue = null;

        Class givenParameterClass = propertyValue.getClass();

        //
        //If property value is a string or a true primative then convert it to whatever
        //we need.  (String will convert to string).
        //
        try {
            convertedValue = convertType(container, setter, propertyValue.toString());
        }
        catch (ClassNotFoundException e) {
            throw new PicoInvocationTargetInitializationException(e);
        }

        //Otherwise, check the parameter type to make sure we can
        //assign it properly.
        if (convertedValue == null) {
            if (setterParameter.isAssignableFrom(givenParameterClass)) {
                convertedValue = propertyValue;
            } else {
                throw new ClassCastException("Setter: " + setter.getName() + " for component: "
                    + componentInstance.toString() + " can only take objects of: " + setterParameter.getName()
                    + " instead got: " + givenParameterClass.getName());
            }
        }
        return convertedValue;
    }
}
