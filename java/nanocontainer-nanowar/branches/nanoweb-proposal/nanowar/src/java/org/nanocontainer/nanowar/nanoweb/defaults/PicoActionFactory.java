package org.nanocontainer.nanowar.nanoweb.defaults;

import org.nanocontainer.nanowar.nanoweb.ActionFactory;
import org.nanocontainer.nanowar.nanoweb.ScriptException;
import org.picocontainer.PicoContainer;

public class PicoActionFactory implements ActionFactory {

	public Object getInstance(PicoContainer pico, String path) throws ScriptException {
		return pico.getComponentInstance(path);
	}

}
