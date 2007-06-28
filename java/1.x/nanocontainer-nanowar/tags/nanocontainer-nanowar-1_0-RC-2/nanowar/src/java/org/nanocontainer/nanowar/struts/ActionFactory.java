/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.nanocontainer.nanowar.ServletContainerFinder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * Uses Pico to produce Actions and inject dependencies into them. This class
 * does the bulk of the work in nano-struts. If you have your own
 * <code>RequestProcessor</code> implementation, you can use an
 * <code>ActionFactory</code> in your
 * <code>RequestProcessor.processActionCreate</code> method to Picofy your
 * Actions.
 * 
 * @author Stephen Molitor
 */
public class ActionFactory {

    private final ServletContainerFinder containerFinder = new ServletContainerFinder();

    /**
     * Gets the <code>Action</code> specified by the mapping type from a Pico
     * container. The action will be instantiated if necessary, and its
     * dependencies will be injected. The action will be instantiated via a
     * special Pico container that just contains actions. If this container
     * already exists in the request attribute, this method will use it. If no
     * such container exists, this method will create a new Pico container and
     * place it in the request. The parent container will either be the request
     * container, or if that container can not be found the session container,
     * or if that container can not be found, the application container. If no
     * parent container can be found, a <code>PicoInitializationException</code>
     * will be thrown. <p/>The action path specified in the mapping is used as
     * the component key for the action.
     * 
     * @param request the HTTP servlet request.
     * @param mapping the Struts mapping object. The type property tells us what
     *        Action class is required.
     * @param servlet the Struts
     *        <code>ActionServlet.  The action is configured with this servlet.
     * @return the <code>Action</code> instance.
     * @throws PicoIntrospectionException  if the mapping type does not specify a valid action.
     * @throws PicoInitializationException if no request, session, or application scoped Pico container
     *                                     can be found.
     */
    public Action getAction(HttpServletRequest request, ActionMapping mapping, ActionServlet servlet)
            throws PicoIntrospectionException, PicoInitializationException {

        MutablePicoContainer actionsContainer = getActionsContainer(request);
        Object actionKey = mapping.getPath();
        Class actionType = getActionClass(mapping.getType());

        Action action = (Action) actionsContainer.getComponentInstance(actionKey);
        if (action == null) {
            actionsContainer.registerComponentImplementation(actionKey, actionType);
            action = (Action) actionsContainer.getComponentInstance(actionKey);
        }

        action.setServlet(servlet);
        return action;
    }

    private MutablePicoContainer getActionsContainer(HttpServletRequest request) {
        MutablePicoContainer actionsContainer = (MutablePicoContainer) request.getAttribute(KeyConstants.ACTIONS_CONTAINER);
        if (actionsContainer == null) {
            actionsContainer = new DefaultPicoContainer(containerFinder.findContainer(request));
            request.setAttribute(KeyConstants.ACTIONS_CONTAINER, actionsContainer);
        }
        return actionsContainer;
    }

    private Class getActionClass(String className) throws PicoIntrospectionException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new PicoIntrospectionException("Action class + '" + className + "' not found.  "
                    + "Check the spelling of the 'type' element of the action mapping.");
        }
    }

}