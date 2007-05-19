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

public class AnnotationComponentAdapter extends SetterInjectionComponentAdapter {

    public AnnotationComponentAdapter(Serializable key, Class  impl, Parameter... params) {
        super(key, impl, params);
    }


    protected final boolean isInjectorMethod(Method method) {
        return method.getAnnotation(Inject.class) != null;
    }
}
