package org.picocontainer.adapters;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Inject;
import org.picocontainer.LifecycleStrategy;

import java.lang.reflect.Method;
import java.io.Serializable;

public class AnnotationInjectionAdapter extends SetterInjectionAdapter {

    public AnnotationInjectionAdapter(Serializable key, Class  impl, Parameter... params) {
        super(key, impl, params);
    }

    public AnnotationInjectionAdapter(Object key, Class impl, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        super(key, impl, parameters, monitor, lifecycleStrategy);
    }


    protected final boolean isInjectorMethod(Method method) {
        return method.getAnnotation(Inject.class) != null;
    }
}
