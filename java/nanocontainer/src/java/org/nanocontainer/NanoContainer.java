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
import org.picocontainer.defaults.NullPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

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

    protected static final Map extensionToComposers = new HashMap();

    static {
        extensionToComposers.put(JAVASCRIPT, "org.nanocontainer.script.rhino.JavascriptContainerBuilder");
        extensionToComposers.put(XML, "org.nanocontainer.script.xml.XMLContainerBuilder");
        extensionToComposers.put(JYTHON, "org.nanocontainer.script.jython.JythonContainerBuilder");
        extensionToComposers.put(GROOVY, "org.nanocontainer.script.groovy.GroovyContainerBuilder");
        extensionToComposers.put(BEANSHELL, "org.nanocontainer.script.bsh.BeanShellContainerBuilder");
    }

    protected ScriptedContainerBuilder containerBuilder;

    protected ObjectReference containerRef;

    public NanoContainer(File compositionFile, PicoContainer parent, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        this(new FileReader(compositionFile), compositionFile.getCanonicalPath().substring(compositionFile.getCanonicalPath().indexOf(".")), parent, classLoader);
    }

    public NanoContainer(File compositionFile, PicoContainer parent) throws IOException, ClassNotFoundException {
        this(new FileReader(compositionFile), compositionFile.getCanonicalPath().substring(compositionFile.getCanonicalPath().indexOf(".")), parent, NanoContainer.class.getClassLoader());
    }

    public NanoContainer(File compositionFile) throws IOException, ClassNotFoundException {
        this(new FileReader(compositionFile), compositionFile.getCanonicalPath().substring(compositionFile.getCanonicalPath().indexOf(".")), new NullPicoContainer(), NanoContainer.class.getClassLoader());
    }

    public NanoContainer(Reader composition, String language, ClassLoader classLoader) throws ClassNotFoundException {
        this(composition, language, new NullPicoContainer(), classLoader);
    }

    public NanoContainer(Reader composition, String language) throws ClassNotFoundException {
        this(composition, language, new NullPicoContainer(), NanoContainer.class.getClassLoader());
    }

    public NanoContainer(Reader composition, String language, PicoContainer parent, ClassLoader classLoader) throws ClassNotFoundException {

        String containerAssemblerClassName = (String) extensionToComposers.get(language);
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
        containerRef = new SimpleReference();
        final ObjectReference parentRef = new SimpleReference();
        parentRef.set(parent);

        // build and start the container
        containerBuilder.buildContainer(containerRef, parentRef, null);
    }

    public void killContainer() {
        containerBuilder.killContainer(containerRef);
    }

    public ScriptedContainerBuilder getContainerBuilder() {
        return containerBuilder;
    }

}
