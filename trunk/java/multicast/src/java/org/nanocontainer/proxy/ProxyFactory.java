package org.nanocontainer.proxy;


public interface ProxyFactory {
    Object createProxy(Class classOrInterface, Class[] interfaces, InvocationInterceptor invocationInterceptor);
    boolean canProxy(Class type);
    boolean isProxyClass(Class clazz);
}