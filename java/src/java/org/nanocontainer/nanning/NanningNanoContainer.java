/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsen                                               *
 *****************************************************************************/

package org.nanocontainer.nanning;

import org.codehaus.nanning.config.AspectSystem;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision: 1.15 $
 */
public class NanningNanoContainer extends DefaultPicoContainer {
    public NanningNanoContainer(AspectSystem aspectSystem, ComponentAdapterFactory componentAdapterFactory) {
        super(new NanningComponentAdapterFactory(aspectSystem, componentAdapterFactory));
    }

    public static class WithAspectSystem extends NanningNanoContainer {
        public WithAspectSystem(AspectSystem aspectSystem) {
            super(aspectSystem, new DefaultComponentAdapterFactory());
        }
    }

    public static class WithComponentAdapterFactory extends NanningNanoContainer {
        public WithComponentAdapterFactory(ComponentAdapterFactory componentAdapterFactory) {
            super(new AspectSystem(), componentAdapterFactory);
        }
    }

    public static class Default extends NanningNanoContainer {
        public Default() {
            super(new AspectSystem(), new DefaultComponentAdapterFactory());
        }
    }
}
