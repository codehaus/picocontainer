/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.dynaop;

import java.lang.reflect.Method;

import org.apache.oro.text.regex.MalformedPatternException;
import org.nanocontainer.nanoaop.ClassPointcut;
import org.nanocontainer.nanoaop.MalformedRegularExpressionException;
import org.nanocontainer.nanoaop.MethodPointcut;
import org.nanocontainer.nanoaop.defaults.AbstractPointcutsFactory;

import dynaop.Pointcuts;

/**
 * Implements the <code>org.nanocontainer.nanoaop.PointcutsFactory</code>
 * interface using dynaop.
 * 
 * @author Stephen Molitor
 * @version $Revision$
 */
public class DynaopPointcutsFactory extends AbstractPointcutsFactory {

    public ClassPointcut instancesOf(Class type) {
        return new DynaopClassPointcut(Pointcuts.instancesOf(type));
    }

    public ClassPointcut className(String regex) {
        try {
            return new DynaopClassPointcut(Pointcuts.className(regex));
        } catch (MalformedPatternException e) {
            throw new MalformedRegularExpressionException("malformed class name regular expression", e);
        }
    }

    public ClassPointcut oneClass(Class clazz) {
        return new DynaopClassPointcut(Pointcuts.singleton(clazz));
    }

    public ClassPointcut packageName(String packageName) {
        return new DynaopClassPointcut(Pointcuts.packageName(packageName));
    }

    public ClassPointcut intersection(ClassPointcut a, ClassPointcut b) {
        return new DynaopClassPointcut(Pointcuts.intersection((dynaop.ClassPointcut) a, (dynaop.ClassPointcut) b));
    }

    public ClassPointcut union(ClassPointcut a, ClassPointcut b) {
        return new DynaopClassPointcut(Pointcuts.union((dynaop.ClassPointcut) a, (dynaop.ClassPointcut) b));
    }

    public ClassPointcut not(ClassPointcut classPointcut) {
        return new DynaopClassPointcut(Pointcuts.not((dynaop.ClassPointcut) classPointcut));
    }

    public MethodPointcut allMethods() {
        return new DynaopMethodPointcut(Pointcuts.ALL_METHODS);
    }

    public MethodPointcut getMethods() {
        return new DynaopMethodPointcut(Pointcuts.GET_METHODS);
    }

    public MethodPointcut isMethods() {
        return new DynaopMethodPointcut(Pointcuts.IS_METHODS);
    }

    public MethodPointcut setMethods() {
        return new DynaopMethodPointcut(Pointcuts.SET_METHODS);
    }

    public MethodPointcut returnType(ClassPointcut classPointcut) {
        return new DynaopMethodPointcut(Pointcuts.returnType((dynaop.ClassPointcut) classPointcut));
    }

    public MethodPointcut signature(String regexp) {
        try {
            return new DynaopMethodPointcut(Pointcuts.signature(regexp));
        } catch (MalformedPatternException e) {
            throw new MalformedRegularExpressionException("malformed method signature regular expression", e);
        }
    }

    public MethodPointcut oneMethod(Method method) {
        return new DynaopMethodPointcut(Pointcuts.singleton(method));
    }

    public MethodPointcut intersection(MethodPointcut a, MethodPointcut b) {
        return new DynaopMethodPointcut(Pointcuts.intersection((dynaop.MethodPointcut) a, (dynaop.MethodPointcut) b));
    }

    public MethodPointcut union(MethodPointcut a, MethodPointcut b) {
        return new DynaopMethodPointcut(Pointcuts.union((dynaop.MethodPointcut) a, (dynaop.MethodPointcut) b));
    }

    public MethodPointcut not(MethodPointcut MethodPointcut) {
        return new DynaopMethodPointcut(Pointcuts.not((dynaop.MethodPointcut) MethodPointcut));
    }

}