package org.nanocontainer.multicast;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class RoundRobinInvoker implements Invoker {
    private int current = 0;

    public void invoke(Object[] targets, Class declaringClass, Method method, Object[] args, List results, InvocationInterceptor invocationInterceptor) throws IllegalAccessException, InvocationTargetException {
        results.add(method.invoke(targets[current], args));
        invocationInterceptor.intercept(method, targets[current], args);
        current++;
        current = current % targets.length;
    }
}
