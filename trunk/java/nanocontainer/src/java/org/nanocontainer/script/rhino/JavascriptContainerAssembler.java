package org.picoextras.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.picocontainer.MutablePicoContainer;
import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.integrationkit.PicoAssemblyException;

import java.io.Reader;

/**
 * This ContainerAssembler uses Javascript to assemble the container.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */
public class JavascriptContainerAssembler implements ContainerAssembler {
    private Reader javascript;

    public JavascriptContainerAssembler(Reader javascript) {
        this.javascript = javascript;
    }

    public void assembleContainer(MutablePicoContainer container, Object assemblyScope) {
        // A tad ugly. Must be a better way.
        PicoScriptable.picoContainer = container;
        Context cx = Context.enter();
        try {
            Scriptable scriptable = cx.initStandardObjects(null);
            ScriptableObject.defineClass(scriptable, PicoScriptable.class);
            cx.evaluateReader(scriptable, javascript, "<cmd>", 1, null);
        } catch (Exception e) {
            throw new PicoAssemblyException(e);
        } finally {
            Context.exit();
            PicoScriptable.picoContainer = null;
        }
    }
}
