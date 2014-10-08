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
import org.picocontainer.NameBinding;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.annotations.Bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Injection happens after instantiation, and fields are marked as 
 * injection points via a named field.
 */
@SuppressWarnings("serial")
public class NamedFieldInjector<T> extends AbstractFieldInjector<T> {

    private final List<String> fieldNames;

    public NamedFieldInjector(Object key,
                                  Class<?> impl,
                                  Parameter[] parameters,
                                  ComponentMonitor componentMonitor,
                                  String fieldNames) {

        super(key, impl, parameters, componentMonitor, true);
        this.fieldNames = Arrays.asList(fieldNames.trim().split(" "));
    }

    @Override
    protected void initializeInjectionMembersAndTypeLists() {
        injectionMembers = new ArrayList<AccessibleObject>();
        List<Annotation> bindingIds = new ArrayList<Annotation>();
        final List<Type> typeList = new ArrayList<Type>();
        final Field[] fields = getFields();
        for (final Field field : fields) {
            if (isNamedForInjection(field)) {
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

    protected boolean isNamedForInjection(Field field) {
        return fieldNames.contains(field.getName());
    }

    private Field[] getFields() {
        return AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
            public Field[] run() {
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

    @Override
    public String getDescriptor() {
        return "NamedFieldInjector-";
    }

    @Override
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

    List<String> getInjectionFieldNames() {
        return Collections.unmodifiableList(fieldNames);
    }


}
