/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar;

import java.io.Serializable;
import org.picocontainer.PicoContainer;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.ObjectReference;
import org.picocontainer.behaviors.Cached;

import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.DefaultContainerBuilder;

/**
 * Base class for application-level and session-level listeners.
 *
 * @author Michael Rimov
 */
public class AbstractNanoWarListener implements Serializable {

    protected PicoContainer buildContainer(ScriptedContainerBuilder builder) {
        ObjectReference containerRef = new Cached.SimpleReference();
        builder.buildContainer(containerRef, new Cached.SimpleReference(), new Cached.SimpleReference(), false);
        return (PicoContainer) containerRef.get();
    }

    protected void killContainer(ObjectReference containerRef) {
        ContainerBuilder containerKiller = new DefaultContainerBuilder(null);
        if (containerRef.get() != null) {
            containerKiller.killContainer(containerRef);
        }
    }
}
