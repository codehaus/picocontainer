package org.nanocontainer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.nanocontainer.rhino.DefaultNanoRhinoScriptable;


public class BogusNanoRhinoScriptable extends ScriptableObject {
    public String getClassName() {
        return "NanoRhinoScriptable";
    }

    // all pub static javascript methods missing.
}
