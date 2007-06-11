package org.picocontainer.injectors;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Inject;
import org.picocontainer.LifecycleStrategy;

import java.lang.reflect.Method;
import java.io.Serializable;

public class AnnotationInjector extends SetterInjector {

    public AnnotationInjector(Object key, Class impl, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        super(key, impl, parameters, monitor, lifecycleStrategy);
    }


    protected final boolean isInjectorMethod(Method method) {
        return method.getAnnotation(Inject.class) != null;
    }
}
