package picocontainer.hierarchical;

import picocontainer.defaults.DefaultComponentFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class ImplementationHidingComponentFactory extends picocontainer.defaults.DefaultComponentFactory {
    public Object createComponent(Class compType, Constructor constructor, Object[] args)
            throws InvocationTargetException, IllegalAccessException, InstantiationException
    {
        Object component = super.createComponent(compType, constructor, args);
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {compType}, new ImplementationHidingProxy(component));
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
