package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This LifecycleManager allows components to implement any kind of lifecycle methods,
 * so that the default {@link org.picocontainer.Startable} and {@link org.picocontainer.Disposable}
 * methods don't have to be implemented.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CustomLifecycleManager extends DefaultLifecycleManager {
    private final Method startMethod;
    private final Method stopMethod;
    private final Method disposeMethod;

    public CustomLifecycleManager(MutablePicoContainer pico, Method startMethod, Method stopMethod, Method disposeMethod) {
        super(pico, startMethod.getDeclaringClass(), stopMethod.getDeclaringClass(), disposeMethod.getDeclaringClass());
        this.startMethod = startMethod;
        this.stopMethod = stopMethod;
        this.disposeMethod = disposeMethod;
    }

    protected void startComponent(Object o) {
        try {
            startMethod.invoke(o, null);
        } catch (IllegalAccessException e) {
            throw new PicoIntrospectionException("Can't call " + startMethod.getName() + " on " + o, e);
        } catch (InvocationTargetException e) {
            throw new PicoIntrospectionException("Failed when calling " + startMethod.getName() + " on " + o, e.getTargetException());
        }
    }

    protected void stopComponent(Object o) {
        try {
            stopMethod.invoke(o, null);
        } catch (IllegalAccessException e) {
            throw new PicoIntrospectionException("Can't call " + stopMethod.getName() + " on " + o, e);
        } catch (InvocationTargetException e) {
            throw new PicoIntrospectionException("Failed when calling " + stopMethod.getName() + " on " + o, e.getTargetException());
        }
    }

    protected void disposeComponent(Object o) {
        try {
            disposeMethod.invoke(o, null);
        } catch (IllegalAccessException e) {
            throw new PicoIntrospectionException("Can't call " + disposeMethod.getName() + " on " + o, e);
        } catch (InvocationTargetException e) {
            throw new PicoIntrospectionException("Failed when calling " + disposeMethod.getName() + " on " + o, e.getTargetException());
        }
    }
}
