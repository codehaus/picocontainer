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

import org.picocontainer.PicoContainer;

import dynaop.MixinFactory;
import dynaop.Proxy;

/**
 * Manufactures mixins from a <code>PicoContainer</code>. Useful when a mixin
 * has dependencies on other components in the <code>PicoContainer</code>.
 * 
 * @author Stephen Molitor
 * @version $Revision$
 */
class ContainerSuppliedMixinFactory implements MixinFactory {

    private final PicoContainer pico;
    private final Object mixinComponentKey;

    /**
     * Creates a new <code>ContainerSuppliedMixinFactory</code> that will
     * manufacture mixins by retrieving them from the <code>PicoContainer</code>
     * using a given component key.
     * 
     * @param pico the <code>PicoContainer</code> to retrieve the mixin from.
     * @param mixinComponentKey the component key that will be used to retrieve
     *        the mixin object from the pico container.
     */
    ContainerSuppliedMixinFactory(PicoContainer pico, Object mixinComponentKey) {
        this.pico = pico;
        this.mixinComponentKey = mixinComponentKey;
    }

    /**
     * Manufactures a <code>Mixin</code> by retrieving it from the
     * <code>PicoContainer</code>.
     * 
     * @param proxy the proxy that the interceptor will wrap.
     * @return the <code>Mixin</code> object.
     * @throws NullPointerException if the mixin can not be found in the pico container.
     */
    public Object create(Proxy proxy) throws NullPointerException {
        Object mixin = pico.getComponentInstance(mixinComponentKey);
        if (mixin == null) {
            throw new NullPointerException("Mixin with component key " + mixinComponentKey
                    + " + not found in PicoContainer");
        }
        return mixin;
    }

    /**
     * Gets properties. Useful for debugging.
     * 
     * @return an empty <code>Properties</code> object.
     */
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("advice", "mixin");
        return properties;
    }

}