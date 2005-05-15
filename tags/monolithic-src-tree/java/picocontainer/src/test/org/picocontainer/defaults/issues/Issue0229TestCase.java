/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Rettig                                           *
 *****************************************************************************/
package org.picocontainer.defaults.issues;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.VerifyingVisitor;

import junit.framework.TestCase;


public class Issue0229TestCase extends TestCase {
    public static class MockRunnable implements Runnable {
        public void run() {
        }
    }

    public static class OtherRunnable implements Runnable {
        public void run() {
        }
    }

    public static class MockRunner {
        private final Runnable[] _runners;

        public MockRunner(Runnable[] runnable) {
            _runners = runnable;
        }
    }

    public void testArrayDependenciesAndVerification() {
        DefaultPicoContainer container = new DefaultPicoContainer();
        container.registerComponentImplementation(MockRunnable.class);
        container.registerComponentImplementation(OtherRunnable.class);
        container.registerComponentImplementation(MockRunner.class);

        // this will fail to resolve the Runnable array on the MockRunner
        VerifyingVisitor visitor = new VerifyingVisitor();
        visitor.traverse(container);

        container.start();
        assertNotNull(container.getComponentInstanceOfType(MockRunner.class));
    }

}
