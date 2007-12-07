/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.parameters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.NameBinding;
import org.picocontainer.injectors.AbstractInjector;

import java.lang.annotation.Annotation;


/**
 * A ComponentParameter should be used to pass in a particular component as argument to a
 * different component's constructor. This is particularly useful in cases where several
 * components of the same type have been registered, but with a different key. Passing a
 * ComponentParameter as a parameter when registering a component will give PicoContainer a hint
 * about what other component to use in the constructor. Collecting parameter types are
 * supported for {@link java.lang.reflect.Array},{@link java.util.Collection}and
 * {@link java.util.Map}.
 * 
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Thomas Heller
 */
@SuppressWarnings("serial")
public class ComponentParameter
        extends BasicComponentParameter {

    /**
     * <code>DEFAULT</code> is an instance of ComponentParameter using the default constructor.
     */
    public static final ComponentParameter DEFAULT = new ComponentParameter();
    /**
     * Use <code>ARRAY</code> as {@link Parameter}for an Array that must have elements.
     */
    public static final ComponentParameter ARRAY = new ComponentParameter(false);
    /**
     * Use <code>ARRAY_ALLOW_EMPTY</code> as {@link Parameter}for an Array that may have no
     * elements.
     */
    public static final ComponentParameter ARRAY_ALLOW_EMPTY = new ComponentParameter(true);

    private final Parameter collectionParameter;

    /**
     * Expect a parameter matching a component of a specific key.
     * 
     * @param componentKey the key of the desired addComponent
     */
    public ComponentParameter(Object componentKey) {
        this(componentKey, null);
    }

    /**
     * Expect any scalar paramter of the appropriate type or an {@link java.lang.reflect.Array}.
     */
    public ComponentParameter() {
        this(false);
    }

    /**
     * Expect any scalar paramter of the appropriate type or an {@link java.lang.reflect.Array}.
     * Resolve the parameter even if no compoennt is of the array's component type.
     * 
     * @param emptyCollection <code>true</code> allows an Array to be empty
     */
    public ComponentParameter(boolean emptyCollection) {
        this(null, emptyCollection ? CollectionComponentParameter.ARRAY_ALLOW_EMPTY : CollectionComponentParameter.ARRAY);
    }

    /**
     * Expect any scalar paramter of the appropriate type or the collecting type
     * {@link java.lang.reflect.Array},{@link java.util.Collection}or {@link java.util.Map}.
     * The components in the collection will be of the specified type.
     * 
     * @param componentValueType the component's type (ignored for an Array)
     * @param emptyCollection <code>true</code> allows the collection to be empty
     */
    public ComponentParameter(Class componentValueType, boolean emptyCollection) {
        this(null, new CollectionComponentParameter(componentValueType, emptyCollection));
    }

    /**
     * Expect any scalar paramter of the appropriate type or the collecting type
     * {@link java.lang.reflect.Array},{@link java.util.Collection}or {@link java.util.Map}.
     * The components in the collection will be of the specified type and their adapter's key
     * must have a particular type.
     * 
     * @param componentKeyType the component adapter's key type
     * @param componentValueType the component's type (ignored for an Array)
     * @param emptyCollection <code>true</code> allows the collection to be empty
     */
    public ComponentParameter(Class componentKeyType, Class componentValueType, boolean emptyCollection) {
        this(null, new CollectionComponentParameter(componentKeyType, componentValueType, emptyCollection));
    }

    private ComponentParameter(Object componentKey, Parameter collectionParameter) {
        super(componentKey);
        this.collectionParameter = collectionParameter;
    }

    public  <T> T resolveInstance(PicoContainer container,
                                  ComponentAdapter adapter,
                                  Class<T> expectedType,
                                  NameBinding expectedNameBinding,
                                  boolean useNames, Annotation binding) {
        // type check is done in isResolvable
        T result = super.resolveInstance(container, adapter, expectedType, expectedNameBinding, useNames, binding);
        if (result == null && collectionParameter != null) {
            result = collectionParameter.resolveInstance(container, adapter, expectedType, expectedNameBinding,
                                                         useNames, binding);
        }
        return result;
    }

    public boolean isResolvable(PicoContainer container,
                                ComponentAdapter adapter,
                                Class expectedType,
                                NameBinding expectedNameBinding,
                                boolean useNames, Annotation binding) {
        if (!super.isResolvable(container, adapter, expectedType, expectedNameBinding, useNames, binding)) {
            if (collectionParameter != null) {
                return collectionParameter.isResolvable(container, adapter, expectedType, expectedNameBinding,
                                                        useNames, binding);
            }
            return false;
        }
        return true;
    }

    public void verify(PicoContainer container,
                       ComponentAdapter adapter,
                       Class expectedType,
                       NameBinding expectedNameBinding,
                       boolean useNames, Annotation binding) {
        try {
            super.verify(container, adapter, expectedType, expectedNameBinding, useNames, binding);
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            if (collectionParameter != null) {
                collectionParameter.verify(container, adapter, expectedType, expectedNameBinding, useNames, binding);
                return;
            }
            throw e;
        }
    }

    /**
     * Accept the visitor for the current {@link Parameter}. If internally a
     * {@link CollectionComponentParameter}is used, it is visited also.
     * 
     * @see BasicComponentParameter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(PicoVisitor visitor) {
        super.accept(visitor);
        if (collectionParameter != null) {
            collectionParameter.accept(visitor);
        }
    }
}
