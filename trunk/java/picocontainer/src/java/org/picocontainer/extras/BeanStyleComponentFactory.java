package org.picocontainer.extras;

import org.picocontainer.ComponentFactory;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.ComponentSpecification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A Component factory that works with Java Bean style components. That is, components that
 * have an empty constructor and zero or more setXxx methods with one argument to set dependencies.
 * Note that this ComponentFactory does not use IoC type 3, but rather a "loosely typed" IoC type 1.
 * (It's loosely typed since no interfaces need to be implemented - as the presence of setXxx
 * methods in the component implementation classes is enough to have these methods called).
 * <em>
 * This class is provided for convenience only, so components adhering to IoC type 1 can be used
 * without modification. It is however recommended to use {@link picocontainer.defaults.DefaultComponentFactory}
 * as the basis for component creation, as this leads to a better component design.
 * </em>
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.4 $
 */
public class BeanStyleComponentFactory implements ComponentFactory {
    public Object createComponent(ComponentSpecification componentSpec, Object[] instanceDependencies) throws PicoInitializationException {
        // We'll assume there is an empty constructor
        Object result = null;
        result = componentSpec.newInstance();

        // Now set the dependencies by calling appropriate methods taking the dependencies as arguments.
        Method[] setters = getSetters(componentSpec.getComponentImplementation());
        if (setters.length != instanceDependencies.length) {
            throw new IllegalStateException("Unmatching number of dependencies: " + setters.length + " vs " + instanceDependencies.length);
        }
//        List arguments = new ArrayList(Arrays.asList(instanceDependencies));
        for (int i = 0; i < setters.length; i++) {
            Method setter = setters[i];
//            Object argument = extractArgument(arguments,setter);
            // We can assume order is correct.
            Object argument = instanceDependencies[i];
            try {
                setter.invoke(result, new Object[]{argument});
            } catch (IllegalAccessException e) {
                throw new RuntimeException("#5 Can we have a concerted effort to try to force these excptions?");
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("#6 Can we have a concerted effort to try to force these excptions?");
            } catch (InvocationTargetException e) {
                throw new RuntimeException("#7 Can we have a concerted effort to try to force these excptions?");
            }
        }
        return result;
    }

    /*
    Not necessary, since we can assume order is correct.

    private Object extractArgument(List arguments, Method setter) throws NoArgumentForSetterException {
        for (Iterator iterator = arguments.iterator(); iterator.hasNext();) {
            Object argumentCandidate = iterator.next();
            if( setter.getParameterTypes()[0].isAssignableFrom(argumentCandidate.getClass())) {
                // Got it. Remove it so we don't use it again.
                iterator.remove();
                return argumentCandidate;
            }
        }
        // We didn't find one. That's bad.
        throw new NoArgumentForSetterException(setter);
    }
    */

    /**
     * {@inheritDoc}
     *
     * The dependencies are resolved by looking at the types of all setXxx methods
     * taking one parameter.
     *
     * @param componentImplementation
     * @return all classes that are defined in setXxx methods taking one argument.
     * @throws PicoIntrospectionException
     */
    public Class[] getDependencies(Class componentImplementation) throws PicoIntrospectionException {
        // TODO use caching.
        Method[] setters = getSetters(componentImplementation);
        Class[] result = new Class[setters.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = setters[i].getParameterTypes()[0];
        }
        return result;
    }

    private Method[] getSetters(Class componentImplementation) {
        // TODO use caching.
        List setters = new ArrayList();
        Method[] methods = componentImplementation.getMethods();
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
        return (Method[]) setters.toArray(new Method[setters.size()]);
    }
}
