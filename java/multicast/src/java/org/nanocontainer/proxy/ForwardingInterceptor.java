package org.nanocontainer.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ForwardingInterceptor implements InvocationInterceptor {
    private final Object target;

    public ForwardingInterceptor(Object target) {
        this.target = target;
    }

    public Object intercept(Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        return method.invoke(target, args);
    }
}