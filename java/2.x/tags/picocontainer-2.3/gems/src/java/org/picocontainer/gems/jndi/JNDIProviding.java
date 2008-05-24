/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.jndi;

import java.util.Properties;

import org.picocontainer.*;

/**
 * TODO: decide where to get JNDI name as we do not have 
 * implementation here. ? Property
 * @author k.pribluda
 *
 */
public class JNDIProviding implements ComponentFactory {

	public <T> ComponentAdapter<T> createComponentAdapter(
			ComponentMonitor componentMonitor,
			LifecycleStrategy lifecycleStrategy,
			Properties componentProperties, Object componentKey,
			Class<T> componentImplementation, Parameter... parameters)
			throws PicoCompositionException {
		return null;
	}

    public void verify(PicoContainer container) {
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentFactory(this);
    }
}
