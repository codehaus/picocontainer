package org.picoextras.multicast;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulticastInvoker implements Invoker {
    public void invoke(Object[] targets, Class declaringClass, Method method, Object[] args, List results, InvocationInterceptor invocationInterceptor) throws IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < targets.length; i++) {
            final Object target = targets[i];
            boolean isValidType = declaringClass.isAssignableFrom(target.getClass());
            if (isValidType) {
                // It's ok to call the method on this one
                Object componentResult = method.invoke(target, args);
                invocationInterceptor.intercept(method, target, args);
                results.add(componentResult);
            }
        }
    }
}
