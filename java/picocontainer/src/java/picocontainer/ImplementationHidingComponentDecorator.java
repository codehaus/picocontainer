package picocontainer;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ImplementationHidingComponentDecorator implements ComponentDecorator {
    public Object decorateComponent(Class compType, Object instance) {
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {compType}, new ImplementationHidingProxy(instance));
    }

    private class ImplementationHidingProxy implements InvocationHandler {
        private Object componentInstance;

        public ImplementationHidingProxy(Object componentInstance) {
            this.componentInstance = componentInstance;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(componentInstance, args);
        }
    }
}
