/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.picoextras.script.rhino.PicoScriptable;
import org.picoextras.script.rhino.JavascriptContainerAssembler;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picocontainer.PicoContainer;
import org.mozilla.javascript.JavaScriptException;

import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
/**
 * @deprecated All this Javascript stuff is moved to PicoExtras-Script. We should avoid parallel
 * class hierarchies and use Dependency Injection instead ;-)
 */ 
public class JavaScriptCompositionNanoContainer extends NanoContainer {

//    private final Class nanoRhinoScriptableClass;
//    private final Reader script;
//
//    public JavaScriptCompositionNanoContainer(Reader script, NanoContainerMonitor monitor, Class nanoRhinoScriptableClass)
//            throws PicoAssemblyException{
//        super(monitor);
//        this.nanoRhinoScriptableClass = nanoRhinoScriptableClass;
//        this.script = script;
//        init();
//    }
//
//    public JavaScriptCompositionNanoContainer(Reader script, NanoContainerMonitor monitor)
//            throws PicoAssemblyException{
//        this(script, monitor, PicoScriptable.class);
//    }
//
//    public JavaScriptCompositionNanoContainer(Reader script)
//            throws PicoAssemblyException{
//        this(script, new NullNanoContainerMonitor());
//    }
//
//    protected PicoContainer createPicoContainer() throws PicoAssemblyException {
//        try {
//            return new JavascriptContainerAssembler().execute(nanoRhinoScriptableClass, script);
//        } catch (IOException e) {
//            throw new PicoAssemblyException(e);
//        } catch (JavaScriptException e) {
//            throw new PicoAssemblyException(e);
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        String nanoContainerJs = args[0];
//        if (nanoContainerJs == null) {
//            nanoContainerJs = "composition/components.js";
//        }
//        NanoContainer nano = new JavaScriptCompositionNanoContainer(new FileReader(nanoContainerJs));
//        nano.addShutdownHook();
//    }
}
