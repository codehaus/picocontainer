package org.nanocontainer.proxy;

/**
 * @deprecated Will be replaced by http://proxytoys.cocehaus.org/
 */
public interface ProxyFactory {
    Object createProxy(Class classOrInterface, Class[] interfaces, InvocationInterceptor invocationInterceptor);
    boolean canProxy(Class type);
    boolean isProxyClass(Class clazz);
}