/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import org.picocontainer.MutablePicoContainer;

/**
 * Allows to compose containers for different webapp scopes. The composer is
 * used by the {@link org.picocontainer.web.PicoServletContainerListener}
 * after the webapp context is initialised. Users can either register the
 * components for each scope directly or load them from a script.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public interface WebappComposer {

    void application(MutablePicoContainer applicationContainer);

    void session(MutablePicoContainer sessionContainer);

    void request(MutablePicoContainer requestContainer);

}
