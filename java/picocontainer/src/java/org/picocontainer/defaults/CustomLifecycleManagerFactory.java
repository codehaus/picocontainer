package org.picocontainer.defaults;

import org.picocontainer.defaults.LifecycleManagerFactory;
import org.picocontainer.defaults.CustomLifecycleManager;
import org.picocontainer.LifecycleManager;
import org.picocontainer.MutablePicoContainer;

import java.lang.reflect.Method;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CustomLifecycleManagerFactory implements LifecycleManagerFactory {
    private final Method startMethod;
    private final Method stopMethod;
    private final Method disposeMethod;

    public CustomLifecycleManagerFactory(Method startMethod, Method stopMethod, Method disposeMethod) {
        this.startMethod = startMethod;
        this.stopMethod = stopMethod;
        this.disposeMethod = disposeMethod;
    }

    public LifecycleManager createLifecycleManager(MutablePicoContainer pico) {
        return new CustomLifecycleManager(pico, startMethod, stopMethod, disposeMethod);
    }
}
