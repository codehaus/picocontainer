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
import org.picocontainer.PicoCompositionException;
import org.python.util.PythonInterpreter;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author Paul Hammant
 * @author Mike Royle
 */
public class JythonCompositionNanoContainer extends NanoContainer {

    private PythonInterpreter interpreter;
    private ReflectionFrontEnd reflectionRootContainer;


    public JythonCompositionNanoContainer(Reader script, NanoContainerMonitor monitor)
            throws PicoCompositionException, ClassNotFoundException, IOException {
        super(monitor);
        interpreter = new PythonInterpreter();
        interpreter.exec("from org.picoextras.reflection import DefaultReflectionFrontEnd");
        reflectionRootContainer = new DefaultReflectionFrontEnd();
        compose(script);
    }

    public JythonCompositionNanoContainer(Reader script)
            throws PicoCompositionException, ClassNotFoundException, IOException {
        this(script, new NullNanoContainerMonitor());
    }

    protected void compose(final Reader script) throws IOException, ClassNotFoundException, PicoCompositionException {
        interpreter.set("rootContainer", reflectionRootContainer);
        interpreter.execfile(new InputStream() {
            public int read() throws IOException {
                return script.read();
            }
        });
        rootContainer = reflectionRootContainer.getPicoContainer();
        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }

    public static void main(String[] args) throws Exception {
        String nanoContainerPy = args[0];
        if (nanoContainerPy == null) {
            nanoContainerPy = "composition/components.py";
        }
        NanoContainer nano = new JythonCompositionNanoContainer(new FileReader(nanoContainerPy));
        addShutdownHook(nano);
    }
}
