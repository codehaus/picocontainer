package org.nanocontainer;

import org.mozilla.javascript.*;
import org.picocontainer.PicoConfigurationException;
import org.nanocontainer.rhino.RhinoFrontEnd;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;


public class JavaScriptAssemblyNanoContainer extends NanoContainer {
    public JavaScriptAssemblyNanoContainer(Reader script, NanoContainerMonitor monitor) throws PicoConfigurationException, ClassNotFoundException, IOException {
        super(monitor);
        configure(script);
    }

    protected void configure(Reader script) throws IOException, ClassNotFoundException, PicoConfigurationException {

        Context cx = Context.enter();
        try {
            Scriptable scriptable = cx.initStandardObjects(null);
            try {
                ScriptableObject.defineClass(scriptable, RhinoFrontEnd.class);
            } catch (final Exception e) {
                throw new PicoConfigurationException() {
                    public String getMessage() {
                        return "JavaScriptException : " + e.getMessage();
                    }
                };
            }
            cx.evaluateReader(scriptable, script, "<cmd>", 1, null);

        } catch (final JavaScriptException e) {
            throw new PicoConfigurationException() {
                public String getMessage() {
                    return "JavaScriptException : " + e.getMessage();
                }
            };
        } finally {
            Context.exit();
        }
        //TODO rootContainer = ??
        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }
}
