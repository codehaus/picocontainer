/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.script;

import java.io.IOException;
import java.io.Reader;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.integrationkit.LifecycleContainerBuilder;
import org.picocontainer.PicoContainer;

/**
 * Baseclass for container builders based on scripting.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class ScriptedContainerBuilder extends LifecycleContainerBuilder {
    protected final Reader script;
    protected final ClassLoader classLoader;

    public ScriptedContainerBuilder(Reader script, ClassLoader classLoader) {
        this.script = script;
        this.classLoader = classLoader;
    }

    protected final PicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope) {
        try {
            return createContainerFromScript(parentContainer, assemblyScope);
        } finally {
            try {
                script.close();
            } catch (IOException e) {
            }
        }
    }

    protected abstract PicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope);

    protected void composeContainer(SoftCompositionPicoContainer container, Object assemblyScope) {
        // do nothing. assume that this is done in createContainer().
    }
}