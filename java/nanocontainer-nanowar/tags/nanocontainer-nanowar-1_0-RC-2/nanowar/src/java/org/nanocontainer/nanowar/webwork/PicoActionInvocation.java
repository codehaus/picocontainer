/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork;

import com.opensymphony.webwork.WebWorkStatics;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;
import org.nanocontainer.nanowar.KeyConstants;
import org.nanocontainer.nanowar.RequestScopeObjectReference;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Implementation of {@link com.opensymphony.xwork.ActionInvocation ActionInvocation}
 * which uses a PicoContainer to create Action instances.
 * 
 * @author Chris Sturm
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoActionInvocation extends DefaultActionInvocation implements KeyConstants {

    public PicoActionInvocation(ActionProxy proxy) throws Exception {
        super(proxy);
    }

    public PicoActionInvocation(ActionProxy proxy, Map extraContext) throws Exception {
        super(proxy, extraContext);
    }

    public PicoActionInvocation(ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(proxy, extraContext, pushAction);
    }

    protected void createAction() {
        Class actionClass = proxy.getConfig().getClass();
        
        PicoContainer requestContainer = getRequestContainer();
        Object actionInstance = requestContainer.getComponentInstance(actionClass);
        if (actionInstance == null) {
            // The action wasn't registered. Attempt to instantiate it.
            actionInstance = createComponentInstance(requestContainer, actionClass);
        }
        try {
	        action = (Action) actionInstance;
		} catch(ClassCastException e) {
			throw new PicoIntrospectionException("The action of class " + actionInstance.getClass().getName() + " is not assignable to type " + Action.class.getName() + ".", e);
		}
    }

    private PicoContainer getRequestContainer() {
        HttpServletRequest request = (HttpServletRequest) getStack().getContext().get(WebWorkStatics.HTTP_REQUEST);
        ObjectReference ref = new RequestScopeObjectReference(request, REQUEST_CONTAINER);
        return (PicoContainer) ref.get();
    }
    
    private Object createComponentInstance(PicoContainer parentContainer, Class clazz) {
        MutablePicoContainer pico = new DefaultPicoContainer(parentContainer);
        pico.registerComponentImplementation(clazz);
        return pico.getComponentInstance(clazz);
    }
}
