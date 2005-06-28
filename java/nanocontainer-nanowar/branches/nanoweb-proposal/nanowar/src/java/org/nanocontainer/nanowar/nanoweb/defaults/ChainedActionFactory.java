package org.nanocontainer.nanoweb.defaults;

import org.nanocontainer.nanoweb.ActionFactory;
import org.nanocontainer.nanoweb.ScriptException;
import org.picocontainer.PicoContainer;

public class ChainedActionFactory implements ActionFactory {

    private final ActionFactory[] actionFactories;

    public ChainedActionFactory(ActionFactory[] actionFactories) {
        this.actionFactories = actionFactories;
    }

    public Object getInstance(PicoContainer pico, String path) throws ScriptException {
        Object action;
        for (int i = 0; i < actionFactories.length; i++) {
            action = actionFactories[i].getInstance(pico, path);
            if (action != null) {
                return action;
            }
        }

        return null;
    }

}
