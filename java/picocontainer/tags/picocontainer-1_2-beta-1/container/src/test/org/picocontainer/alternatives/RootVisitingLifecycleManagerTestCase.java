/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.alternatives;

import org.jmock.MockObjectTestCase;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class RootVisitingLifecycleManagerTestCase extends MockObjectTestCase {

    public void testNothing() {
        // My major problem with this testcase (renamed from DefaultLifecycleTestCase) is that is was not testing lifecycle
        // events.  Specifically start, stop and dispose.  It was insteat testing traversal. The clue is the mocking
        // only of the invocation of 'traverse' in each of the three nethods below.
        // As such, the methods should be called things like - testShouldDelegateToTraverseOperationTraverseOnStart
        // and alike. - Paul.
    }

//    public void testShouldDelegateToStartVisitorTraverseOnStart() throws NoSuchMethodException {
//        Mock visitor = mock(PicoVisitor.class);
//        Mock pico = mock(PicoContainer.class);
//        visitor.expects(once()).method("traverse").with(same(pico.proxy()));
//
//        LifecycleManager lifecycleManager = new RootVisitingLifecycleManager();
//        lifecycleManager.start((PicoContainer) pico.proxy());
//    }
//
//    public void testShouldDelegateToStopVisitorTraverseOnStart() throws NoSuchMethodException {
//        Mock visitor = mock(PicoVisitor.class);
//        Mock pico = mock(PicoContainer.class);
//        visitor.expects(once()).method("traverse").with(same(pico.proxy()));
//
//        LifecycleManager lifecycleManager = new RootVisitingLifecycleManager();
//        lifecycleManager.stop((PicoContainer) pico.proxy());
//    }
//
//    public void testShouldDelegateToDisposeVisitorTraverseOnStart() throws NoSuchMethodException {
//        Mock visitor = mock(PicoVisitor.class);
//        Mock pico = mock(PicoContainer.class);
//        visitor.expects(once()).method("traverse").with(same(pico.proxy()));
//
//        LifecycleManager lifecycleManager = new RootVisitingLifecycleManager();
//        lifecycleManager.dispose((PicoContainer) pico.proxy());
//    }
}
