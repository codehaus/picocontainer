/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.nanocontainer.rhino.DefaultNanoRhinoScriptable;
import org.nanocontainer.rhino.NanoRhinoManager;
import org.picocontainer.PicoConfigurationException;

import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;

public class JavaScriptAssemblyNanoContainer extends NanoContainer {

    private Class nanoRhinoScriptableClass = DefaultNanoRhinoScriptable.class;

    public JavaScriptAssemblyNanoContainer(Reader script, NanoContainerMonitor monitor, Class nanoRhinoScriptableClass)
            throws PicoConfigurationException, ClassNotFoundException, IOException {
        super(monitor);
        this.nanoRhinoScriptableClass = nanoRhinoScriptableClass;
        configure(script);
    }

    public JavaScriptAssemblyNanoContainer(Reader script, NanoContainerMonitor monitor)
            throws PicoConfigurationException, ClassNotFoundException, IOException {
        super(monitor);
        configure(script);
    }

    public JavaScriptAssemblyNanoContainer(Reader script)
            throws PicoConfigurationException, ClassNotFoundException, IOException {
        super(new NullNanoContainerMonitor());
        configure(script);
    }

    protected void configure(Reader script) throws IOException, ClassNotFoundException, PicoConfigurationException {
        rootContainer = new NanoRhinoManager().execute(nanoRhinoScriptableClass, script);
        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerJs = args[0];
        if (nanoContainerJs == null) {
            nanoContainerJs = "config/nanocontainer.js";
        }
        NanoContainer nano = new JavaScriptAssemblyNanoContainer(new FileReader(nanoContainerJs));
        addShutdownHook(nano);
    }

}
