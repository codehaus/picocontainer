/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsen                                               *
 *****************************************************************************/

package org.picoextras.nanning;

import org.codehaus.nanning.config.AspectSystem;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision$
 * @deprecated Use a {@link DefaultPicoContainer} with a {@link NanningComponentAdapterFactory}
 * instead. This favours chaining of component adapters.
 */
public class NanningNanoContainer extends DefaultPicoContainer {
    public NanningNanoContainer(AspectSystem aspectSystem, ComponentAdapterFactory componentAdapterFactory) {
        super(new NanningComponentAdapterFactory(aspectSystem, componentAdapterFactory));
    }

    public NanningNanoContainer(AspectSystem aspectSystem) {
        this(aspectSystem, new DefaultComponentAdapterFactory());
    }

    public NanningNanoContainer(ComponentAdapterFactory componentAdapterFactory) {
        this(new AspectSystem(), componentAdapterFactory);
    }

    public NanningNanoContainer() {
        this(new AspectSystem(), new DefaultComponentAdapterFactory());
    }
}
