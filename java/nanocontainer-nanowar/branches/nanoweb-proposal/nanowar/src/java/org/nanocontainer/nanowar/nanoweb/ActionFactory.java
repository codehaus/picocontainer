package org.nanocontainer.nanowar.nanoweb;

import org.picocontainer.PicoContainer;

public interface ActionFactory {

    public Object getInstance(PicoContainer pico, String path) throws ScriptException;

}
