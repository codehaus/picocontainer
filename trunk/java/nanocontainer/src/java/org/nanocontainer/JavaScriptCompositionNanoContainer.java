/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.picoextras.script.rhino.DefaultNanoRhinoScriptable;
import org.picoextras.script.rhino.NanoRhinoManager;
import org.picoextras.script.PicoCompositionException;
import org.picocontainer.PicoContainer;

import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;

public class JavaScriptCompositionNanoContainer extends NanoContainer {

    private final Class nanoRhinoScriptableClass;
    private final Reader script;

    public JavaScriptCompositionNanoContainer(Reader script, NanoContainerMonitor monitor, Class nanoRhinoScriptableClass)
            throws PicoCompositionException{
        super(monitor);
        this.nanoRhinoScriptableClass = nanoRhinoScriptableClass;
        this.script = script;
        init();
    }

    public JavaScriptCompositionNanoContainer(Reader script, NanoContainerMonitor monitor)
            throws PicoCompositionException{
        this(script, monitor, DefaultNanoRhinoScriptable.class);
    }

    public JavaScriptCompositionNanoContainer(Reader script)
            throws PicoCompositionException{
        this(script, new NullNanoContainerMonitor());
    }

    protected PicoContainer createPicoContainer() throws PicoCompositionException {
        try {
            return new NanoRhinoManager().execute(nanoRhinoScriptableClass, script);
        } catch (IOException e) {
            throw new PicoCompositionException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerJs = args[0];
        if (nanoContainerJs == null) {
            nanoContainerJs = "composition/components.js";
        }
        NanoContainer nano = new JavaScriptCompositionNanoContainer(new FileReader(nanoContainerJs));
        nano.addShutdownHook();
    }
}
