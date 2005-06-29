package org.nanocontainer.nanowar.nanoweb.defaults;

import org.nanocontainer.nanowar.nanoweb.ActionFactory;
import org.nanocontainer.nanowar.nanoweb.ScriptException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public abstract class AbstractActionFactory implements ActionFactory {

    public final Object getInstance(PicoContainer pico, String path) throws ScriptException {
        Class actionClass = getClass(path);

        if (actionClass == null) {
            return null;
        }

        MutablePicoContainer actionContainer = new DefaultPicoContainer(pico);
        actionContainer.registerComponentImplementation(actionClass, actionClass);

        return actionContainer.getComponentInstance(actionClass);
    }

    protected abstract Class getClass(String path) throws ScriptException;

}
