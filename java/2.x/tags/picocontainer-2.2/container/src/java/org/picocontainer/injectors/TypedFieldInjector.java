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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * Injection happens after instantiation, and fields are marked as
 * injection points via a field type.
 */
public class TypedFieldInjector extends IterativeInjector {

    /**
	 * Serialization UUID.
	 */

    private final List<String> classes;
    private static final long serialVersionUID = 4579165430483092396L;

    public TypedFieldInjector(Object key,
                                  Class<?> impl,
                                  Parameter[] parameters,
                                  ComponentMonitor componentMonitor,
                                  LifecycleStrategy lifecycleStrategy,
                                  String classNames) {

        super(key, impl, parameters, componentMonitor, lifecycleStrategy, true);
        this.classes = Arrays.asList(classNames.trim().split(" "));
    }

    protected void initializeInjectionMembersAndTypeLists() {
        injectionMembers = new ArrayList<AccessibleObject>();
        List<Annotation> bindingIds = new ArrayList<Annotation>();
        final List<Class> typeList = new ArrayList<Class>();
        final Field[] fields = getFields();
        for (final Field field : fields) {
            if (isTypedForInjection(field)) {
                injectionMembers.add(field);
                typeList.add(box(field.getType()));
                bindingIds.add(getBinding(field));
            }
        }
        injectionTypes = typeList.toArray(new Class[0]);
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

    protected boolean isTypedForInjection(Field field) {
        return classes.contains(field.getType().getName());
    }

    private Field[] getFields() {
        return (Field[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return getComponentImplementation().getDeclaredFields();
            }
        });
    }


    protected void injectIntoMember(AccessibleObject member, Object componentInstance, Object toInject)
        throws IllegalAccessException, InvocationTargetException {
        Field field = (Field) member;
        field.setAccessible(true);
        field.set(componentInstance, toInject);
    }

    public String getDescriptor() {
        return "TypedFieldInjector-";
    }

    protected NameBinding makeParameterNameImpl(final AccessibleObject member) {
        return new NameBinding() {
            public String getName() {
                return ((Field) member).getName();
            }
        };
    }

    List<String> getInjectionFieldTypes() {
        return Collections.unmodifiableList(classes);
    }


}