/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picoextras.multicast;

import org.picoextras.multicast.InvocationInterceptor;

import java.lang.reflect.Method;

public class NullInvocationInterceptor implements InvocationInterceptor {
    public void intercept(Method method, Object target, Object[] args) {
        // do nothing
    }
}