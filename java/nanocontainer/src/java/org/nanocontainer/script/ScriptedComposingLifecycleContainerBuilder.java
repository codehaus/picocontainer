/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script;

import org.nanocontainer.integrationkit.ComposingLifecycleContainerBuilder;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.jython.JythonContainerBuilder;
import org.nanocontainer.script.rhino.JavascriptContainerBuilder;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class ScriptedComposingLifecycleContainerBuilder extends ComposingLifecycleContainerBuilder {
    private static final Map EXTENSION_TO_BUILDER_CLASS_MAP = new HashMap();

    static {
        ScriptedComposingLifecycleContainerBuilder.EXTENSION_TO_BUILDER_CLASS_MAP.put("groovy", GroovyContainerBuilder.class);
        ScriptedComposingLifecycleContainerBuilder.EXTENSION_TO_BUILDER_CLASS_MAP.put("js", JavascriptContainerBuilder.class);
        ScriptedComposingLifecycleContainerBuilder.EXTENSION_TO_BUILDER_CLASS_MAP.put("py", JythonContainerBuilder.class);
        ScriptedComposingLifecycleContainerBuilder.EXTENSION_TO_BUILDER_CLASS_MAP.put("xml", XMLContainerBuilder.class);
    }

    protected final Reader script;
    protected final ClassLoader classLoader;

    public ScriptedComposingLifecycleContainerBuilder(Reader script, ClassLoader classLoader) {
        this.script = script;
        this.classLoader = classLoader;
    }

    public static Class getContainerBuilderClass(String extension) {
        Class containerBuilderClass = (Class) EXTENSION_TO_BUILDER_CLASS_MAP.get(extension);
        if(containerBuilderClass == null) {
            throw new IllegalArgumentException("Unknown extension: '" + extension + "' (There should be no '.')");
        }
        return containerBuilderClass;
    }

    public static ContainerBuilder createBuilder(String extension, Reader scriptReader, ClassLoader applicationClassLoader) {
        Class builderClass = getContainerBuilderClass(extension);
        MutablePicoContainer builderFactory = new DefaultPicoContainer();
        builderFactory.registerComponentImplementation("builder", builderClass);
        builderFactory.registerComponentInstance(scriptReader);
        builderFactory.registerComponentInstance(applicationClassLoader);
        return (ContainerBuilder) builderFactory.getComponentInstance("builder");
    }
}