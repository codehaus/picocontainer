/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.script;

import org.nanocontainer.integrationkit.ComposingLifecycleContainerBuilder;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ScriptedContainerBuilder extends ComposingLifecycleContainerBuilder {
    private static final Map EXTENSION_TO_BUILDER_CLASS_NAME_MAP = new HashMap();

    static {
        ScriptedContainerBuilder.EXTENSION_TO_BUILDER_CLASS_NAME_MAP.put("groovy", "org.nanocontainer.script.groovy.GroovyContainerBuilder");
        ScriptedContainerBuilder.EXTENSION_TO_BUILDER_CLASS_NAME_MAP.put("js", "org.nanocontainer.script.rhino.JavascriptContainerBuilder");
        ScriptedContainerBuilder.EXTENSION_TO_BUILDER_CLASS_NAME_MAP.put("py", "org.nanocontainer.script.jython.JythonContainerBuilder");
        ScriptedContainerBuilder.EXTENSION_TO_BUILDER_CLASS_NAME_MAP.put("xml", "org.nanocontainer.script.xml.XMLContainerBuilder");
    }

    protected final Reader script;
    protected final ClassLoader classLoader;

    public ScriptedContainerBuilder(Reader script, ClassLoader classLoader) {
        this.script = script;
        this.classLoader = classLoader;
    }

    private static Class getContainerBuilderClass(String extension) throws ClassNotFoundException {
        String containerBuilderClassName = (String) EXTENSION_TO_BUILDER_CLASS_NAME_MAP.get(extension);
        if(containerBuilderClassName == null) {
            throw new IllegalArgumentException("Unknown extension: '" + extension + "' (There should be no '.')");
        }
        return ScriptedContainerBuilder.class.getClassLoader().loadClass(containerBuilderClassName);
    }

    public static ContainerBuilder createBuilder(String extension, Reader scriptReader, ClassLoader applicationClassLoader) throws ClassNotFoundException {
        Class builderClass = getContainerBuilderClass(extension);
        MutablePicoContainer builderFactory = new DefaultPicoContainer();
        builderFactory.registerComponentImplementation("builder", builderClass);
        builderFactory.registerComponentInstance(scriptReader);
        builderFactory.registerComponentInstance(applicationClassLoader);
        return (ContainerBuilder) builderFactory.getComponentInstance("builder");
    }
}