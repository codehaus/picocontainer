/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.alternatives;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.LifecycleManager;
import org.picocontainer.defaults.AbstractImplementationHidingPicoContainerTestCase;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * This is a demonstration that functionality equivalent to {@link ImplementationHidingPicoContainer}
 * can be obtained by composing a DPC with aIHCAF.
 *
 * @author Aslak Helles&oslash;y
 */
public class ImplementationHidingWithDefaultPicoContainerTestCase extends AbstractImplementationHidingPicoContainerTestCase {

    protected MutablePicoContainer createImplementationHidingPicoContainer() {
        return createPicoContainer(null);
    }

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new DefaultPicoContainer(new CachingComponentAdapterFactory(new ImplementationHidingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory(), false)), parent);
    }

    protected MutablePicoContainer createPicoContainer(PicoContainer parent, LifecycleManager lifecycleManager) {
        return new DefaultPicoContainer(new CachingComponentAdapterFactory(new ImplementationHidingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory(), false)), parent, lifecycleManager);
    }

    public void testStartStopAndDisposeNotCascadedtoRemovedChildren() {
        super.testStartStopAndDisposeNotCascadedtoRemovedChildren();
    }
}
