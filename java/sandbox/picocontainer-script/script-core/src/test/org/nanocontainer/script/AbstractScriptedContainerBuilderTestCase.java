/*******************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 */
package org.nanocontainer.script;

import org.picocontainer.PicoContainer;

public abstract class AbstractScriptedContainerBuilderTestCase {
    protected PicoContainer buildContainer(ScriptedContainerBuilder builder, PicoContainer parentContainer, Object scope) {
        return builder.buildContainer(parentContainer, scope, true);
    }
}
