/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.NameBinding;
import org.picocontainer.annotations.Bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Injection happens after instantiation, and through fields marked as injection points via an Annotation.
 * The default annotation of org.picocontainer.annotations.@Inject can be overridden.
 */
@SuppressWarnings("serial")
public class AnnotatedFieldInjector extends IterativeInjector {

 	
	private final Class<? extends Annotation> injectionAnnotation;

    public AnnotatedFieldInjector(Object key,
                                  Class<?> impl,
                                  Parameter[] parameters,
                                  ComponentMonitor componentMonitor,
                                  LifecycleStrategy lifecycleStrategy, 
                                  Class<? extends Annotation> injectionAnnotation, boolean useNames) {

        super(key, impl, parameters, componentMonitor, lifecycleStrategy, useNames);
        this.injectionAnnotation = injectionAnnotation;
    }

    protected void initializeInjectionMembersAndTypeLists() {
        injectionMembers = new ArrayList<AccessibleObject>();
        List<Annotation> bindingIds = new ArrayList<Annotation>();
        final List<Type> typeList = new ArrayList<Type>();
        final Field[] fields = getFields();
        for (final Field field : fields) {
            if (isAnnotatedForInjection(field)) {
                injectionMembers.add(field);
                typeList.add(box(field.getType()));
                bindingIds.add(getBinding(field));
            }
        }
        injectionTypes = typeList.toArray(new Type[0]);
        bindings = bindingIds.toArray(new Annotation[0]);
    }

    private Annotation getBinding(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Bind.class)) {
                return annotation;
            }
        }
        return null;
    }

    protected boolean isAnnotatedForInjection(Field field) {
        return field.getAnnotation(injectionAnnotation) != null;
    }

    private Field[] getFields() {
        return (Field[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return getComponentImplementation().getDeclaredFields();
            }
        });
    }


    protected Object injectIntoMember(AccessibleObject member, Object componentInstance, Object toInject)
        throws IllegalAccessException, InvocationTargetException {
        Field field = (Field) member;
        field.setAccessible(true);
        field.set(componentInstance, toInject);
        return null;
    }

    public String getDescriptor() {
        return "AnnotatedFieldInjector-";
    }

    protected NameBinding makeParameterNameImpl(final AccessibleObject member) {
        return new NameBinding() {
            public String getName() {
                return ((Field) member).getName();
            }
        };
    }

    protected Object memberInvocationReturn(Object lastReturn, AccessibleObject member, Object instance) {
        return instance;
    }
}
