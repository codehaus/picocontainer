package org.picoextras.rhino;

public class NanoRhinoScriptableHolder {

    private NanoRhinoScriptable nanoRhinoScriptable;

    public NanoRhinoScriptable getNanoRhinoScriptable() {
        return nanoRhinoScriptable;
    }

    public void setNanoRhinoScriptable(NanoRhinoScriptable nanoRhinoScriptable) {
        if (this.nanoRhinoScriptable == null) {
            this.nanoRhinoScriptable = nanoRhinoScriptable;
        }
    }

}
