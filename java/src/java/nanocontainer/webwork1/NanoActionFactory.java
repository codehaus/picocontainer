/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package nanocontainer.webwork1;

import webwork.action.Action;
import webwork.action.ServletActionContext;
import webwork.action.factory.ActionFactory;

import java.util.HashMap;
import java.util.Map;

import nanocontainer.servlet.ObjectHolder;
import nanocontainer.servlet.ObjectInstantiater;
import nanocontainer.servlet.lifecycle.BaseLifecycleListener;
import nanocontainer.servlet.holder.RequestScopeObjectHolder;

import javax.servlet.http.HttpServletRequest;

public class NanoActionFactory extends ActionFactory {

    ClassLoader actionLoader;
    Map actionMapping = new HashMap();

    public NanoActionFactory() {
        // Choose classloader
        actionLoader = Thread.currentThread().getContextClassLoader();
    }

    public Action getActionImpl(String name) throws Exception {
        Class actionClass = loadClass(name);
        ObjectInstantiater instantiater = findInstantiater();
        Action result = (Action)instantiater.newInstance(actionClass);
        return result;
    }

    private ObjectInstantiater findInstantiater() {
        HttpServletRequest request = ServletActionContext.getRequest();
        ObjectHolder instantiaterHolder = new RequestScopeObjectHolder(request, BaseLifecycleListener.INSTANTIATER_KEY);
        return (ObjectInstantiater)instantiaterHolder.get();
    }

    private Class loadClass(String name) {
        // Check cache
        Class actionClass = (Class) actionMapping.get(name);

        // Find class using cached actionLoader, or else try Class.forName() as a backup
        if (actionClass == null) {
            try {
                actionClass = actionLoader.loadClass(name);
            } catch (Exception e) {
                try {
                    actionClass = Class.forName(name);
                } catch (Exception e2) {
                    // Not found or could not be instantiated
                    throw new IllegalArgumentException("Action '" + name + "' not found or could not be initialized: " + e2);
                }
            }

            // Put action class in cache
            actionMapping.put(name, actionClass);
        }
        return actionClass;
    }
}
