/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.aop.dynaop;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.aop.AspectableNodeBuilderDecorator;
import org.picocontainer.aop.AspectablePicoContainer;
import org.picocontainer.aop.AspectsContainer;
import org.picocontainer.aop.AspectsManager;

import dynaop.Aspects;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;

/**
 * Dynaop-based AspectableNodeBuilderDecorator
 * 
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public class DynaopAspectableNodeBuilderDecorator extends AspectableNodeBuilderDecorator {

    public DynaopAspectableNodeBuilderDecorator(AspectsManager aspectsManager) {
        super(aspectsManager);
    }

    protected AspectablePicoContainer mixinAspectablePicoContainer(AspectsManager aspectsManager,
            MutablePicoContainer pico) {
        Aspects aspects = new Aspects();
        aspects.mixin(Pointcuts.ALL_CLASSES, new Class[] { AspectsContainer.class }, new InstanceMixinFactory(
                aspectsManager));
        aspects.interfaces(Pointcuts.ALL_CLASSES, new Class[] { AspectablePicoContainer.class });
        return (AspectablePicoContainer) new ProxyFactory(aspects).wrap(pico);
    }

}
