/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 06-Mar-2004
 * Time: 20:52:31
 */
package org.nanocontainer.proxy;


public interface ProxyFactory {
    Object createProxy(Class classOrInterface, Class[] interfaces, InvocationInterceptor invocationInterceptor);
    boolean canProxy(Class type);
    boolean isProxyClass(Class clazz);
}