/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.gems.injectors;

import org.picocontainer.injectors.FactoryInjector;
import org.picocontainer.injectors.InjectInto;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Type;

/**
 * This will Inject a Commons-Logging Logger for the injectee's class name
 */
public class CommonsLoggingInjector extends FactoryInjector<Log> {

    @Override
	public Log getComponentInstance(final PicoContainer container, final Type into) throws PicoCompositionException {
        return LogFactory.getLog((((InjectInto) into).getIntoClass()).getName());
    }
}