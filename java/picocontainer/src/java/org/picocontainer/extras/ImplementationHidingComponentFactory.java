package org.picocontainer.extras;

import org.picocontainer.ComponentFactory;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.ComponentSpecification;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ImplementationHidingComponentFactory implements ComponentFactory {
    private final ComponentFactory componentFactory;

    public ImplementationHidingComponentFactory(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    public Object createComponent(ComponentSpecification componentSpec, Object[] instanceDependencies) throws PicoInitializationException {
        Object componentInstance = componentFactory.createComponent(componentSpec, instanceDependencies);
        // TODO: search for all interfaces for component-implementation instead
        Class[] interfaces = new Class[]{ (Class) componentSpec.getComponentKey() };
        return Proxy.newProxyInstance(componentSpec.getComponentImplementation().getClassLoader(),
                interfaces, new ImplementationHidingProxy(componentInstance));
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
