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

import java.util.Properties;

import org.nanocontainer.nanoaop.AspectablePicoContainer;
import org.nanocontainer.nanoaop.AspectablePicoContainerFactory;
import org.nanocontainer.nanoaop.AspectsContainer;
import org.nanocontainer.nanoaop.AspectsManager;
import org.nanocontainer.nanoaop.defaults.AspectsComponentAdapterFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import dynaop.Aspects;
import dynaop.MixinFactory;
import dynaop.Pointcuts;
import dynaop.Proxy;
import dynaop.ProxyFactory;

/**
 * @author Stephen Molitor
 */
public class DynaopAspectablePicoContainerFactory implements AspectablePicoContainerFactory {

    public AspectablePicoContainer createContainer() {
        final AspectsManager aspectsManager = new DynaopAspectsManager();
        ComponentAdapterFactory caFactory = new AspectsComponentAdapterFactory(aspectsManager);
        MutablePicoContainer pico = new DefaultPicoContainer(caFactory);
        Aspects aspects = new Aspects();
        aspects.mixin(Pointcuts.ALL_CLASSES, new Class[] { AspectsContainer.class }, new MixinFactory() {
            public Object create(Proxy proxy) {
                return aspectsManager;
            }

            public Properties getProperties() {
                return new Properties();
            }
        });
        aspects.interfaces(Pointcuts.ALL_CLASSES, new Class[] { AspectablePicoContainer.class });
        return (AspectablePicoContainer) ProxyFactory.getInstance(aspects).wrap(pico);
    }

    public AspectablePicoContainer createContainer(MutablePicoContainer delegate) {
        return null;
    }

}