package org.nanocontainer.proxy;

import java.lang.reflect.Method;
import java.io.Serializable;

/**
 * @deprecated Will be replaced by http://proxytoys.cocehaus.org/
 */
public interface InvocationInterceptor extends Serializable {
    Object intercept(Object proxy, Method method, Object[] args) throws Throwable;
}