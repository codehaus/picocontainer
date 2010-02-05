/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.annotations.Bind;

import static org.picocontainer.injectors.PrimitiveMemberChecker.isPrimitiveArgument;

/**
 * Injection will happen in a single member function on the component.
 *
 * @author Paul Hammant 
 * 
 */
@SuppressWarnings("serial")
public abstract class SingleMemberInjector<T> extends AbstractInjector<T> {

    private transient Paranamer paranamer;

    public SingleMemberInjector(Object componentKey,
                                Class componentImplementation,
                                Parameter[] parameters,
                                ComponentMonitor monitor,
                                boolean useNames) {
        super(componentKey, componentImplementation, parameters, monitor, useNames);
    }

    protected Paranamer getParanamer() {
        if (paranamer == null) {
            paranamer = new CachingParanamer(new AnnotationParanamer(new AdaptiveParanamer()));
        }
        return paranamer;
    }

    @SuppressWarnings("unchecked")
    protected Object[] getMemberArguments(PicoContainer container, final AccessibleObject member, final Type[] parameterTypes, final Annotation[] bindings) {
        boxParameters(parameterTypes);
        Object[] result = new Object[parameterTypes.length];
        final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

        for (int i = 0; i < currentParameters.length; i++) {
            result[i] = getParameter(container, member, i, parameterTypes[i], bindings[i], currentParameters[i], null);
        }

        return result;
    }

    protected void boxParameters(Type[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = box(parameterTypes[i]);
        }
    }

    protected Object getParameter(PicoContainer container, AccessibleObject member, int i, Type parameterType, Annotation binding,
                                  Parameter currentParameter, ComponentAdapter<?> injecteeAdapter) {
        ParameterNameBinding expectedNameBinding = new ParameterNameBinding(getParanamer(), member, i);
        Object result = currentParameter.resolve(container, this, injecteeAdapter, parameterType, expectedNameBinding, useNames(), binding).resolveInstance();
        nullCheck(member, i, expectedNameBinding, result);
        return result;
    }

    @SuppressWarnings("synthetic-access")
    protected void nullCheck(AccessibleObject member, int i, ParameterNameBinding expectedNameBinding, Object result) {
        if (result == null && !isNullParamAllowed(member, i)) {
            throw new ParameterCannotBeNullException(i, member, expectedNameBinding.getName());
        }
    }

    /**
     * Checks to see if a null parameter is allowed in the given
     * constructor/field/method.  The default version allows null 
     * if the target object is not a primitive type.
     * @param member constructor method or field
     * @param i parameter #.
     * @return true if the null parameter might be allowed.
     */
    protected boolean isNullParamAllowed(AccessibleObject member, int i) {
        return !(isPrimitiveArgument(member, i));
    }

    protected Annotation[] getBindings(Annotation[][] annotationss) {
        Annotation[] retVal = new Annotation[annotationss.length];
        for (int i = 0; i < annotationss.length; i++) {
            Annotation[] annotations = annotationss[i];
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().getAnnotation(Bind.class) != null) {
                    retVal[i] = annotation;
                    break;
                }
            }
        }
        return retVal;
    }

    public static class ParameterCannotBeNullException extends PicoCompositionException {
        private final String name;
        private ParameterCannotBeNullException(int ix, AccessibleObject member, String name) {
            super("Parameter " + ix + " of '" + member + "' named '" + name + "' cannot be null");
            this.name = name;
        }
        public String getParameterName() {
            return name;
        }
    }

}
