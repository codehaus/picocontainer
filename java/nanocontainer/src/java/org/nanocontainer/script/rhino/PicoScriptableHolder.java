package org.picoextras.script.rhino;

public class PicoScriptableHolder {

    private PicoScriptable picoScriptable;

    public PicoScriptable getPicoScriptable() {
        return picoScriptable;
    }

    public void setPicoScriptable(PicoScriptable picoScriptable) {
        if (this.picoScriptable == null) {
            this.picoScriptable = picoScriptable;
        }
    }

}
