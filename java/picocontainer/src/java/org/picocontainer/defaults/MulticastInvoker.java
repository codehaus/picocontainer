package org.picocontainer.defaults;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulticastInvoker implements Invoker {
    public void invoke(Object[] targets, Class declaringClass, Method method, Object[] args, List results) throws IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < targets.length; i++) {
            boolean isValidType = declaringClass.isAssignableFrom(targets[i].getClass());
            if (isValidType) {
                // It's ok to call the method on this one
                Object componentResult = method.invoke(targets[i], args);
                results.add(componentResult);
            }
        }
    }
}
