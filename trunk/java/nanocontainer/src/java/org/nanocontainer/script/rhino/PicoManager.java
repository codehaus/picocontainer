package org.picoextras.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.ScriptableObject;
import org.picoextras.script.PicoCompositionException;
import org.picocontainer.PicoContainer;

import java.io.Reader;
import java.io.IOException;

public class PicoManager {
    public PicoContainer execute(Class scriptableClass, Reader javascript) throws PicoCompositionException, IOException, JavaScriptException {
        Context cx = Context.enter();
        try {
            Scriptable scriptable = cx.initStandardObjects(null);
            defineClass(scriptable, scriptableClass);
            PicoScriptableHolder holder = new PicoScriptableHolder();

            Scriptable jsArgs = Context.toObject(holder, scriptable);
            scriptable.put("pico", scriptable, jsArgs);

            cx.evaluateReader(scriptable, javascript, "<cmd>", 1, null);
            return holder.getPicoScriptable().getPicoContainer();
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
