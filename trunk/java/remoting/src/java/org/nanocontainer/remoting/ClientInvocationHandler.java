package org.nanocontainer.remoting;

import org.nanocontainer.proxy.InvocationInterceptor;
import org.nanocontainer.remoting.rmi.RemoteInterceptorImpl;

import java.lang.reflect.Method;

/**
 * @author Neil Clayton
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ClientInvocationHandler implements InvocationInterceptor {
    private final ByRefKey key;
    private final RemotingInterceptor interceptor;
    private static Method getKey;
    {
        try {
            getKey = KeyHolder.class.getMethod("getKey", null);
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }

    public ClientInvocationHandler(ByRefKey key, RemoteInterceptorImpl interceptor) {
        this.key = key;
        this.interceptor = interceptor;
    }

    public Object intercept(Object proxy, Method method, Object[] args) throws Throwable {
        if(getKey.equals(method)) {
            return key;
        }
        Invocation invocation = new Invocation(method.getName(), method.getParameterTypes(), args);
        return interceptor.invoke(invocation);
    }
}