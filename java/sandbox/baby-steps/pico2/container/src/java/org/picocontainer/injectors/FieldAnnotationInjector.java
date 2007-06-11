package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoVisitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class FieldAnnotationInjector extends ConstructorInjector {

    ConstructorInjector ctorInjector;

    public FieldAnnotationInjector(Object key,
                                   Class impl, Parameter[] parameters, ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy) {

        super(key, makeSubclassForConstructor(impl), parameters, componentMonitor, lifecycleStrategy);

    }

    private static Class makeSubclassForConstructor(Class impl) {
        return impl;
    }

}
