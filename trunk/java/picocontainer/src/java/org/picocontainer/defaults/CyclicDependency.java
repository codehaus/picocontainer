/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.picocontainer.defaults;

import java.io.Serializable;

/**
 * Abstract utility class to detect recursion cycles.
 * Derive from this class and implement {@link CyclicDependency#run}. 
 * The method will be called by  {@link CyclicDependency#observe}. Select
 * an appropriate guard for your scope. Any {@link ObjectReference} can be 
 * used as long as it is initialized with  <code>Boolean.FALSE</code>.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.1
 */
public abstract class CyclicDependency {

    /**
     * A guard for a CyclicDependency that is based on a {@link ThreadLocal}.
     */
    public static class ThreadLocalGuard extends ThreadLocal implements ObjectReference, Serializable {
        /** Initialize with <code>Boolean.FALSE</code>. */
        protected synchronized Object initialValue() {
            return Boolean.FALSE;
        }
    }
    /**
     * A simple guard for a CyclicDependency.
     */
    public  static class SimpleGuard extends SimpleReference {
        /** Construct a SimpleGuard and initialize with <code>Boolean.FALSE</code>. */
        public SimpleGuard() {
            set(Boolean.FALSE);
        }
    };

    /**
     * Derive from this class and implement this function with the functionality 
     * to observe for a dependency cycle.
     * 
     * @return a value, if the functionality result in an expression, 
     *      otherwise just return <code>null</code>
     */
    public abstract Object run();
    
    /**
     * Call the observing function. The provided guard will hold the {@link Boolean} value.
     * If the guard is already <code>Boolean.TRUE</code> a {@link CyclicDependencyException} 
     * will be  thrown.
     * 
     * @param guard the guard for the observation.
     * @param stackFrame the current stack frame
     * @param cyclicDependency the {@link CyclicDependency} instance whose 
     *      <code>run</code> method will be called.
     * @return the result of the <code>run</code> method
     */
    public static final Object observe(ObjectReference guard, Class stackFrame, CyclicDependency cyclicDependency) {
        if (Boolean.TRUE.equals(guard.get())) {
            throw new CyclicDependencyException(stackFrame);
        }
        Object result = null;
        try {
            guard.set(Boolean.TRUE);
            result = cyclicDependency.run();
        } catch (final CyclicDependencyException e) {
            e.push(stackFrame);
            throw e;
        } finally {
            guard.set(Boolean.FALSE);
        }
        return result;
    }
}
