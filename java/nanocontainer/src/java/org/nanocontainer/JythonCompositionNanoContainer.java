/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.picoextras.reflection.DefaultReflectionContainerAdapter;
import org.picoextras.reflection.ReflectionContainerAdapter;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picocontainer.PicoContainer;
import org.python.util.PythonInterpreter;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author Paul Hammant
 * @author Mike Royle
 * @author Aslak Helles&oslash;y
 *
 * @deprecated All this Jython stuff is moved to PicoExtras-Script. We should avoid parallel
 * class hierarchies and use Dependency Injection instead ;-)
 */
public class JythonCompositionNanoContainer extends NanoContainer {
//
//    private ReflectionContainerAdapter reflectionRootContainer;
//    private PythonInterpreter interpreter;
//    private Reader script;
//
//    public JythonCompositionNanoContainer(Reader script, NanoContainerMonitor monitor)
//            throws PicoAssemblyException{
//        super(monitor);
//        this.script = script;
//        interpreter = new PythonInterpreter();
//        interpreter.exec("from org.picoextras.reflection import DefaultReflectionContainerAdapter");
//        reflectionRootContainer = new DefaultReflectionContainerAdapter();
//        init();
//    }
//
//    public JythonCompositionNanoContainer(Reader script)
//            throws PicoAssemblyException {
//        this(script, new NullNanoContainerMonitor());
//    }
//
//    public static void main(String[] args) throws Exception {
//        String nanoContainerPy = args[0];
//        if (nanoContainerPy == null) {
//            nanoContainerPy = "composition/components.py";
//        }
//        NanoContainer nano = new JythonCompositionNanoContainer(new FileReader(nanoContainerPy));
//        nano.addShutdownHook();
//    }
//
//    protected PicoContainer createPicoContainer() throws PicoAssemblyException {
//        interpreter.set("rootContainer", reflectionRootContainer);
//        interpreter.execfile(new InputStream() {
//            public int read() throws IOException {
//                return script.read();
//            }
//        });
//        return reflectionRootContainer.getPicoContainer();
//    }
}
