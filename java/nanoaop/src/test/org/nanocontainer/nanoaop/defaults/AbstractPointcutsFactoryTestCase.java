/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.defaults;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.nanocontainer.nanoaop.ClassPointcut;
import org.nanocontainer.nanoaop.ComponentPointcut;
import org.nanocontainer.nanoaop.MethodPointcut;
import org.nanocontainer.nanoaop.PointcutsFactory;

/**
 * @author Stephen Molitor
 */
public class AbstractPointcutsFactoryTestCase extends TestCase {

    private static class ConcreteFactory extends AbstractPointcutsFactory {
        public MethodPointcut allMethods() {
            return null;
        }

        public ClassPointcut instancesOf(Class type) {
            return null;
        }

        public ClassPointcut className(String regex) {
            return null;
        }

        public MethodPointcut getMethods() {
            return null;
        }

        public ClassPointcut intersection(ClassPointcut a, ClassPointcut b) {
            return null;
        }

        public MethodPointcut intersection(MethodPointcut a, MethodPointcut b) {
            return null;
        }

        public MethodPointcut isMethods() {
            return null;
        }

        public ClassPointcut not(ClassPointcut classPointcut) {
            return null;
        }

        public MethodPointcut not(MethodPointcut methodPointcut) {
            return null;
        }

        public ClassPointcut oneClass(Class clazz) {
            return null;
        }

        public MethodPointcut oneMethod(Method method) {
            return null;
        }

        public ClassPointcut packageName(String packageName) {
            return null;
        }

        public MethodPointcut returnType(ClassPointcut classPointcut) {
            return null;
        }

        public MethodPointcut setMethods() {
            return null;
        }

        public MethodPointcut signature(String regexp) {
            return null;
        }

        public ClassPointcut union(ClassPointcut a, ClassPointcut b) {
            return null;
        }

        public MethodPointcut union(MethodPointcut a, MethodPointcut b) {
            return null;
        }
    }

    public void testComponent() {
        PointcutsFactory factory = new ConcreteFactory();

        ComponentPointcut pointcutA = factory.component("a");
        assertEquals("a", pointcutA.getComponentKey());

        ComponentPointcut pointcutB = factory.component("b");
        assertEquals("b", pointcutB.getComponentKey());
    }

}