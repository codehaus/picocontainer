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

import org.jmock.MockObjectTestCase;
import org.nanocontainer.nanoaop.IdentifiableMixin;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import dynaop.MixinFactory;

/**
 * @author Stephen Molitor
 */
public class ContainerSuppliedMixinFactoryTestCase extends MockObjectTestCase {

    private MutablePicoContainer pico = new DefaultPicoContainer();
    private MixinFactory mixinFactory = new ContainerSuppliedMixinFactory(pico, "mixinComponentKey");
    
    public void testCreate() {
        IdentifiableMixin mixin = new IdentifiableMixin();
        pico.registerComponentInstance("mixinComponentKey", mixin);
        
        IdentifiableMixin actualMixin = (IdentifiableMixin) mixinFactory.create(null);
        assertNotNull(actualMixin);
        assertSame(mixin, actualMixin);
    }
    
    public void testMixinNotInContainer() {
        try {
            mixinFactory.create(null);
            fail("NullPointerException should hvae been raised");
        } catch (NullPointerException e) {
        }
    }
    
    public void testPropertiesNotNull() {
        assertNotNull(mixinFactory.getProperties());
    }
    
}
