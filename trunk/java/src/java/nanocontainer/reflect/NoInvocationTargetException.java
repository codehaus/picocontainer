package nanocontainer.reflect;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Arrays;

/**
 *
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class NoInvocationTargetException extends Exception {
    private final Object proxy;
    private final Method method;

    public NoInvocationTargetException(Object proxy, Method method) {
        this.proxy = proxy;
        this.method = method;
    }

    public Object getProxy() {
        return proxy;
    }

    public Method getMethod() {
        return method;
    }

    public String getMessage() {
        List interfaces = Arrays.asList(proxy.getClass().getInterfaces());
        return method.toString() + " doesn't exist in any of the proxy's interfaces: " + interfaces;
    }
}
