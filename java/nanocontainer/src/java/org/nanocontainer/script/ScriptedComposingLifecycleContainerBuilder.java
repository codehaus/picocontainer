/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script;

import org.picoextras.integrationkit.ComposingLifecycleContainerBuilder;
import org.nanocontainer.script.rhino.JavascriptContainerBuilder;
import org.nanocontainer.script.jython.JythonContainerBuilder;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;

import java.io.Reader;
import java.util.Map;
import java.util.HashMap;

public class ScriptedComposingLifecycleContainerBuilder extends ComposingLifecycleContainerBuilder {
    private static final Map EXTENSION_TO_BUILDER_CLASS_MAP = new HashMap();
    static {
        EXTENSION_TO_BUILDER_CLASS_MAP.put("groovy", GroovyContainerBuilder.class);
        EXTENSION_TO_BUILDER_CLASS_MAP.put("js", JavascriptContainerBuilder.class);
        EXTENSION_TO_BUILDER_CLASS_MAP.put("py", JythonContainerBuilder.class);
        EXTENSION_TO_BUILDER_CLASS_MAP.put("xml", XMLContainerBuilder.class);
    }

    protected final Reader script;
    protected final ClassLoader classLoader;

    public ScriptedComposingLifecycleContainerBuilder(Reader script, ClassLoader classLoader) {
        this.script = script;
        this.classLoader = classLoader;
    }

    public static Class getContainerBuilderClass(String extension) {
        return (Class) EXTENSION_TO_BUILDER_CLASS_MAP.get(extension);
    }
}