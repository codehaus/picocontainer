package org.nanocontainer.proxy;

import org.picocontainer.defaults.InterfaceFinder;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class StandardProxyFactory implements ProxyFactory {
    public Object createProxy(Class classOrInterface, Class[] interfaces, final InvocationInterceptor invocationInterceptor) {
        if(!classOrInterface.isInterface()) {
            Set interfaceSet = new HashSet();
            interfaceSet.addAll(Arrays.asList(new InterfaceFinder().getAllInterfaces(classOrInterface)));
            if(interfaces != null) {
                interfaceSet.addAll(Arrays.asList(interfaces));
            }
            interfaces = (Class[]) interfaceSet.toArray(new Class[interfaceSet.size()]);
        }
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new InvocationInterceptorAdapter(invocationInterceptor));
    }

    public boolean canProxy(Class type) {
        return type.isInterface();
    }

    public boolean isProxyClass(Class clazz) {
        return Proxy.isProxyClass(clazz);
    }

    private class InvocationInterceptorAdapter implements InvocationHandler {
        private final InvocationInterceptor invocationInterceptor;

        public InvocationInterceptorAdapter(InvocationInterceptor invocationInterceptor) {
            this.invocationInterceptor = invocationInterceptor;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return invocationInterceptor.intercept(proxy, method, args);
        }
    }
}