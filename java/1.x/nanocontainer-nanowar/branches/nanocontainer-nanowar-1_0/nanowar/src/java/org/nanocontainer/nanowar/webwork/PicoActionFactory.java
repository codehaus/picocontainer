/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork;

import javax.servlet.http.HttpServletRequest;

import org.nanocontainer.nanowar.ActionsContainerFactory;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.ObjectReference;

import webwork.action.Action;
import webwork.action.ServletActionContext;
import webwork.action.factory.ActionFactory;

/**
 * Replacement for the standard WebWork JavaActionFactory that uses a 
 * PicoContainer to resolve all of the dependencies an Action may have.
 *
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 * @author Mauro Talevi
 */
public class PicoActionFactory extends ActionFactory {

    private ActionsContainerFactory actionsContainerFactory = new ActionsContainerFactory();
    
    public Action getActionImpl(String className) throws Exception {
        try {
            Class actionClass = actionsContainerFactory.getActionClass(className);
            Action action = null;
            try {
                action = instantiateAction(actionClass);
            } catch (Exception e) {
                //swallow these exceptions and return null action
            }
            return action;
        } catch (PicoIntrospectionException e) {
            return null;
        }
    }

    protected Action instantiateAction(Class actionClass) {
        MutablePicoContainer actionsContainer = getActionsContainer();
        Action action = (Action) actionsContainer.getComponentInstance(actionClass);
        
        if (action == null) {
            // The action wasn't registered. Attempt to instantiate it.
            actionsContainer.registerComponentImplementation(actionClass);
            action = (Action) actionsContainer.getComponentInstance(actionClass);
        }
        return action;
    }
    

    /**
     *  Return actions container, first try using the ActionsContainerFactory, 
     *  than in WebWork ActionContext.
     */
    private MutablePicoContainer getActionsContainer() {
        HttpServletRequest request = ServletActionContext.getRequest();
        if ( request != null ) {
            return actionsContainerFactory.getActionsContainer(request);
        } else {
            ObjectReference ref = new ActionContextScopeObjectReference(KeyConstants.REQUEST_CONTAINER);
            return (MutablePicoContainer) ref.get();
        }
    }
}
