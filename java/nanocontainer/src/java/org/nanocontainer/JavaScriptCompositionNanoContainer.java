/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.picoextras.rhino.DefaultNanoRhinoScriptable;
import org.picoextras.rhino.NanoRhinoManager;
import org.picocontainer.PicoCompositionException;

import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;

public class JavaScriptCompositionNanoContainer extends NanoContainer {

    private Class nanoRhinoScriptableClass = DefaultNanoRhinoScriptable.class;

    public JavaScriptCompositionNanoContainer(Reader script, NanoContainerMonitor monitor, Class nanoRhinoScriptableClass)
            throws PicoCompositionException, ClassNotFoundException, IOException {
        super(monitor);
        this.nanoRhinoScriptableClass = nanoRhinoScriptableClass;
        compose(script);
    }

    public JavaScriptCompositionNanoContainer(Reader script, NanoContainerMonitor monitor)
            throws PicoCompositionException, ClassNotFoundException, IOException {
        super(monitor);
        compose(script);
    }

    public JavaScriptCompositionNanoContainer(Reader script)
            throws PicoCompositionException, ClassNotFoundException, IOException {
        super(new NullNanoContainerMonitor());
        compose(script);
    }

    protected void compose(Reader script) throws IOException, ClassNotFoundException, PicoCompositionException {
        rootContainer = new NanoRhinoManager().execute(nanoRhinoScriptableClass, script);
        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerJs = args[0];
        if (nanoContainerJs == null) {
            nanoContainerJs = "composition/components.js";
        }
        NanoContainer nano = new JavaScriptCompositionNanoContainer(new FileReader(nanoContainerJs));
        addShutdownHook(nano);
    }

}
