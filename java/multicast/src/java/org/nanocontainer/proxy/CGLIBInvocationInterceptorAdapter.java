package org.nanocontainer.proxy;

import net.sf.cglib.proxy.InvocationHandler;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @deprecated Will be replaced by http://proxytoys.cocehaus.org/
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
class CGLIBInvocationInterceptorAdapter implements InvocationHandler, Serializable {
    private final InvocationInterceptor invocationInterceptor;

    public CGLIBInvocationInterceptorAdapter(InvocationInterceptor invocationInterceptor) {
        this.invocationInterceptor = invocationInterceptor;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invocationInterceptor.intercept(proxy, method, args);
    }
}