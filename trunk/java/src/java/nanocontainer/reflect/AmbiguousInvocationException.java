package nanocontainer.reflect;

import java.lang.reflect.Method;

/**
 *
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class AmbiguousInvocationException extends Exception {
    private final Object firstComponent;
    private final Object secondComponent;
    private final Method method;

    public AmbiguousInvocationException(Object firstComponent, Object secondComponent, Method method) {
        this.firstComponent = firstComponent ;
        this.secondComponent = secondComponent;
        this.method = method;
    }

    public Object getFirstComponent() {
        return firstComponent;
    }

    public Object getSecondComponent() {
        return secondComponent;
    }

    public String getMessage() {
        return "Ambiguous invocation of" + method + ": " + firstComponent.getClass().getName() + ", " + secondComponent.getClass().getName();
    }
}
