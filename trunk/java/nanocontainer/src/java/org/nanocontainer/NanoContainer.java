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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * The main class for configuration of PicoContainer with various scripting languages.
 * When using the constructors taking a file, the extensions must be one of the following:
 * <ul>
 * <li>.groovy</li>
 * <li>.bsh</li>
 * <li>.js</li>
 * <li>.py</li>
 * <li>.xml</li>
 * </ul>
 * -And the content of the file likewise. See <a href="http://docs.codehaus.org/display/NANO/NanoContainer">NanoContainer documentation</a>
 * for details.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslah;y
 */
public class NanoContainer {

    public static final String GROOVY = ".groovy";
    public static final String BEANSHELL = ".bsh";
    public static final String JAVASCRIPT = ".js";
    public static final String JYTHON = ".py";
    public static final String XML = ".xml";

    private static final Map extensionToBuilders = new HashMap();

    static {
        extensionToBuilders.put(GROOVY, "org.nanocontainer.script.groovy.GroovyContainerBuilder");
        extensionToBuilders.put(BEANSHELL, "org.nanocontainer.script.bsh.BeanShellContainerBuilder");
        extensionToBuilders.put(JAVASCRIPT, "org.nanocontainer.script.rhino.JavascriptContainerBuilder");
        extensionToBuilders.put(XML, "org.nanocontainer.script.xml.XMLContainerBuilder");
        extensionToBuilders.put(JYTHON, "org.nanocontainer.script.jython.JythonContainerBuilder");
    }

    private ScriptedContainerBuilder containerBuilder;

    public NanoContainer(File compositionFile, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        this(new FileReader(fileExists(compositionFile)), getBuilderClassName(compositionFile), classLoader);
    }

    public NanoContainer(File compositionFile) throws IOException, ClassNotFoundException {
        this(new FileReader(fileExists(compositionFile)), getBuilderClassName(compositionFile), NanoContainer.class.getClassLoader());
    }

    public NanoContainer(Reader composition, String builderClass) throws ClassNotFoundException {
        this(composition, builderClass, NanoContainer.class.getClassLoader());
    }

    public NanoContainer(Reader composition, String builderClass, ClassLoader classLoader) throws ClassNotFoundException {
        this(composition, builderClass, classLoader, null);
    }

    public NanoContainer(Reader composition, String builderClass, ClassLoader classLoader, PicoContainer parent) throws ClassNotFoundException {

        DefaultReflectionContainerAdapter defaultReflectionContainerAdapter;
        {
            // disposable.
            DefaultPicoContainer dpc = new DefaultPicoContainer(); // TODO parent?
            dpc.registerComponentInstance(composition);
            dpc.registerComponentInstance(classLoader);
            defaultReflectionContainerAdapter = new DefaultReflectionContainerAdapter(dpc);
        }
        ComponentAdapter componentAdapter = defaultReflectionContainerAdapter.registerComponentImplementation(builderClass);
        containerBuilder = (ScriptedContainerBuilder) componentAdapter.getComponentInstance(defaultReflectionContainerAdapter.getPicoContainer());

    }
    private static File fileExists(File file) {
        if (file.exists()) {
            return file;
        } else {
            //todo a proper exception.
            throw new RuntimeException("File " + file.getName() + " does not exist.");
        }
    }

    private static String getBuilderClassName(File compositionFile) throws IOException {
        String language = getExtension(compositionFile);
        return getBuilderClassName(language);
    }

    public static String getBuilderClassName(String extension) {
        return (String) extensionToBuilders.get(extension);
    }

    private static String getExtension(File file) throws IOException {
        return file.getCanonicalPath().substring(file.getCanonicalPath().lastIndexOf("."));
    }

    public ScriptedContainerBuilder getContainerBuilder() {
        return containerBuilder;
    }

}
