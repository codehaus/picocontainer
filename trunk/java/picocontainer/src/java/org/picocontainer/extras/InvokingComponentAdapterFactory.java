package org.picocontainer.extras;

import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.*;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * A generic ComponentAdapter that will invoke a particular method
 * on the component after it has been instantiated. This can be useful
 * for implementing simple lifecycle support without the components
 * having to implement any interfaces.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InvokingComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    private String methodName;
    private Class[] parameterTypes;
    private Object[] arguments;

    public InvokingComponentAdapterFactory(ComponentAdapterFactory delegate,
                                           String methodName, Class[] parameterTypes, Object[] arguments) {
        super(delegate);
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new Adapter(super.createComponentAdapter(componentKey, componentImplementation, parameters));
    }

    public class Adapter extends DecoratingComponentAdapter {
        private Method method;

        public Object getInvocationResult() {
            return invocationResult;
        }

        private Object invocationResult;

        public Adapter(ComponentAdapter delegate) {
            super(delegate);
            try {
                method = delegate.getComponentImplementation().getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                method = null;
            } catch (final SecurityException e) {
                ///CLOVER:OFF
                method = null;
                ///CLOVER:ON                
            }
        }
    
        public Object getComponentInstance(AbstractPicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
            Object result = super.getComponentInstance(picoContainer);

            if( method != null ) {
                try {
                    invocationResult = method.invoke(result, arguments);
                } catch (IllegalAccessException e) {
                    throw new PicoInvocationTargetInitializationException(e);
                } catch (final IllegalArgumentException e) {
                    throw new PicoInitializationException() {
                        public String getMessage() {
                            return "Illegal argument:" + e.getMessage();
                        }
                    };
                } catch (InvocationTargetException e) {
                    throw new PicoInvocationTargetInitializationException(e.getTargetException());
                }
            }

            return result;
        }
    }
}
