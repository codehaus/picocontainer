/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class DefaultLifecycleManagerTestCase extends MockObjectTestCase {

    public void testShouldDelegateToStartVisitorTraverseOnStart() throws NoSuchMethodException {
        Mock visitor = mock(PicoVisitor.class);
        Mock pico = mock(PicoContainer.class);
        visitor.expects(once()).method("traverse").with(same(pico.proxy()));

        LifecycleManager lifecycleManager = new DefaultLifecycleManager((PicoVisitor) visitor.proxy(), null, null);
        lifecycleManager.start((PicoContainer) pico.proxy());
    }

    public void testShouldDelegateToStopVisitorTraverseOnStart() throws NoSuchMethodException {
        Mock visitor = mock(PicoVisitor.class);
        Mock pico = mock(PicoContainer.class);
        visitor.expects(once()).method("traverse").with(same(pico.proxy()));

        LifecycleManager lifecycleManager = new DefaultLifecycleManager(null, (PicoVisitor) visitor.proxy(), null);
        lifecycleManager.stop((PicoContainer) pico.proxy());
    }

    public void testShouldDelegateToDisposeVisitorTraverseOnStart() throws NoSuchMethodException {
        Mock visitor = mock(PicoVisitor.class);
        Mock pico = mock(PicoContainer.class);
        visitor.expects(once()).method("traverse").with(same(pico.proxy()));

        LifecycleManager lifecycleManager = new DefaultLifecycleManager(null, null, (PicoVisitor) visitor.proxy());
        lifecycleManager.dispose((PicoContainer) pico.proxy());
    }
}
