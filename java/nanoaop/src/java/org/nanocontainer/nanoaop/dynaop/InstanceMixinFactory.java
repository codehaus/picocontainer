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

import dynaop.MixinFactory;
import dynaop.Proxy;

/**
 * @author Stephen Molitor
 */
public class InstanceMixinFactory implements MixinFactory {
    private final Object instance;

    public InstanceMixinFactory(Object instance) {
        this.instance = instance;
    }

    public Object create(Proxy proxy) {
        return instance;
    }

    public Properties getProperties() {
        return new Properties();
    }

}