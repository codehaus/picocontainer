/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.componentadapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.componentadapters.DecoratingComponentAdapterFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SynchronizedComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    public SynchronizedComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    public ComponentAdapter createComponentAdapter(ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters) {
        return new SynchronizedComponentAdapter(super.createComponentAdapter(registerationCharacteristic, componentKey, componentImplementation, parameters));
    }
}
