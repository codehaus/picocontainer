package org.picocontainer.defaults;

import org.picocontainer.PicoVisitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.Startable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Disposable;
import org.picocontainer.ComponentAdapter;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class LifecycleVisitor implements PicoVisitor {
    public static final PicoVisitor STARTER;
    public static final PicoVisitor STOPPER;
    public static final PicoVisitor DISPOSER;
    static {
        try {
            STARTER = new LifecycleVisitor(Startable.class.getMethod("start", null));
            STOPPER = new LifecycleVisitor(Startable.class.getMethod("stop", null));
            DISPOSER = new LifecycleVisitor(Disposable.class.getMethod("dispose", null)) {
                public void visitContainer(PicoContainer pico) {
                    super.visitContainer(pico);
                    if (pico.getParent() instanceof MutablePicoContainer) {
                        ((MutablePicoContainer) pico.getParent()).removeChildContainer(pico);
                    }
                }
            };
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.getMessage());
        }
    }

    private final Method method;

    public LifecycleVisitor(Method method) {
        this.method = method;
    }

    public void visitContainer(PicoContainer pico) {
    }

    public void visitComponentAdapter(ComponentAdapter componentAdapter) {
    }

    public void visitComponentInstance(Object o) {
        try {
            method.invoke(o, null);
        } catch (IllegalAccessException e) {
            throw new PicoIntrospectionException("Can't call " + method.getName() + " on " + o, e);
        } catch (InvocationTargetException e) {
            throw new PicoIntrospectionException("Failed when calling " + method.getName() + " on " + o, e.getTargetException());
        }
    }
}
