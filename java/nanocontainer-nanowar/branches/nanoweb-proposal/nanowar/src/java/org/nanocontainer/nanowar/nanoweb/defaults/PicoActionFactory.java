package org.nanocontainer.nanoweb.defaults;

import org.nanocontainer.nanoweb.ActionFactory;
import org.nanocontainer.nanoweb.ScriptException;
import org.picocontainer.PicoContainer;

public class PicoActionFactory implements ActionFactory {

    public Object getInstance(PicoContainer pico, String path) throws ScriptException {
        return pico.getComponentInstance(path);
    }

}
