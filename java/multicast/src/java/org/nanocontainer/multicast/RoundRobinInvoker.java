package org.picoextras.multicast;

import org.picocontainer.defaults.Invoker;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class RoundRobinInvoker implements Invoker {
    private int current = 0;

    public void invoke(Object[] targets, Class declaringClass, Method method, Object[] args, List results) throws IllegalAccessException, InvocationTargetException {
        results.add(method.invoke(targets[current], args));
        current++;
        current = current % targets.length;
    }
}
