/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.nanocontainer.reflection.DefaultReflectionContainerAdapter;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.picocontainer.defaults.NullPicoContainer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class NanoContainer {

    public static final String JAVASCRIPT = ".js";
    public static final String JYTHON = ".py";
    public static final String GROOVY = ".groovy";
    public static final String XML = ".xml";
    public static final String BEANSHELL = ".bsh";

    private static final Map extensionToComposers = new HashMap();

    static {
        extensionToComposers.put(JAVASCRIPT, "org.nanocontainer.script.rhino.JavascriptContainerBuilder");
        extensionToComposers.put(XML, "org.nanocontainer.script.xml.XMLContainerBuilder");
        extensionToComposers.put(JYTHON, "org.nanocontainer.script.jython.JythonContainerBuilder");
        extensionToComposers.put(GROOVY, "org.nanocontainer.script.groovy.GroovyContainerBuilder");
        extensionToComposers.put(BEANSHELL, "org.nanocontainer.script.bsh.BeanShellContainerBuilder");
    }

    private ScriptedContainerBuilder containerBuilder;

    public NanoContainer(File compositionFile, PicoContainer parent, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        this(new FileReader(fileExists(compositionFile)), getLanguage(compositionFile), parent, classLoader);
    }

    public NanoContainer(File compositionFile, PicoContainer parent) throws IOException, ClassNotFoundException {
        this(new FileReader(fileExists(compositionFile)), getLanguage(compositionFile), parent, NanoContainer.class.getClassLoader());
    }

    public NanoContainer(File compositionFile) throws IOException, ClassNotFoundException {
        this(new FileReader(fileExists(compositionFile)), getLanguage(compositionFile), new NullPicoContainer(), NanoContainer.class.getClassLoader());
    }

    public NanoContainer(Reader composition, String extension, ClassLoader classLoader) throws ClassNotFoundException {
        this(composition, extension, null, classLoader);
    }

    public NanoContainer(Reader composition, String extension) throws ClassNotFoundException {
        this(composition, extension, null, NanoContainer.class.getClassLoader());
    }

    public NanoContainer(Reader composition, String extension, PicoContainer parent, ClassLoader classLoader) throws ClassNotFoundException {

        String containerAssemblerClassName = (String) extensionToComposers.get(extension);
        DefaultReflectionContainerAdapter defaultReflectionContainerAdapter;
        {
            // disposable.
            DefaultPicoContainer dpc = new DefaultPicoContainer();
            dpc.registerComponentInstance(composition);
            dpc.registerComponentInstance(classLoader);
            defaultReflectionContainerAdapter = new DefaultReflectionContainerAdapter(dpc);
        }
        ComponentAdapter componentAdapter = defaultReflectionContainerAdapter.registerComponentImplementation(containerAssemblerClassName);
        containerBuilder = (ScriptedContainerBuilder) componentAdapter.getComponentInstance();
        final ObjectReference parentRef = new SimpleReference();
        parentRef.set(parent);
    }

    private static File fileExists(File compositionFile) {
        if (compositionFile.exists()) {
            return compositionFile;
        } else {
            //todo a proper exception.
            throw new RuntimeException("File " + compositionFile.getName() + " does not exist.");
        }
    }

    private static String getLanguage(File compositionFile) throws IOException {
        return compositionFile.getCanonicalPath().substring(compositionFile.getCanonicalPath().lastIndexOf("."));
    }

    public ScriptedContainerBuilder getContainerBuilder() {
        return containerBuilder;
    }

}
