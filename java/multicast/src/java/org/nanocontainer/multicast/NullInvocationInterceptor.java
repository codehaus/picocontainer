package org.nanocontainer.multicast;

import org.nanocontainer.proxy.InvocationInterceptor;

import java.lang.reflect.Method;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NullInvocationInterceptor implements InvocationInterceptor {
    public Object intercept(Object proxy, Method method, Object[] args) {
        // do nothing
        return null;
    }
}