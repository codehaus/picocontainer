package org.picoextras.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.ScriptableObject;
import org.picoextras.script.PicoCompositionException;
import org.picocontainer.PicoContainer;

import java.io.Reader;
import java.io.IOException;

public class NanoRhinoManager {
    public PicoContainer execute(Class scriptableClass, Reader script) throws PicoCompositionException, IOException {
        Context cx = Context.enter();
        Scriptable scriptable;
        try {
            scriptable = cx.initStandardObjects(null);

            defineClass(scriptable, scriptableClass);

            NanoRhinoScriptableHolder nanoHolder = new NanoRhinoScriptableHolder();
            Scriptable jsArgs = Context.toObject(nanoHolder, scriptable);
            scriptable.put("nano", scriptable, jsArgs);

            cx.evaluateReader(scriptable, script, "<cmd>", 1, null);

            return nanoHolder.getNanoRhinoScriptable().getPicoContainer();

        } catch (final JavaScriptException e) {
            throw new PicoCompositionException(e);
        } finally {
            Context.exit();
        }

    }

    private void defineClass(Scriptable scriptable, Class rhinoClass) throws PicoCompositionException {
        try {
            ScriptableObject.defineClass(scriptable, rhinoClass);
        } catch (final Exception e) {
            throw new PicoCompositionException(e);
        }
    }
}
