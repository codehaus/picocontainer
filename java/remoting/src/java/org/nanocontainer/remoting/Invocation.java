package org.nanocontainer.remoting;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

/**
 * @author Neil Clayton
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class Invocation implements Serializable {
    private String methodName;
    private Class[] parameterTypes;
    private Object[] args;

    public Invocation(String methodName, Class[] parameterTypes, Object[] args) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    public Object invoke(Object target) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = target.getClass().getMethod(methodName, parameterTypes);
        return method.invoke(target, args);
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}