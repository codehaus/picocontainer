package picocontainer.extras;

import picocontainer.ComponentFactory;
import picocontainer.PicoInitializationException;
import picocontainer.PicoIntrospectionException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ImplementationHidingComponentFactory implements ComponentFactory {
    private final ComponentFactory componentFactory;

    public ImplementationHidingComponentFactory(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    public Object createComponent(Class componentType, Class componentImplementation, Class[] dependencies, Object[] instanceDependencies) throws PicoInitializationException {
        Object componentInstance = componentFactory.createComponent(componentType, componentImplementation, dependencies, instanceDependencies);
        return Proxy.newProxyInstance(componentImplementation.getClass().getClassLoader(), new Class[]{componentType}, new ImplementationHidingProxy(componentInstance));
    }

    public Class[] getDependencies(Class componentImplementation) throws PicoIntrospectionException {
        return componentFactory.getDependencies(componentImplementation);
    }

    private class ImplementationHidingProxy implements InvocationHandler {
        private Object componentInstance;

        public ImplementationHidingProxy(Object componentInstance) {
            if (componentInstance == null) {
                throw new NullPointerException("componentInstance can't be null");
            }
            this.componentInstance = componentInstance;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(componentInstance, args);
        }
    }
}
