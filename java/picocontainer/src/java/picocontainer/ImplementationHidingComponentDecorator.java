package picocontainer;

import java.lang.reflect.*;

public class ImplementationHidingComponentDecorator extends DefaultComponentFactory {
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
