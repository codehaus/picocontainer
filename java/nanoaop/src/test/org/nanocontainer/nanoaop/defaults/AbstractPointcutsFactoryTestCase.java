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
    }
    
    public void testComponent() {
        PointcutsFactory factory = new ConcreteFactory();
        
        ComponentPointcut pointcutA = factory.component("a");
        assertEquals("a", pointcutA.getComponentKey());
        
        ComponentPointcut pointcutB = factory.component("b");
        assertEquals("b", pointcutB.getComponentKey());
    }

}