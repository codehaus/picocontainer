package org.nanocontainer.multicast;

import java.lang.reflect.Method;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NullInvocationInterceptor implements InvocationInterceptor {
    public void intercept(Method method, Object target, Object[] args) {
        // do nothing
    }
}