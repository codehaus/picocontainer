/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picocontainer.defaults;

import java.lang.reflect.Method;

public class NullInvocationInterceptor implements InvocationInterceptor {
    public void intercept(Method method, Object target, Object[] args) {
        // do nothing
    }
}