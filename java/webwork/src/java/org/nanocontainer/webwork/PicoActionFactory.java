/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.webwork;

import org.nanocontainer.servlet.KeyConstants;
import org.nanocontainer.servlet.RequestScopeObjectReference;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import webwork.action.Action;
import webwork.action.ServletActionContext;
import webwork.action.factory.ActionFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Replacement for the standard WebWork JavaActionFactory that resolves all of the
 * dependencies an Action may have in the constructor.
 *
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class PicoActionFactory extends ActionFactory implements KeyConstants {

    private Map classCache = new HashMap();

    public Action getActionImpl(String className) throws Exception {
        Class actionClass = loadClass(className);
        if (actionClass == null) {
            return null;
        } else {
            Action action = null;
            try {
                action = instantiateAction(actionClass);
            } catch (Exception e) {
                // todo: what? webwork seems to swallow these exceptions - I think.
                e.printStackTrace();
            }
            return action;
        }
    }

    protected Class loadClass(String className) {
        if (classCache.containsKey(className)) {
            return (Class) classCache.get(className);
        } else {
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Class result = classLoader.loadClass(className);
                classCache.put(className, result);
                return result;
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    protected Action instantiateAction(Class actionClass) {
        MutablePicoContainer container = new DefaultPicoContainer(getRequestContainer());
        container.registerComponentImplementation(actionClass);
        return (Action) container.getComponentInstance(actionClass);
    }

    private MutablePicoContainer getRequestContainer() {
        HttpServletRequest request = ServletActionContext.getRequest();
        ObjectReference ref = new RequestScopeObjectReference(request, REQUEST_CONTAINER);
        return (MutablePicoContainer) ref.get();
    }
}
