/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop;

/**
 * Wraps a Pico container component key. Used by methods in
 * <code>AspectsContainer</code> to disambiguate between mixin component keys
 * of type <code>Class</code> and mixin classes.
 * 
 * @author Stephen Molitor
 * @version $Revision$
 */
public class ComponentKey {

    private final Object componentKey;

    /**
     * Creates a new <code>ComponentKey</code> object.
     * 
     * @param componentKey the component key.
     */
    public ComponentKey(Object componentKey) {
        this.componentKey = componentKey;
    }

    /**
     * Gets the component key.
     * 
     * @return the component key.
     */
    public Object getComponentKey() {
        return componentKey;
    }

}