/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.dynaop;

import dynaop.MixinFactory;
import dynaop.Proxy;
import org.picocontainer.PicoContainer;

import java.util.Properties;

/**
 * Manufactures mixins from a <code>PicoContainer</code>. Useful when a mixin
 * has dependencies on other components in the <code>PicoContainer</code>.
 * 
 * @author Stephen Molitor
 */
public class PicoMixinFactory implements MixinFactory {
    private PicoContainer container;
    private Object mixinComponentKey;

    /**
     * Creates a new <code>PicoMixinFactory</code> that will manufacture
     * mixins by retrieving them from a <code>PicoContainer</code> using a
     * given component key.
     * 
     * @param container
     *            the <code>PicoContainer</code> to retrieve the mixin from.
     * @param mixinComponentKey
     *            the component key that will be used to retrieve the mixin
     *            object from the container.
     */
    public PicoMixinFactory(PicoContainer container, Object mixinComponentKey) {
        this.container = container;
        this.mixinComponentKey = mixinComponentKey;
    }

    /**
     * Manufactures an <code>Mixin</code> by retrieving it from the
     * <code>PicoContainer</code>.
     * 
     * @param proxy
     *            the proxy that the interceptor will wrap.
     * @return the <code>Mixin</code> object.
     * @throws NullPointerException
     *             if the mixin can not be found in the container.
     */
    public Object create(Proxy proxy) throws NullPointerException {
        Object mixin = container.getComponentInstance(mixinComponentKey);
        if (mixin == null) {
            throw new NullPointerException("Mixin with component key "
                            + mixinComponentKey
                            + " + not found in PicoContainer");
        }
        return mixin;
    }

    /**
     * Gets properties. Useful for debuging.
     * 
     * @return an empty <code>Properties</code> object.
     */
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("advice", "mixin");
        properties.setProperty("class", create(null).getClass().getName());
        return properties;
    }

}