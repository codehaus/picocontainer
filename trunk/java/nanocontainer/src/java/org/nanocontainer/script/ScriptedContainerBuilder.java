/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.script;

import org.nanocontainer.integrationkit.LifecycleContainerBuilder;
import org.picocontainer.MutablePicoContainer;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

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

    protected void composeContainer(MutablePicoContainer container, Object assemblyScope) {
        // do nothing. assume that this is done in createContainer().
    }
}