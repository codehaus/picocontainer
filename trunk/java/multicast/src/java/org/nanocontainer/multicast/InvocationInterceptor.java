/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 26-Jan-2004
 * Time: 21:14:38
 */
package org.picoextras.multicast;

import java.lang.reflect.Method;

public interface InvocationInterceptor {
    void intercept(Method method, Object target, Object[] args);
}