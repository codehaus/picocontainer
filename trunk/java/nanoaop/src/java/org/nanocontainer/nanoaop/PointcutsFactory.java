/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop;

import java.lang.reflect.Method;

/**
 * @author Stephen Molitor
 */
public interface PointcutsFactory {

    ComponentPointcut component(Object componentKey);

    ClassPointcut instancesOf(Class type);

    ClassPointcut className(String regex);

    ClassPointcut oneClass(Class clazz);

    ClassPointcut packageName(String packageName);

    ClassPointcut intersection(ClassPointcut a, ClassPointcut b);

    ClassPointcut union(ClassPointcut a, ClassPointcut b);

    ClassPointcut not(ClassPointcut classPointcut);

    MethodPointcut allMethods();

    MethodPointcut getMethods();

    MethodPointcut isMethods();

    MethodPointcut setMethods();

    MethodPointcut signature(String regexp);

    MethodPointcut oneMethod(Method method);

    MethodPointcut returnType(ClassPointcut classPointcut);

    MethodPointcut intersection(MethodPointcut a, MethodPointcut b);

    MethodPointcut union(MethodPointcut a, MethodPointcut b);

    MethodPointcut not(MethodPointcut methodPointcut);

}