package org.picocontainer.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Inject;
import org.picocontainer.defaults.LifecycleStrategy;

import java.lang.reflect.Method;
import java.io.Serializable;

public class AnnotationInjectionComponentAdapter extends SetterInjectionComponentAdapter {

    public AnnotationInjectionComponentAdapter(Serializable key, Class  impl, Parameter... params) {
        super(key, impl, params);
    }

    public AnnotationInjectionComponentAdapter(Object key, Class impl, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        super(key, impl, parameters, monitor, lifecycleStrategy);
    }


    protected final boolean isInjectorMethod(Method method) {
        return method.getAnnotation(Inject.class) != null;
    }
}
