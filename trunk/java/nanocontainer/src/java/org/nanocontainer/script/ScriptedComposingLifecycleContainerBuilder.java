/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script;

import org.nanocontainer.integrationkit.ComposingLifecycleContainerBuilder;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class ScriptedComposingLifecycleContainerBuilder extends ComposingLifecycleContainerBuilder {
    private static final Map EXTENSION_TO_BUILDER_CLASS_NAME_MAP = new HashMap();

    static {
        ScriptedComposingLifecycleContainerBuilder.EXTENSION_TO_BUILDER_CLASS_NAME_MAP.put("groovy", "org.nanocontainer.script.groovy.GroovyContainerBuilder");
        ScriptedComposingLifecycleContainerBuilder.EXTENSION_TO_BUILDER_CLASS_NAME_MAP.put("js", "org.nanocontainer.script.rhino.JavascriptContainerBuilder");
        ScriptedComposingLifecycleContainerBuilder.EXTENSION_TO_BUILDER_CLASS_NAME_MAP.put("py", "org.nanocontainer.script.jython.JythonContainerBuilder");
        ScriptedComposingLifecycleContainerBuilder.EXTENSION_TO_BUILDER_CLASS_NAME_MAP.put("xml", "org.nanocontainer.script.xml.XMLContainerBuilder");
    }

    protected final Reader script;
    protected final ClassLoader classLoader;

    public ScriptedComposingLifecycleContainerBuilder(Reader script, ClassLoader classLoader) {
        this.script = script;
        this.classLoader = classLoader;
    }

    private static Class getContainerBuilderClass(String extension) throws ClassNotFoundException {
        String containerBuilderClassName = (String) EXTENSION_TO_BUILDER_CLASS_NAME_MAP.get(extension);
        if(containerBuilderClassName == null) {
            throw new IllegalArgumentException("Unknown extension: '" + extension + "' (There should be no '.')");
        }
        return ScriptedComposingLifecycleContainerBuilder.class.getClassLoader().loadClass(containerBuilderClassName);
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