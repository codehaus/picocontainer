package org.nanocontainer.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.io.Serializable;

/**
 * @deprecated Will be replaced by http://proxytoys.cocehaus.org/
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
class StandardInvocationInterceptorAdapter implements InvocationHandler, Serializable {
    private final InvocationInterceptor invocationInterceptor;

    public StandardInvocationInterceptorAdapter(InvocationInterceptor invocationInterceptor) {
        this.invocationInterceptor = invocationInterceptor;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invocationInterceptor.intercept(proxy, method, args);
    }
}