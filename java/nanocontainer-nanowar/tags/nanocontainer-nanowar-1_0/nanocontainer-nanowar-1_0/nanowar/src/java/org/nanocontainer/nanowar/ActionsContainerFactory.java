package org.nanocontainer.nanowar;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * Factory for the actions container used by MVC action-based frameworks.
 * This factory looks for the PicoContainer in the webapp scopes, via
 * the {@link org.nanocontainer.nanowar.ServletContainerFinder ServletContainerFinder},
 * and uses it to inject the depedencies into the actions.
 * 
 * @author Mauro Talevi
 */
public class ActionsContainerFactory {

    private final ServletContainerFinder containerFinder = new ServletContainerFinder();
    private Map classCache = new HashMap();
    
    public MutablePicoContainer getActionsContainer(HttpServletRequest request) {
        MutablePicoContainer actionsContainer = 
            (MutablePicoContainer) request.getAttribute(KeyConstants.ACTIONS_CONTAINER);
        if (actionsContainer == null) {
            actionsContainer = new DefaultPicoContainer(containerFinder.findContainer(request));
            request.setAttribute(KeyConstants.ACTIONS_CONTAINER, actionsContainer);
        }
        return actionsContainer;
    }

    public Class getActionClass(String className) throws PicoIntrospectionException {
        try {
            return loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new PicoIntrospectionException("Action class '" + className + "' not found", e);
        }
    }
    
    protected Class loadClass(String className) throws ClassNotFoundException {
        if (classCache.containsKey(className)) {
            return (Class) classCache.get(className);
        } else {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class result = classLoader.loadClass(className);
            classCache.put(className, result);
            return result;
        }
    }    

}
