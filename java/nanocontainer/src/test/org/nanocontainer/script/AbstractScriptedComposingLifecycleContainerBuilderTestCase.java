/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

public abstract class AbstractScriptedComposingLifecycleContainerBuilderTestCase extends TestCase {
    private ObjectReference containerRef = new SimpleReference();
    private ObjectReference parentContainerRef = new SimpleReference();

    protected PicoContainer buildContainer(ScriptedComposingLifecycleContainerBuilder builder, PicoContainer parentContainer) {
        parentContainerRef.set(parentContainer);
        builder.buildContainer(containerRef, parentContainerRef, "SOME_SCOPE");
        return (PicoContainer) containerRef.get();
    }
}