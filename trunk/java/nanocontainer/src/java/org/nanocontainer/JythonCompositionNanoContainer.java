/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.picoextras.reflection.DefaultReflectionFrontEnd;
import org.picoextras.reflection.ReflectionFrontEnd;
import org.picoextras.script.PicoCompositionException;
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
 */
public class JythonCompositionNanoContainer extends NanoContainer {

    private ReflectionFrontEnd reflectionRootContainer;
    private PythonInterpreter interpreter;
    private Reader script;

    public JythonCompositionNanoContainer(Reader script, NanoContainerMonitor monitor)
            throws PicoCompositionException{
        super(monitor);
        this.script = script;
        interpreter = new PythonInterpreter();
        interpreter.exec("from org.picoextras.reflection import DefaultReflectionFrontEnd");
        reflectionRootContainer = new DefaultReflectionFrontEnd();
        init();
    }

    public JythonCompositionNanoContainer(Reader script)
            throws PicoCompositionException {
        this(script, new NullNanoContainerMonitor());
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerPy = args[0];
        if (nanoContainerPy == null) {
            nanoContainerPy = "composition/components.py";
        }
        NanoContainer nano = new JythonCompositionNanoContainer(new FileReader(nanoContainerPy));
        nano.addShutdownHook();
    }

    protected PicoContainer createPicoContainer() throws PicoCompositionException {
        interpreter.set("rootContainer", reflectionRootContainer);
        interpreter.execfile(new InputStream() {
            public int read() throws IOException {
                return script.read();
            }
        });
        return reflectionRootContainer.getPicoContainer();
    }
}
